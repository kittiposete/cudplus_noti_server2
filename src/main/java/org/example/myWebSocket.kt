package org.example

import com.google.gson.Gson
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage
import org.eclipse.jetty.websocket.api.annotations.WebSocket

@WebSocket
class myWebSocket {
    @OnWebSocketMessage
    fun message(session: Session?, message: String?) {
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


            }

            RequestMethod.CHECK_SUBSCRIBE -> {
            }
        }
    }
}

enum class ServerResult {
    SUCCESS, FAIL;

    override fun toString(): String {
        return when (this) {
            SUCCESS -> {
                "success"
            }

            FAIL -> {
                "fail"
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