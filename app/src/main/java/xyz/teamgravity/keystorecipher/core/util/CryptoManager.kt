package xyz.teamgravity.keystorecipher.core.util

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.io.InputStream
import java.io.OutputStream
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class CryptoManager(
    private val keystore: KeyStore,
) {

    companion object {
        private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
        private const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
        private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
        private const val KEYSTORE_ALIAS = "secret"
    }

    private val encryptCipher = Cipher.getInstance(TRANSFORMATION).apply {
        init(Cipher.ENCRYPT_MODE, getKey())
    }

    private fun decryptCipher(vector: ByteArray): Cipher {
        return Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, getKey(), IvParameterSpec(vector))
        }
    }

    private fun getKey(): SecretKey {
        val key = keystore.getEntry(KEYSTORE_ALIAS, null) as? KeyStore.SecretKeyEntry
        return key?.secretKey ?: createKey()
    }

    private fun createKey(): SecretKey {
        return KeyGenerator.getInstance(ALGORITHM).apply {
            init(
                KeyGenParameterSpec.Builder(KEYSTORE_ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(BLOCK_MODE)
                    .setEncryptionPaddings(PADDING)
                    .setUserAuthenticationRequired(false)
                    .setRandomizedEncryptionRequired(true)
                    .build()
            )
        }.generateKey()
    }

    ///////////////////////////////////////////////////////////////////////////
    // API
    ///////////////////////////////////////////////////////////////////////////

    fun encrypt(data: ByteArray, output: OutputStream): ByteArray {
        val encryptedData = encryptCipher.doFinal(data)
        output.use { stream ->
            stream.write(encryptCipher.iv.size)
            stream.write(encryptCipher.iv)
            stream.write(encryptedData.size)
            stream.write(encryptedData)
        }
        return encryptedData
    }

    fun decrypt(input: InputStream): ByteArray {
        return input.use { stream ->
            val vectorSize = stream.read()
            val vector = ByteArray(vectorSize)
            stream.read(vector)

            val encryptedDataSize = stream.read()
            val encryptedData = ByteArray(encryptedDataSize)
            stream.read(encryptedData)

            decryptCipher(vector).doFinal(encryptedData)
        }
    }
}