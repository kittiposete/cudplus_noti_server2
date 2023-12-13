package org.example

import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet

class DatabaseConnection {
    //    private var databasePath = "/home/kittipos/database/cudplus/database.db"
    private var databasePath: String = when {
        System.getProperty("os.name").lowercase().contains("linux") -> "/home/kittipos/database/cudplus/database.db"
        System.getProperty("os.name").lowercase().contains("mac") -> "/Users/kittipos/my_database/cudplus/database.db"
        else -> throw IllegalArgumentException("Platform not supported")
    }
    private val subscriptionTableName = "subscription"
    private val chatDataTableName = "chat_data"
    private var conn: Connection

    init {
        val dbFile = File(databasePath)
        if (!dbFile.exists()) {
            dbFile.parentFile?.mkdirs()   //create parent dirs
            dbFile.createNewFile()
        }
        conn = DriverManager.getConnection("jdbc:sqlite:$databasePath")

        // check it database locked force unlock
        try {
            conn.createStatement().execute("BEGIN IMMEDIATE TRANSACTION")
            // Database is not locked, you can proceed
            conn.createStatement().execute("COMMIT")
        } catch (e: Exception) {
            // Database is locked force unlock
            try {
                conn.createStatement().execute("ROLLBACK")
            } catch (rollbackException: Exception) {
                // Handle the rollback exception if necessary
                rollbackException.printStackTrace()
            }
        }

        // check if table
        val metaData = conn.metaData
        val rs: ResultSet = metaData.getTables(null, null, "%", null)

        var foundSubscriptionTable = false
        var foundChatDataTable = false
        while (rs.next()) {
            val tableName = rs.getString(3)
            if (tableName == subscriptionTableName) {
                foundSubscriptionTable = true
            }
            if (tableName == chatDataTableName) {
                foundChatDataTable = true
            }
        }
        if (!foundSubscriptionTable) {
            conn.createStatement()
                .execute("CREATE TABLE $subscriptionTableName (user text, password text, device_id text)")
        }
        if (!foundChatDataTable) {
            conn.createStatement().execute("CREATE TABLE $chatDataTableName (user text, chat_data text)")
        }
    }

    @Synchronized
    fun getSubscriptionData(): ArrayList<SubscriptionData> {
        val sqlStatement = "SELECT * FROM $subscriptionTableName"
        val preparedStatement = conn.prepareStatement(sqlStatement)
        val rs = preparedStatement.executeQuery()
        val result = ArrayList<SubscriptionData>()
        while (rs.next()) {
            result.add(
                SubscriptionData(
                    rs.getString("user"),
                    rs.getString("password"),
                    rs.getString("device_id")
                )
            )
        }
        return result
    }

    @Synchronized
    fun addSubscription(username: String, password: String, deviceId: String) {
        if (isAlreadySubscribe(username, password, deviceId)) {
            return
        }
        val sqlStatement = "INSERT INTO $subscriptionTableName (user, password, device_id) VALUES (?, ?, ?)"
        val preparedStatement = conn.prepareStatement(sqlStatement)
        preparedStatement.setString(1, username)
        preparedStatement.setString(2, password)
        preparedStatement.setString(3, deviceId)
        preparedStatement.executeUpdate()
    }


    @Synchronized
    fun readChatData(username: String): String? {
        val sqlStatement = "SELECT chat_data FROM $chatDataTableName WHERE user = ?"
        val preparedStatement = conn.prepareStatement(sqlStatement)
        preparedStatement.setString(1, username)
        val rs = preparedStatement.executeQuery()
        return if (rs.next()) {
            rs.getString("chat_data")
        } else {
            null
        }
    }


    @Synchronized
    fun saveChatData(username: String, chatData: String) {
        val oldChatData = readChatData(username)
        if (oldChatData == chatData) {
            return
        }
        if (oldChatData != null) {
            val sqlStatement = "UPDATE $chatDataTableName SET chat_data = ? WHERE user = ?"
            val preparedStatement = conn.prepareStatement(sqlStatement)
            preparedStatement.setString(1, chatData)
            preparedStatement.setString(2, username)
            preparedStatement.executeUpdate()
        } else {
            val sqlStatement = "INSERT INTO $chatDataTableName (user, chat_data) VALUES (?, ?)"
            val preparedStatement = conn.prepareStatement(sqlStatement)
            preparedStatement.setString(1, username)
            preparedStatement.setString(2, chatData)
            preparedStatement.executeUpdate()
        }
    }


    @Synchronized
    fun isAlreadySubscribe(username: String, password: String, deviceId: String): Boolean {
        // check is have data like this in subscription table
        val sqlStatement = "SELECT * FROM $subscriptionTableName WHERE user = ? AND password = ? AND device_id = ?"
        val preparedStatement = conn.prepareStatement(sqlStatement)
        preparedStatement.setString(1, username)
        preparedStatement.setString(2, password)
        preparedStatement.setString(3, deviceId)
        val rs = preparedStatement.executeQuery()

        return rs.next()
    }

    fun close() {
        conn.close()
    }
}

data class SubscriptionData(
    val username: String,
    val password: String,
    val deviceId: String
)