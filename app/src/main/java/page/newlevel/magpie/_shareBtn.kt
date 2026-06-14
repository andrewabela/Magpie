package page.newlevel.magpie

import android.content.Intent
import android.widget.ImageView
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
internal fun ShareBtn(note: Note, modifier: Modifier = Modifier) {
    AndroidView(
        factory = { ctx ->
            ImageView(ctx).apply {
                setImageResource(R.drawable.share)
                setPadding(28, 31, 28 , 25)
                setOnClickListener {
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, note.getContent())
                        putExtra(Intent.EXTRA_SUBJECT, note.getTitle())
                        type = "text/plain"
                    }

                    val shareIntent = Intent.createChooser(sendIntent, null)
                    ctx.startActivity(shareIntent)
                    }
            }
        },
        modifier = modifier
            .padding(10.dp, 10.dp, 10.dp, 0.dp)
            .background(
                color = colorResource(R.color.semi_transparent),
                shape = CircleShape
            )
    )
}
