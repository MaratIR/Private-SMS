package com.privatemsg

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

data class StoredMessage(
    val chatId: String,
    val fromPhone: String,
    val body: String,
    val isIncoming: Boolean,
    val timestamp: Long
)

object IncomingMessageStore {

    private const val PREFS_NAME = "privatemsg_messages"
    private const val KEY_MESSAGES = "messages"

    fun add(context: Context, chatId: String, fromPhone: String, body: String, isIncoming: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val list = getMessages(context).toMutableList()
        list.add(
            StoredMessage(
                chatId = chatId,
                fromPhone = fromPhone,
                body = body,
                isIncoming = isIncoming,
                timestamp = System.currentTimeMillis()
            )
        )
        val arr = JSONArray()
        list.forEach { m ->
            arr.put(JSONObject().apply {
                put("chatId", m.chatId)
                put("fromPhone", m.fromPhone)
                put("body", m.body)
                put("isIncoming", m.isIncoming)
                put("timestamp", m.timestamp)
            })
        }
        prefs.edit().putString(KEY_MESSAGES, arr.toString()).apply()
    }

    fun getMessages(context: Context): List<StoredMessage> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_MESSAGES, "[]") ?: return emptyList()
        return try {
            val arr = JSONArray(json)
            List(arr.length()) { i ->
                val o = arr.getJSONObject(i)
                StoredMessage(
                    chatId = o.getString("chatId"),
                    fromPhone = o.getString("fromPhone"),
                    body = o.getString("body"),
                    isIncoming = o.getBoolean("isIncoming"),
                    timestamp = o.getLong("timestamp")
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getMessagesForChat(context: Context, chatId: String): List<StoredMessage> =
        getMessages(context).filter { it.chatId == chatId }.sortedBy { it.timestamp }
}
