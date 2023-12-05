import datetime

from bs4 import BeautifulSoup
# from pyvirtualdisplay import Display
from selenium import webdriver
from selenium.webdriver.chrome.webdriver import WebDriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.ui import WebDriverWait

import chat_message_item as chat_message_item

driver: WebDriver = None


def init():
    global driver
    # display = Display(size=(800, 600))
    # display.start()

    option = webdriver.ChromeOptions()
    option.add_argument("--headless")
    option.add_argument('--disable-gpu')
    option.add_argument("--incognito")
    driver = webdriver.Chrome(options=option)

    # delete all cookie
    driver.delete_all_cookies()


def login_with_username(username, password) -> str:
    """
    :param username: the username to log in with
    :param password: the password to log in with
    :return: the result of the login process (success or failure)
    """
    # remove cookie
    driver.delete_all_cookies()
    driver.get("https://cudplus.onsmart.school/login")
    driver.refresh()

    time_out = datetime.datetime.now() + datetime.timedelta(seconds=20)
    while True:
        try:
            go_to_login = WebDriverWait(driver, 15).until(
                EC.presence_of_element_located(
                    (By.XPATH, "/html/body/div/div/div[1]/div[2]/form/div/div/button"))
            )
            go_to_login.click()
            break
        except:
            if time_out < datetime.datetime.now():
                return "login failed because URL cannot click go to login"
            driver.refresh()

    time_out = datetime.datetime.now() + datetime.timedelta(seconds=15)
    while True:
        current_url = driver.current_url
        if current_url == "https://www.mycourseville.com/api/login":
            break
        else:
            if time_out < datetime.datetime.now():
                return "login failed because URL not change to https://www.mycourseville.com/api/login"

    time_out = datetime.datetime.now() + datetime.timedelta(seconds=25)
    while True:
        try:
            username_field = WebDriverWait(driver, 10).until(
                EC.presence_of_element_located(
                    (By.XPATH, "/html/body/div[2]/main/div[1]/div/div[2]/div/form/div[2]/input"))
            )
            password_field = WebDriverWait(driver, 10).until(
                EC.presence_of_element_located(
                    (By.XPATH, "/html/body/div[2]/main/div[1]/div/div[2]/div/form/div[3]/input"))
            )
            login_button = WebDriverWait(driver, 10).until(
                EC.presence_of_element_located(
                    (By.XPATH, "/html/body/div[2]/main/div[1]/div/div[2]/div/form/div[5]/button"))
            )
            username_field.send_keys(username)
            password_field.send_keys(password)
            login_button.click()
            break
        except:
            if time_out < datetime.datetime.now():
                return ("login failed because cannot find "
                        "username_field, password_field, login_button")
            driver.refresh()
            continue

    # check for password wrong
    try:
        driver.find_element(by=By.CLASS_NAME, value="invalid-feedback")
        return "login failed because password wrong"
    except:
        pass

    time_out = datetime.datetime.now() + datetime.timedelta(seconds=15)
    while True:
        try:
            current_url = driver.current_url
        except:
            continue
        if current_url == "https://cudplus.onsmart.school/home":
            break
        elif datetime.datetime.now() < time_out:
            driver.refresh()
        elif current_url == "https://www.mycourseville.com/api/login":
            return "login failed because it not change url"
        elif current_url != "https://cudplus.onsmart.school/home":
            return "login failed unknown error"

    global display_name

    display_name = WebDriverWait(driver, 15).until(
        EC.presence_of_element_located(
            (By.XPATH, "/html/body/div[2]/div[3]/div[2]/div[1]/div/div/div/div[1]"))
    ).text

    return "success"


def read_notifications(length):
    driver.get("https://cudplus.onsmart.school/utility/notifications")
    for i in range(length // 17):
        # wait for button display
        load_more_button = WebDriverWait(driver, 20).until(
            EC.presence_of_element_located(
                (By.XPATH, "/html/body/div[1]/div[3]/div[2]/div[3]/div/section/div/div/div/button"))
        )
        load_more_button.click()

    # get all notifications
    all_notifications = driver.find_elements(by=By.XPATH,
                                             value="/html/body/div[1]/div[3]/div[2]/div["
                                                   "3]/div/section/div/ul")

    # for loop item in ul with bs4
    # convent to bs4 object
    soup = BeautifulSoup(all_notifications[0].get_attribute("innerHTML"), "html.parser")

    output = []
    # for loop all li
    for li in soup.find_all("li"):
        text = []
        # find media-body px-2 align-self-center
        frame = li.find_all("div", {"class": "media-body px-2 align-self-center"})[0]

        # for loop every thing in frame
        for item in frame:
            # find all text
            text.append(item.text)
        text.pop()

        output.append("".join(text))

    # remove last item until length = length
    while len(output) > length:
        output.pop()

    return output


def get_chat_date(load_all_message: bool = False):
    def count_message():
        chat_list = driver.find_elements(by=By.XPATH,
                                         value="/html/body/div[1]/div[3]/div[2]/div["
                                               "2]/div/div/section/div/div/div[1]/ul")[0]

        # get all chat item
        all_chat_items = chat_list.find_elements(by=By.TAG_NAME, value="li")

        size = len(all_chat_items)
        return size

    def read_message():
        message_items = []
        # get all chat
        chat_list = driver.find_elements(by=By.XPATH,
                                         value="/html/body/div[1]/div[3]/div[2]/div["
                                               "2]/div/div/section/div/div/div[1]/ul")[
            0]

        # get all chat item
        all_chat_items = chat_list.find_elements(by=By.TAG_NAME, value="li")

        chat_room = driver.find_element(by=By.XPATH,
                                        value="/html/body/div[1]/div[3]/div[2]/div["
                                              "2]/div/div/section/div/div/header/div/div").text

        # for message in all_chat_items:
        for chat_item in all_chat_items:
            # check class
            if "ss-left" in chat_item.get_attribute("class"):
                message = chat_item.find_element(by=By.CLASS_NAME,
                                                 value="ss-chat-item-bubble-main.p-3.ss-rounded"
                                                       ".ss-color-gray.ss-light")
                message_content = ""
                try:
                    m = message.find_elements(by=By.TAG_NAME,
                                              value="span")
                    for i in m:
                        message_content += i.text
                except:
                    message_content = message.find_element(by=By.TAG_NAME,
                                                           value="a").text

                sender_name = message.find_element(by=By.CLASS_NAME,
                                                   value="mt-2.ss-font-smallest.ss-font-light.ss"
                                                         "-chat-item-creator").text

                message_item = chat_message_item.ChatMessageItem(sender_name, chat_room,
                                                                 message_content)
                message_items.append(message_item)
            elif "ss-right" in chat_item.get_attribute("class"):
                sender_name = display_name
                message_content = chat_item.find_element(by=By.CLASS_NAME,
                                                         value="ss-chat-item-bubble-main.p-3.ss"
                                                               "-rounded.ss-color-0.ss-dim").text
                message_item = chat_message_item.ChatMessageItem(sender_name, chat_room,
                                                                 message_content)
                message_items.append(message_item)
            else:
                raise Exception("class not found")

        return message_items

    driver.get("https://cudplus.onsmart.school/communication/chatrooms")
    driver.refresh()

    # load all message
    if load_all_message:
        while True:
            try:
                load_more_button = WebDriverWait(driver, 5).until(
                    EC.presence_of_element_located(
                        (By.XPATH,
                         "/html/body/div[1]/div[3]/div[2]/div[3]/div/div/section/footer/button"))
                )
            except:
                break
            load_more_button.click()

    # load with webdriverwait
    timeout = datetime.datetime.now() + datetime.timedelta(seconds=20)

    chat_hrefs = []
    while True:
        all_chat = WebDriverWait(driver, 20).until(
            EC.presence_of_element_located(
                (By.XPATH, "/html/body/div[1]/div[3]/div[2]/div[3]/div/div/section/div/ul"))
        )
        li_element = all_chat.find_elements(by=By.TAG_NAME, value="li")
        try:
            for li in li_element:
                a_tag = li.find_element(by=By.CLASS_NAME, value="media.ss-color-0.ss-no-deco.p-3")
                href = a_tag.get_attribute("href")

                chat_hrefs.append(href)
            break
        except:
            if timeout < datetime.datetime.now():
                raise Exception("timeout")

    message_list = []
    # for loop all chatroom
    for href in chat_hrefs:
        driver.get(href)

        is_first_load = True
        prevent_chat_room_message_length = -1
        # check for load more button
        if load_all_message:
            while True:
                try:
                    load_more_button = driver.find_element(by=By.XPATH,
                                                           value="/html/body/div[1]/div[3]/div["
                                                                 "2]/div["
                                                                 "2]/div/div/section/div/div/div["
                                                                 "1]/div/button")
                    prevent_chat_room_message_length = count_message()
                    load_more_button.click()
                    is_first_load = False
                except:
                    # check is first load and have message then break
                    if is_first_load:
                        if count_message() > 0:
                            # reconfirm again
                            try:
                                driver.find_element(by=By.XPATH,
                                                    value="/html/body/div[1]/div[3]/div[2]/div["
                                                          "2]/div/div/section/div/div/div["
                                                          "1]/div/button")
                            except:
                                break
                    else:
                        # this line mean current wait for load more button
                        # if message is all check again one time and break
                        current_message_length = count_message()
                        if current_message_length > prevent_chat_room_message_length:
                            # check for button again
                            try:
                                driver.find_element(by=By.XPATH,
                                                    value="/html/body/div[1]/div[3]/div[2]/div["
                                                          "2]/div/div/section/div/div/div["
                                                          "1]/div/button")
                            except:
                                break
        else:
            # check for message length
            while True:
                if count_message() > 0:
                    break
        start_time = datetime.datetime.now()
        message_list += read_message()
        end_time = datetime.datetime.now()
        # print("read message time: {} ms".format((end_time - start_time).microseconds / 1000))
        driver.back()
    return message_list


# def logout():
#     driver.get("https://cudplus.onsmart.school/home")
#
#     print("current url: {}".format(driver.current_url))
#     # if driver.current_url == "https://cudplus.onsmart.school/login":
#     #     WebDriverWait(driver, 15).until(
#     #         EC.presence_of_element_located(
#     #             (By.XPATH, "/html/body/div/div/div[1]/div[2]/form/div/div/button"))
#     #     ).click()
#     #     print("current url2: {}".format(driver.current_url))
#     #     if driver.current_url != "https://www.mycourseville.com/api/login":
#     #         return
#
#     open_menu_button = WebDriverWait(driver, 15).until(EC.element_to_be_clickable(
#     (By.XPATH, "/html/body/div[2]/div[3]/div[1]/button")))
#     # open_menu_button = driver.find_element(by=By.ID, value="ss-app-sidebar-control")
#     open_menu_button.click()
#     logout_button = driver.find_element(by=By.XPATH,
#                                         value="/html/body/div[2]/div[1]/ul/li[12]/a")
#     time_out = datetime.datetime.now() + datetime.timedelta(seconds=10)
#     while True:
#         try:
#             logout_button.click()
#             break
#         except:
#             if time_out < datetime.datetime.now():
#                 raise Exception("cannot click logout button")
#
#     WebDriverWait(driver, 15).until(
#         EC.presence_of_element_located(
#             (By.XPATH, "/html/body/div/div/div[1]/div[2]/form/div/div/button"))
#     )


def close():
    driver.close()
