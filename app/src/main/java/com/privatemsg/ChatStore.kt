package com.privatemsg

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

data class Chat(
    val id: String,
    val phoneNumber: String,
    val displayName: String?,
    val code: String
)

class ChatStore(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getChats(): List<Chat> {
        val json = prefs.getString(KEY_CHATS, "[]") ?: return emptyList()
        return try {
            val arr = JSONArray(json)
            List(arr.length()) { i ->
                val o = arr.getJSONObject(i)
                Chat(
                    id = o.getString("id"),
                    phoneNumber = o.getString("phone"),
                    displayName = o.optString("name").takeIf { it.isNotEmpty() },
                    code = o.getString("code")
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun addChat(chat: Chat) {
        val list = getChats().toMutableList()
        if (list.any { it.id == chat.id }) return
        list.add(chat)
        saveChats(list)
    }

    fun getChatById(id: String): Chat? = getChats().find { it.id == id }

    fun getChatByPhone(phone: String): Chat? =
        getChats().find { normalizePhone(it.phoneNumber) == normalizePhone(phone) }

    fun saveChats(chats: List<Chat>) {
        val arr = JSONArray()
        chats.forEach { c ->
            arr.put(JSONObject().apply {
                put("id", c.id)
                put("phone", c.phoneNumber)
                put("name", c.displayName ?: "")
                put("code", c.code)
            })
        }
        prefs.edit().putString(KEY_CHATS, arr.toString()).commit()
    }

    companion object {
        private const val PREFS_NAME = "privatemsg_chats"
        private const val KEY_CHATS = "chats"

        fun normalizePhone(s: String): String = s.filter { it.isDigit() }
    }
}
