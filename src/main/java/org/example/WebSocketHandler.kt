package org.example

import com.google.gson.Gson
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer

class WebSocketHandler : WebSocketServer() {

/*    fun message(session: Session?, message: String?) {
        println("message: $message")
        if (message == null || session == null) {
            return
        }
        val messageJson = Gson().fromJson(message, HashMap::class.java)
        val method = RequestMethod.fromString(messageJson["method"] as String)

        when (method) {
            RequestMethod.SUBSCRIBE -> {
                val username = messageJson["username"] as? String
                val password = messageJson["password"] as? String
                val deviceId = messageJson["device_id"] as? String
                if (username == null || password == null || deviceId == null) {
                    session.remote.sendString(ServerResult.FAIL.toString())
                    return
                } else if (username.isEmpty() || password.isEmpty() || deviceId.isEmpty()) {
                    session.remote.sendString(ServerResult.FAIL.toString())
                    return
                }

                if (DatabaseConnection().isAlreadySubscribe(username, password, deviceId)) {
                    session.remote.sendString(ServerResult.SUCCESS.toString())
                    return
                }

                if (BotAdapter().checkUsernameAndPassword(username, password) == BotResult.SUCCESS) {
                    session.remote.sendString(ServerResult.SUCCESS.toString())
                    DatabaseConnection().addSubscription(username, password, deviceId)
                    return
                } else {
                    session.remote.sendString(ServerResult.FAIL.toString())
                    return
                }
            }

            RequestMethod.CHECK_SUBSCRIBE -> {
                val username = messageJson["username"] as? String
                val password = messageJson["password"] as? String
                val deviceId = messageJson["device_id"] as? String
                if (username == null || password == null || deviceId == null) {
                    session.remote.sendString(ServerResult.FAIL.toString())
                    return
                } else if (username.isEmpty() || password.isEmpty() || deviceId.isEmpty()) {
                    session.remote.sendString(ServerResult.FAIL.toString())
                    return
                }

                if (DatabaseConnection().isAlreadySubscribe(username, password, deviceId)) {
                    session.remote.sendString(ServerResult.TRUE.toString())
                    return
                } else {
                    session.remote.sendString(ServerResult.FALSE.toString())
                    return
                }
            }
        }
    }*/

    init {
        println("WebSocketHandler init")
    }

    override fun onMessage(socketConnection: WebSocket?, message: String?) {
        println("message: $message")
        if (message == null || socketConnection == null) {
            return
        }
        val messageJson = Gson().fromJson(message, HashMap::class.java)
        val method = RequestMethod.fromString(messageJson["method"] as String)

        when (method) {
            RequestMethod.SUBSCRIBE -> {
                val username = messageJson["username"] as? String
                val password = messageJson["password"] as? String
                val deviceId = messageJson["device_id"] as? String
                if (username == null || password == null || deviceId == null) {
//                    socketConnection.remote.sendString(ServerResult.FAIL.toString())
                    socketConnection.send(ServerResult.FAIL.toString())
                    return
                } else if (username.isEmpty() || password.isEmpty() || deviceId.isEmpty()) {
//                    session.remote.sendString(ServerResult.FAIL.toString())
                    socketConnection.send(ServerResult.FAIL.toString())
                    return
                }

                if (DatabaseConnection().isAlreadySubscribe(username, password, deviceId)) {
//                    session.remote.sendString(ServerResult.SUCCESS.toString())
                    socketConnection.send(ServerResult.SUCCESS.toString())
                    return
                }

                if (BotAdapter().checkUsernameAndPassword(username, password) == BotResult.SUCCESS) {
//                    session.remote.sendString(ServerResult.SUCCESS.toString())
                    socketConnection.send(ServerResult.SUCCESS.toString())
                    DatabaseConnection().addSubscription(username, password, deviceId)
                    return
                } else {
//                    session.remote.sendString(ServerResult.FAIL.toString())
                    socketConnection.send(ServerResult.FAIL.toString())
                    return
                }
            }

            RequestMethod.CHECK_SUBSCRIBE -> {
                val username = messageJson["username"] as? String
                val password = messageJson["password"] as? String
                val deviceId = messageJson["device_id"] as? String
                if (username == null || password == null || deviceId == null) {
//                    session.remote.sendString(ServerResult.FAIL.toString())
                    socketConnection.send(ServerResult.FAIL.toString())
                    return
                } else if (username.isEmpty() || password.isEmpty() || deviceId.isEmpty()) {
//                    session.remote.sendString(ServerResult.FAIL.toString())
                    socketConnection.send(ServerResult.FAIL.toString())
                    return
                }

                if (DatabaseConnection().isAlreadySubscribe(username, password, deviceId)) {
//                    session.remote.sendString(ServerResult.TRUE.toString())
                    socketConnection.send(ServerResult.TRUE.toString())
                    return
                } else {
//                    session.remote.sendString(ServerResult.FALSE.toString())
                    socketConnection.send(ServerResult.FALSE.toString())
                    return
                }
            }
        }    }

    override fun onOpen(connection: WebSocket?, incomingHandshake: ClientHandshake?) {
        println("onOpen $connection")
    }

    override fun onClose(p0: WebSocket?, p1: Int, p2: String?, p3: Boolean) {
        TODO("Not yet implemented")
    }


    override fun onError(p0: WebSocket?, p1: java.lang.Exception?) {
        TODO("Not yet implemented")
    }

    override fun onStart() {
        TODO("Not yet implemented")
    }
}
enum class ServerResult {
    SUCCESS, FAIL, TRUE, FALSE;

    override fun toString(): String {
        return when (this) {
            SUCCESS -> {
                "success"
            }

            FAIL -> {
                "fail"
            }

            TRUE -> {
                "True"
            }

            FALSE -> {
                "False"
            }
        }
    }
}

enum class RequestMethod {
    SUBSCRIBE, CHECK_SUBSCRIBE;

    companion object {
        fun fromString(methodText: String): RequestMethod {
            return when (methodText) {
                "subscribe" -> {
                    SUBSCRIBE
                }

                "check_subscribe" -> {
                    CHECK_SUBSCRIBE
                }

                else -> {
                    throw Exception("Unknown method: $methodText")
                }
            }
        }
    }

    override fun toString(): String {
        return when (this) {
            SUBSCRIBE -> {
                "SUBSCRIBE"
            }

            CHECK_SUBSCRIBE -> {
                "CHECK_SUBSCRIBE"
            }
        }
    }
}