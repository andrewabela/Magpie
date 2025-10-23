package page.newlevel.magpie

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import page.newlevel.notes.storage.Note

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AppContent()
        }
    }
}

@Composable
private fun AppContent() {
    MaterialTheme{
        Surface {
            MainScreen()
        }
    }
}

@Composable
private fun MainScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        contentAlignment = Alignment.TopCenter
    ) {

        Column {
            Greeting(name = "Android ${android.os.Build.VERSION.RELEASE} (API ${android.os.Build.VERSION.SDK_INT})")
            Greeting(name = "Android ${android.os.Build.VERSION.RELEASE} (API ${android.os.Build.VERSION.SDK_INT})")
            NotesListScreen()
        }
    }
}


@Composable
private fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Composable
private fun Note(note: Note) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Blue)
    ) {
        Text(text = "Note id: ${note.uuid}, title: ${note.getTitle()}")
    }
}

@Composable
private fun NotesListScreen() {
    Note(
        note = Note(
            uuid = "1234",
            getTitle = { "abc" },
            getContent = { "Abcd" },
            editContent = { /* no-op */ },
            rm = { /* no-op */ }
        )
    )
    Note(
        note = Note(
            uuid = "2345",
            getTitle = { "Note 2" },
            getContent = { "AaBbCc" },
            editContent = { /* no-op */ },
            rm = { /* no-op */ }
        )
    )
}