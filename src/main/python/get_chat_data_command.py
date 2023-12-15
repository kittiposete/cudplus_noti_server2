import json
import sys

import bot_worker

username = sys.argv[1]
password = sys.argv[2]


def throw_error():
    print("{\"status\":\"error\"}")


def throw_password_wrong():
    print("{\"status\":\"password wrong\"}")


# bot init
try:
    bot_worker.init()
except Exception as e:
    try:
        bot_worker.close()
    except Exception as e:
        pass
    throw_error()
    sys.exit()

# login
try:
    login_result = bot_worker.login_with_username(username=username, password=password)
    if login_result == "login failed because password wrong":
        try:
            bot_worker.close()
        except Exception as e:
            pass
        throw_password_wrong()
        sys.exit()
    elif login_result != "success":
        try:
            bot_worker.close()
        except Exception as e:
            pass
        throw_error()
        sys.exit()
except Exception as e:
    try:
        bot_worker.close()
    except Exception as e:
        pass
    throw_error()
    sys.exit()

# get chat data
chat_str = ""
try:
    chat_object = bot_worker.get_chat_date()
    for i in chat_object:
        chat_str += i.toString() + ",\n"

except Exception as e:
    try:
        bot_worker.close()
    except Exception as e:
        pass
    throw_error()
    sys.exit()

# close bot
try:
    bot_worker.close()
except Exception as e:
    throw_error()
    sys.exit()

# convent to json
status = "success"
json_object = {}
json_object["status"] = status
json_object["chat"] = chat_str
json_str = json.dumps(json_object)

# print result
for c in json_str:
    print(c)
exit(0)
