package xyz.teamgravity.keystorecipher.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import xyz.teamgravity.keystorecipher.R
import xyz.teamgravity.keystorecipher.presentation.viewmodel.CryptoViewModel

@Composable
fun CryptoScreen(
    viewmodel: CryptoViewModel = hiltViewModel(),
) {

    val context = LocalContext.current

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        TextField(
            value = viewmodel.decryptedMessage,
            onValueChange = viewmodel::onDecryptedMessageChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = stringResource(id = R.string.encrypt_string)) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = { viewmodel.onEncrypt(context.filesDir) }) {
                Text(text = stringResource(id = R.string.encrypt))
            }
            Button(onClick = { viewmodel.onDecrypt(context.filesDir) }) {
                Text(text = stringResource(id = R.string.decrypt))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = viewmodel.encryptedMessage)
    }
}