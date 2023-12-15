package org.example

import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader
import java.math.BigInteger
import java.util.concurrent.TimeUnit

class BotAdapter {
    // get src folder path
    private val checkUsernameAndPasswordPy = "src/main/python/check_username_and_password_command.py"
    private val getChatDataPy = "src/main/python/get_chat_data_command.py"

    private var interpreterPath: String = when {
        System.getProperty("os.name").lowercase().contains("linux") -> "src/main/python/venv/bin/python3"
        System.getProperty("os.name").lowercase().contains("mac") -> "src/main/python/venv_macos/bin/python3"
        else -> throw IllegalArgumentException("Platform not supported")
    }


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

    private fun logging(message: String) = println("[BotAdapter] $message")

    fun getChatData(username: String, password: String): GetChatDataResult {
        logging("getChatData")
        val proc = ProcessBuilder(interpreterPath, getChatDataPy, username, password).start()
        logging("start bot process")


//        val startTime = System.currentTimeMillis()
//
//        while (true) {
//            try {
//                proc.exitValue()
//                break
//            } catch (e: IllegalThreadStateException) {
//                Thread.sleep(1000)
//            }
//        }
//
//        val endTime = System.currentTimeMillis()
//        // log the time
//        logging("bot is alive for ${endTime - startTime} ms")


//        logging("bot is finished")

        val reader = BufferedReader(InputStreamReader(proc.inputStream))
        val chars = mutableListOf<Char>()
        var char: Int = reader.read()
        chars.add(char.toChar())
        var count: BigInteger = BigInteger.ZERO
        while (char != -1) {
//            count++
            count = count.add(BigInteger.ONE)
            char = reader.read()
//            if (count % 2 == 0L) {
            if (count.mod(BigInteger.valueOf(2)) == BigInteger.ZERO) {
                chars.add(char.toChar())
            }
        }
        val output = chars.joinToString("").trim().dropLast(1)
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