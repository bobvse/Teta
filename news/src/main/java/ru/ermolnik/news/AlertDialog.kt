package ru.ermolnik.news

import androidx.compose.foundation.layout.Column
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
fun ShowDialog(
    retryConnectAction: () -> Unit,
    description: String,
) {
    MaterialTheme {
        Column {
            val openDialog = remember { mutableStateOf(false) }
            if (openDialog.value) {
                AlertDialog(
                    onDismissRequest = {
                        // Dismiss the dialog when the user clicks outside the dialog or on the back
                        // button. If you want to disable that functionality, simply use an empty
                        // onCloseRequest.
                        openDialog.value = false
                    },
                    title = {
                        Text(text = "Ошибка соединения")
                    },
                    text = {
                        Text(description)
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                openDialog.value = false
                                retryConnectAction.invoke()
                            }) {
                            Text("Повторить попытку")
                        }
                    },
                    dismissButton = {
                        Button(

                            onClick = {
                                openDialog.value = false
                            }) {
                            Text("Отмена")
                        }
                    }
                )
            }
        }
    }
}