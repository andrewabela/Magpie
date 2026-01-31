package page.newlevel.magpie

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.core.content.edit

class SettingsActivity : ComponentActivity() {

    companion object {
        private const val PREFS_NAME = "magpie_preferences"
        private const val KEY_USER_NAME = "user_name"

        fun getUserName(context: Context): String? {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getString(KEY_USER_NAME, null)
        }

        fun setUserName(context: Context, name: String) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit { putString(KEY_USER_NAME, name) }
        }
    }

    // Flag to track if notes were modified (requires UI refresh)
    private var notesModified = false

    private val createFileLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        uri?.let { saveBackupToUri(it) }
    }

    private val openFileLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let { restoreBackupFromUri(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SettingsScreen()
        }
    }

    override fun finish() {
        if (notesModified) {
            setResult(Activity.RESULT_OK)
        } else {
            setResult(Activity.RESULT_CANCELED)
        }
        super.finish()
    }

    @Composable
    private fun SettingsScreen() {
        val context = androidx.compose.ui.platform.LocalContext.current
        var userName by remember { mutableStateOf(getUserName(context) ?: "") }
        var showRestoreDialog by remember { mutableStateOf(false) }
        var showDeleteDialog by remember { mutableStateOf(false) }

        if (showRestoreDialog) {
            AlertDialog(
                onDismissRequest = { showRestoreDialog = false },
                title = { Text("Restore Notes") },
                text = { Text("This will import notes from a backup file. Existing notes will NOT be deleted.") },
                confirmButton = {
                    Button(
                        onClick = {
                            openFileLauncher.launch(arrayOf("application/json"))
                            showRestoreDialog = false
                        }
                    ) { Text("Choose File") }
                },
                dismissButton = {
                    Button(onClick = { showRestoreDialog = false }) { Text("Cancel") }
                }
            )
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete All Notes") },
                text = { Text("Are you sure you want to delete ALL notes? This action cannot be undone!") },
                confirmButton = {
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.danger_setting_btn_bg)),
                        onClick = {
                            performDeleteAllNotes()
                            showDeleteDialog = false
                        }
                    ) { Text("Delete All") }
                },
                dismissButton = {
                    Button(onClick = { showDeleteDialog = false }) { Text("Cancel") }
                }
            )
        }

        SettingsMainScreen(
            userName = userName,
            onUserNameChange = {
                userName = it
                setUserName(context, it)
            },
            onBackup = { CreateAndSaveDBBackup() },
            onRestore = { showRestoreDialog = true },
            onDelete = { showDeleteDialog = true },
            onNavigateBack = { finish() }
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Preview
    @Composable
    private fun SettingsMainScreen(
        modifier: Modifier = Modifier,
        userName: String = "",
        onUserNameChange: (String) -> Unit = {},
        onBackup: () -> Unit = {},
        onRestore: () -> Unit = {},
        onDelete: () -> Unit = {},
        onNavigateBack: () -> Unit = {}
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Settings", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = colorResource(id = R.color.white))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = colorResource(id = R.color.settings_bg),
                        titleContentColor = colorResource(id = R.color.white)
                    )
                )
            },
            containerColor = colorResource(id = R.color.settings_bg)
        ) { paddingValues ->
            Column(
                modifier = modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = userName,
                    onValueChange = onUserNameChange,
                    label = { Text("Your Name") },
                    placeholder = { Text("Enter your name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = colorResource(id = R.color.settings_bg),
                        unfocusedContainerColor = colorResource(id = R.color.settings_bg),
                        focusedTextColor = colorResource(id = R.color.white),
                        unfocusedTextColor = colorResource(id = R.color.white),
                        focusedLabelColor = colorResource(id = R.color.white),
                        unfocusedLabelColor = colorResource(id = R.color.white),
                        focusedPlaceholderColor = colorResource(id = R.color.white).copy(alpha = 0.5f),
                        unfocusedPlaceholderColor = colorResource(id = R.color.white).copy(alpha = 0.5f),
                        cursorColor = colorResource(id = R.color.white)
                    ),
                    singleLine = true
                )

                Button(
                    onClick = onBackup,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.safe_setting_btn_bg),
                        contentColor = colorResource(id = R.color.white)
                    ),
                ) {
                    Text("Backup Notes to File")
                }
                Button(
                    onClick = onRestore,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.danger_setting_btn_bg),
                        contentColor = colorResource(id = R.color.white)
                    ),
                ) {
                    Text("Restore Notes from File")
                }
                Button(
                    onClick = onDelete,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.danger_setting_btn_bg),
                        contentColor = colorResource(id = R.color.white)
                    ),
                ) {
                    Text("Delete All Notes")
                }
            }
        }
    }
    private fun CreateAndSaveDBBackup() {
        val timestamp = SimpleDateFormat("yyyy-MM-dd_HHmmss", Locale.getDefault()).format(Date())
        createFileLauncher.launch("notes_backup_$timestamp.json")
    }

    private fun saveBackupToUri(uri: Uri) {
        try {
            val context = this
            val storage = page.newlevel.magpie.storage.LocalDB(context)

            val jsonData = storage.exportAllNotes()

            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(jsonData.toByteArray())
            }

            Toast.makeText(context, "Backup saved successfully", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Backup failed: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    private fun restoreBackupFromUri(uri: Uri) {
        try {
            val context = this
            val storage = page.newlevel.magpie.storage.LocalDB(context)

            val jsonData = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.readBytes().toString(Charsets.UTF_8)
            } ?: throw Exception("Failed to read file")

            if (jsonData.isEmpty() || !jsonData.trim().startsWith("[")) {
                throw Exception("Invalid backup file format")
            }

            val count = storage.importNotes(jsonData)

            notesModified = true
            Toast.makeText(context, "Restored $count notes successfully", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Restore failed: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    private fun performDeleteAllNotes() {
        try {
            val context = this
            val storage = page.newlevel.magpie.storage.LocalDB(context)

            val count = storage.deleteAllNotes()

            notesModified = true
            Toast.makeText(context, "Deleted $count notes", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Delete failed: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }
}
