package com.privatemsg

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.privatemsg.databinding.ActivitySetupBinding

class SetupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetupBinding
    private var selectedPhone: String? = null
    private var selectedName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnPickContact.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
            startActivityForResult(intent, REQ_PICK_CONTACT)
        }

        binding.btnSaveSetup.setOnClickListener {
            val code = binding.setupCode.text?.toString()?.trim()
            if (selectedPhone.isNullOrBlank()) {
                Toast.makeText(this, "Выберите контакт", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (code.isNullOrBlank()) {
                Toast.makeText(this, "Введите код для расшифровки", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val store = ChatStore(this)
            val chat = Chat(
                id = "chat_${System.currentTimeMillis()}_${selectedPhone!!.hashCode()}",
                phoneNumber = selectedPhone!!,
                displayName = selectedName,
                code = code
            )
            store.addChat(chat)
            Toast.makeText(this, "Чат создан", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, ChatActivity::class.java).apply {
                putExtra(ChatActivity.EXTRA_CHAT_ID, chat.id)
            })
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != REQ_PICK_CONTACT || resultCode != RESULT_OK || data == null) return
        val uri = data.data ?: return
        val cursor = contentResolver.query(
            uri!!,
            arrayOf(ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME),
            null, null, null
        ) ?: return
        if (!cursor.moveToFirst()) {
            cursor.close()
            return
        }
        val contactId = cursor.getString(0) ?: run {
            cursor.close()
            Toast.makeText(this, "Не удалось получить контакт", Toast.LENGTH_SHORT).show()
            return
        }
        selectedName = cursor.getString(1)
        cursor.close()

        val phoneCursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
            "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID}=?",
            arrayOf(contactId),
            null
        ) ?: return
        if (phoneCursor.moveToFirst()) {
            selectedPhone = phoneCursor.getString(0)?.replace(" ", "")?.replace("-", "")?.trim()
            if (!selectedPhone.isNullOrBlank()) {
                binding.setupContactDisplay.visibility = android.view.View.VISIBLE
                binding.setupContactDisplay.text = "${selectedName ?: ""} $selectedPhone"
                Toast.makeText(this, "Контакт выбран. Введите код и нажмите «Сохранить»", Toast.LENGTH_SHORT).show()
            } else {
                selectedPhone = null
                Toast.makeText(this, "У контакта нет номера телефона", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "У контакта нет номера телефона", Toast.LENGTH_SHORT).show()
        }
        phoneCursor.close()
    }

    companion object {
        private const val REQ_PICK_CONTACT = 200
    }
}
