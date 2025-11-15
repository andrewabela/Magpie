package page.newlevel.magpie

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import page.newlevel.notes.storage.Note

@Composable
internal fun FavoriteBtn(note: Note, modifier: Modifier = Modifier) {
    // Use note instance as key so state refreshes when note object changes
    val isFavoriteState = androidx.compose.runtime.remember(note) {
        androidx.compose.runtime.mutableStateOf(note.isFavorite())
    }

    AndroidView(
        factory = { ctx ->
            android.widget.ImageView(ctx).apply {
                val fav = isFavoriteState.value
                setImageResource(if (fav) R.drawable.heart_smile else R.drawable.heart_empty)
                setPadding(if (fav) 90 else 28, 31, if (fav) 28 else 90, 25)
                setOnClickListener {
                    val newValue = !isFavoriteState.value
                    note.setFavorite(newValue)
                    isFavoriteState.value = newValue
                    setImageResource(if (newValue) R.drawable.heart_smile else R.drawable.heart_empty)
                    setPadding(if (newValue) 90 else 28, 31, if (newValue) 28 else 90, 25)
                }
            }
        },
        update = { imageView ->
            // Update the view when state changes
            val fav = isFavoriteState.value
            imageView.setImageResource(if (fav) R.drawable.heart_smile else R.drawable.heart_empty)
            imageView.setPadding(if (fav) 90 else 28, 31, if (fav) 28 else 90, 25)
        },
        modifier = modifier
            .padding(10.dp, 10.dp, 10.dp, 0.dp)
            .background(
                color = colorResource(R.color.semi_transparent),
                shape = CircleShape
            )
    )
}