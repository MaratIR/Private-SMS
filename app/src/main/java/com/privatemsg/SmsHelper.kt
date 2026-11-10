package com.privatemsg

import android.content.Context
import android.telephony.SmsManager

object SmsHelper {

    fun sendEncryptedSms(context: Context, phoneNumber: String, plainText: String, code: String): Boolean {
        return try {
            val body = CryptoHelper.wrapForSms(plainText, code)
            val sms = SmsManager.getDefault()
            sms.sendTextMessage(phoneNumber, null, body, null, null)
            true
        } catch (e: Exception) {
            false
        }
    }
}
