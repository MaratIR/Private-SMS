package com.privatemsg

import android.util.Base64
import java.security.spec.KeySpec
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

/**
 * Шифрование/дешифрование для формата prvtsnd:[ciphertext].
 * Код (пароль) используется для получения AES-ключа через PBKDF2.
 */
object CryptoHelper {

    const val PREFIX = "prvtsnd:"
    private const val ALGORITHM = "AES/CBC/PKCS5Padding"
    private const val KEY_ALGORITHM = "PBKDF2WithHmacSHA256"
    private const val SALT = "privatemsg_salt_v1"
    private const val ITERATIONS = 65536
    private const val KEY_SIZE = 256

    fun encrypt(plainText: String, code: String): String {
        val key = deriveKey(code)
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val iv = cipher.iv
        val encrypted = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        val combined = iv + encrypted
        return Base64.encodeToString(combined, Base64.NO_WRAP)
    }

    fun decrypt(cipherText: String, code: String): String? {
        return try {
            val key = deriveKey(code)
            val combined = Base64.decode(cipherText, Base64.NO_WRAP)
            if (combined.size < 17) return null
            val iv = combined.copyOfRange(0, 16)
            val encrypted = combined.copyOfRange(16, combined.size)
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(iv))
            String(cipher.doFinal(encrypted), Charsets.UTF_8)
        } catch (e: Exception) {
            null
        }
    }

    fun wrapForSms(plainText: String, code: String): String =
        PREFIX + encrypt(plainText, code)

    fun unwrapFromSms(smsBody: String, code: String): String? {
        if (!smsBody.startsWith(PREFIX)) return null
        val payload = smsBody.removePrefix(PREFIX).trim()
        return decrypt(payload, code)
    }

    fun isPrivateMessage(smsBody: String): Boolean = smsBody.startsWith(PREFIX)

    private fun deriveKey(code: String): SecretKeySpec {
        val factory = SecretKeyFactory.getInstance(KEY_ALGORITHM)
        val spec: KeySpec = PBEKeySpec(
            code.toCharArray(),
            SALT.toByteArray(Charsets.UTF_8),
            ITERATIONS,
            KEY_SIZE
        )
        val tmp = factory.generateSecret(spec)
        return SecretKeySpec(tmp.encoded, "AES")
    }
}
