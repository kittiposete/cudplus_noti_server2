package org.example

import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet

class DatabaseConnection {
    private val databasePath = "/home/kittipos/database/cudplus/database.db"
    private val subscriptionTableName = "subscription"
    private val chatDataTableName = "chat_data"
    private var conn: Connection

    init {
        if (!File(databasePath).exists()) {
            File(databasePath).createNewFile()
        }
        conn = DriverManager.getConnection("jdbc:sqlite:$databasePath")
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
                .execute("CREATE TABLE {subscription_table_name} (user text, password text, device_id text)")
        }
        if (!foundChatDataTable) {
            conn.createStatement().execute("CREATE TABLE {chat_data_table_name} (user text, chat_data text)")
        }
    }

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