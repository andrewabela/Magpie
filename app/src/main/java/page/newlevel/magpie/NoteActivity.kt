package page.newlevel.magpie
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import page.newlevel.magpie.ui.theme.MagpieTheme
import page.newlevel.notes.storage.Note
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.layout.Spacer

class NotePage : ComponentActivity() {
    companion object {
        var currentNote: Note? = null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val note = currentNote
        if (note == null) {
            throw Exception("No note provided to NotePage")
        }
        setContent {
            MagpieTheme {
                Scaffold(containerColor = colorResource(id = R.color.note_page_bg), modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NoteMainScreen(
                        note = note,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
internal fun NoteMainScreen(note: Note, modifier: Modifier = Modifier) {
    val titleState = remember { mutableStateOf(note.getTitle()) }
    val bodyState = remember { mutableStateOf(note.getContent()) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BackBtn()
            // add padding around the favorite button
            FavoriteBtn(note = note, modifier = Modifier.padding(0.dp, 5.dp, 0.dp, 0.dp))
        }
        // Title
        BasicTextField(
            value = titleState.value,
            onValueChange = { newValue ->
                titleState.value = newValue
                note.editTitle(newValue)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 20.dp),
            textStyle = TextStyle(
                fontFamily = lexendDeca,
                fontSize = 36.sp,
                lineHeight = 40.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
            )
        )
        // Content
        BasicTextField(
            value = bodyState.value,
            onValueChange = { newValue ->
                bodyState.value = newValue
                note.editContent(newValue)
            },
            textStyle = TextStyle(
                fontFamily = lexendDeca,
                fontSize = 22.sp,
                lineHeight = 28.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Light
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 32.dp)
        )
    }
}

@Composable
private fun BackBtn(){
    AndroidView(
        factory = { ctx ->
            android.widget.ImageView(ctx).apply {
                setPadding(38, 38, 38, 38)
                setImageResource( R.drawable.back )
                setBackground(GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(ctx.getColor(R.color.back_btn_icon_bg))
                })
                setOnClickListener {
                    (ctx as ComponentActivity).finish()
                }
            }
        },
        modifier = Modifier.padding(10.dp)
    )
}