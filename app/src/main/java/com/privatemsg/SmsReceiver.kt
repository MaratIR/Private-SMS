package com.privatemsg

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.telephony.SmsMessage

/**
 * Принимает входящие SMS. Если тело начинается с prvtsnd: — пробуем расшифровать
 * кодом из чата с этим номером и сохраняем расшифрованное сообщение в ContentProvider
 * (или просто помечаем для отображения в чате). Здесь только парсинг и вызов расшифровки;
 * сохранение в чат — через ContentResolver или свой провайдер/БД.
 */
class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return
        val store = ChatStore(context)
        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent) ?: return
        for (smsMessage in messages) {
            val from = smsMessage.displayOriginatingAddress ?: continue
            val body = smsMessage.displayMessageBody ?: continue
            if (!CryptoHelper.isPrivateMessage(body)) continue
            val chat = store.getChatByPhone(from)
            if (chat == null) continue
            val decrypted = CryptoHelper.unwrapFromSms(body, chat.code) ?: continue
            IncomingMessageStore.add(context, chat.id, from, decrypted, true)
        }
    }
}
