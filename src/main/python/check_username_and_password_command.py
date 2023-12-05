import sys

import bot_worker

username = sys.argv[1]
password = sys.argv[2]

# bot init
try:
    bot_worker.init()
except Exception as e:
    print("error")
    print(e)
    try:
        bot_worker.close()
    except Exception as e:
        pass
    sys.exit()

# login
try:
    login_result = bot_worker.login_with_username(username=username, password=password)
except Exception as e:
    login_result = "error"
    print(e)
    try:
        bot_worker.close()
    except Exception as e:
        pass
    sys.exit()

# close bot
bot_worker.close()

# print result
if login_result == "success":
    print("true")
elif login_result == "login failed because password wrong":
    print("false")
else:
    print("error")
