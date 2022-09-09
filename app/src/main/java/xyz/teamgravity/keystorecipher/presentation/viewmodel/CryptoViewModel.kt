package xyz.teamgravity.keystorecipher.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import xyz.teamgravity.keystorecipher.core.util.CryptoManager
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class CryptoViewModel @Inject constructor(
    private val handle: SavedStateHandle,
    private val crypto: CryptoManager,
) : ViewModel() {

    companion object {
        private const val DECRYPTED_MESSAGE = "decrypted_message"
        private const val DEFAULT_DECRYPTED_MESSAGE = ""

        private const val ENCRYPTED_MESSAGE = "encrypted_message"
        private const val DEFAULT_ENCRYPTED_MESSAGE = ""

        private const val ENCRYPTED_FILE_NAME = "secret.txt"
    }

    var decryptedMessage: String by mutableStateOf(handle.get<String>(DECRYPTED_MESSAGE) ?: DEFAULT_DECRYPTED_MESSAGE)
        private set

    var encryptedMessage: String by mutableStateOf(handle.get<String>(ENCRYPTED_MESSAGE) ?: DEFAULT_ENCRYPTED_MESSAGE)
        private set

    private fun onEncryptedMessageChange(value: String) {
        handle[ENCRYPTED_MESSAGE] = value
        encryptedMessage = value
    }

    ///////////////////////////////////////////////////////////////////////////
    // API
    ///////////////////////////////////////////////////////////////////////////

    fun onDecryptedMessageChange(value: String) {
        handle[DECRYPTED_MESSAGE] = value
        decryptedMessage = value
    }

    fun onDecrypt(filesDir: File) {
        val encryptedFile = File(filesDir, ENCRYPTED_FILE_NAME)
        if (encryptedFile.exists()) onDecryptedMessageChange(crypto.decrypt(FileInputStream(encryptedFile)).decodeToString())
    }

    fun onEncrypt(filesDir: File) {
        val bytes = decryptedMessage.encodeToByteArray()
        val encryptedFile = File(filesDir, ENCRYPTED_FILE_NAME)
        if (!encryptedFile.exists()) encryptedFile.createNewFile()
        onEncryptedMessageChange(crypto.encrypt(bytes, FileOutputStream(encryptedFile)).decodeToString())
    }
}