package com.privatemsg

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.privatemsg.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private var chat: Chat? = null
    private lateinit var adapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val chatId = intent.getStringExtra(EXTRA_CHAT_ID) ?: run {
            finish()
            return
        }
        chat = ChatStore(this).getChatById(chatId)
        if (chat == null) {
            finish()
            return
        }
        title = chat!!.displayName ?: chat!!.phoneNumber

        val messages = IncomingMessageStore.getMessagesForChat(this, chatId)
        adapter = MessageAdapter(messages)
        binding.chatRecycler.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        binding.chatRecycler.adapter = adapter

        binding.chatSend.setOnClickListener { sendMessage() }
        binding.chatInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEND) {
                sendMessage()
                true
            } else false
        }
    }

    override fun onResume() {
        super.onResume()
        chat?.id?.let { id ->
            adapter.updateMessages(IncomingMessageStore.getMessagesForChat(this, id))
            val count = adapter.itemCount
            if (count > 0) {
                binding.chatRecycler.smoothScrollToPosition(count - 1)
            }
        }
    }

    private fun sendMessage() {
        val c = chat ?: return
        val text = binding.chatInput.text?.toString()?.trim() ?: return
        if (text.isEmpty()) return

        val ok = SmsHelper.sendEncryptedSms(this, c.phoneNumber, text, c.code)
        if (ok) {
            IncomingMessageStore.add(this, c.id, c.phoneNumber, text, false)
            binding.chatInput.text?.clear()
            adapter.updateMessages(IncomingMessageStore.getMessagesForChat(this, c.id))
            val count = adapter.itemCount
            if (count > 0) {
                binding.chatRecycler.smoothScrollToPosition(count - 1)
            }
        } else {
            Toast.makeText(this, "Ошибка отправки SMS", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val EXTRA_CHAT_ID = "chat_id"
    }
}
