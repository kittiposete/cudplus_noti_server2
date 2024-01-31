package org.example

class SendNotificationService {
    private fun logging(message: String) {
        println("(Send Notification Service): $message")
    }

    fun run(databaseConnection: DatabaseConnection) {
        println("Send Notification Service is running")
        while (true) {
            val subscriptionData = databaseConnection.getSubscriptionData()
            for ((username, password, deviceId) in subscriptionData) {
                logging("Checking for $username")
                val botAdapter = BotAdapter()

                val newChatData = botAdapter.getChatData(username, password)
                logging("(Bot Service): finished getting chat data")
                val oldChatData = databaseConnection.readChatData(username)
                logging("finished reading chat data")
                if (newChatData.status != BotResult.SUCCESS) {
                    println("Error: " + newChatData.status)
                    continue
                }
                val newChatDataString = newChatData.chatData
                val resultStatus = newChatData.status
                if (resultStatus != BotResult.SUCCESS) {
                    println("Error: $resultStatus")
                } else {
                    if (oldChatData == null) {
                        assert(newChatDataString != null)
                        println("username: $username")
                        println("new chat data: $newChatDataString")
                        databaseConnection.saveChatData(username, newChatDataString!!)
                        continue
                    }
                    println("old chat data: $oldChatData")
                    println("new chat data: $newChatDataString")
                    if (oldChatData != newChatDataString) {
                        val title = "New Chat"
                        val message = "new chat"
                        try {
                            PushNotification().pushNotification(title, message, deviceId)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        // save to a database
                        databaseConnection.saveChatData(username, newChatDataString!!)
                    } else {
                        println("No new chat")
                    }
                }
            }
        }
    }
}
