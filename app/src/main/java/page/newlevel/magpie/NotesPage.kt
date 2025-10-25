package page.newlevel.magpie

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import page.newlevel.notes.storage.Note
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.ui.viewinterop.AndroidView
import page.newlevel.magpie.R

@Composable
internal fun MainScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {

        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()).systemBarsPadding()
        ) {
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
private fun Note(note: Note, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.padding(1.dp).height(215.dp),
        shape = RoundedCornerShape(38.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Blue)
    ) {
        FavoriteBtn(
            note = note
        )
        Text(text = "Note id: ${note.uuid}, title: ${note.getTitle()}")
    }
}

@Composable
private fun NotesListScreen() {
    val (allNotes, _) = page.newlevel.magpie.storage.Faker().listNotes(0, 100)

    Column (
        modifier = Modifier.padding(1.dp)
    ) {
        allNotes.chunked(2).forEach { pair ->
            Row {
                pair.forEach { note ->
                    Note(note = note, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}


@Composable
private fun FavoriteBtn(note: Note) {
    val isFavoriteState = androidx.compose.runtime.remember {
        androidx.compose.runtime.mutableStateOf(note.isFavorite())
    }
    val isFavorite = isFavoriteState.value

        AndroidView(
            factory = { ctx ->
                android.widget.ImageView(ctx).apply {
                    setPadding(32, 32, 32, 32)
                    val fav = isFavoriteState.value
                    setImageResource(if (fav) R.drawable.heart_empty else R.drawable.heart_smile)
                    setOnClickListener {
                        val newValue = !isFavoriteState.value
                        note.setFavorite(newValue)
                        isFavoriteState.value = newValue
                        setImageResource(if (newValue) R.drawable.heart_empty else R.drawable.heart_smile)
                    }
                }
            },
            modifier = Modifier
                .padding(4.dp)
        )
}