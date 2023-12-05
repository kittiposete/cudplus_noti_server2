package org.example

import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit

class BotAdapter {
    // get src folder path
    private val checkUsernameAndPasswordPy = "src/main/python/check_username_and_password_command.py"
    private val getChatDataPy = "src/main/python/get_chat_data_command.py"
    private val interpreterPath = "src/main/python/venv/bin/python3"


    fun checkUsernameAndPassword(username: String, password: String): BotResult {
        val proc = ProcessBuilder(interpreterPath, checkUsernameAndPasswordPy, username, password).start()

        proc.waitFor(600, TimeUnit.SECONDS)

        val output = BufferedReader(InputStreamReader(proc.inputStream)).readText().trim()

        val errorOutput = BufferedReader(InputStreamReader(proc.errorStream)).readText().trim()
        if (errorOutput.isNotEmpty()) {
            println("Error: $errorOutput")
        }
        return when (output) {
            "true" -> {
                BotResult.SUCCESS
            }

            "false" -> {
                BotResult.WRONG_USERNAME_OR_PASSWORD
            }

            else -> {
                BotResult.UNKNOWN_ERROR
            }
        }
    }

    fun getChatData(username: String, password: String): GetChatDataResult {
        println("getChatData")
        val proc = ProcessBuilder(interpreterPath, getChatDataPy, username, password).start()

        proc.waitFor(1200, TimeUnit.SECONDS)

        val output = BufferedReader(InputStreamReader(proc.inputStream)).readText().trim()

        val errorOutput = BufferedReader(InputStreamReader(proc.errorStream)).readText().trim()
        if (errorOutput.isNotEmpty()) {
            println("Error: $errorOutput")
            return GetChatDataResult(null, BotResult.UNKNOWN_ERROR)
        }

        println("output: $output")

        // output is json string, convent it to hashmap
        Gson().fromJson(output, HashMap::class.java).let {
            val status = it["status"] as? String
            when (status) {
                "success" -> {
                    return GetChatDataResult(it["data"] as? String, BotResult.SUCCESS)
                }

                "password wrong" -> {
                    return GetChatDataResult(null, BotResult.WRONG_USERNAME_OR_PASSWORD)
                }

                else -> {
                    return GetChatDataResult(null, BotResult.UNKNOWN_ERROR)
                }
            }
        }
    }
}

class GetChatDataResult(val chatData: String?, val status: BotResult)

enum class BotResult {
    SUCCESS,
    WRONG_USERNAME_OR_PASSWORD,
    UNKNOWN_ERROR
}