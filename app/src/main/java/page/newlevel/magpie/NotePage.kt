package page.newlevel.magpie
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import page.newlevel.magpie.ui.theme.MagpieTheme
import page.newlevel.notes.storage.Note


//val lexendDeca = FontFamily(
//    Font(R.font.lexend_deca_extralight, androidx.compose.ui.text.font.FontWeight.ExtraLight),
//    Font(R.font.lexend_deca_light, androidx.compose.ui.text.font.FontWeight.Light),
//    Font(R.font.lexend_deca_normal, androidx.compose.ui.text.font.FontWeight.Normal),
//    Font(R.font.lexend_deca_medium, androidx.compose.ui.text.font.FontWeight.Medium),
//    Font(R.font.lexend_deca_semibold, androidx.compose.ui.text.font.FontWeight.SemiBold)
//)


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
    val title = note.getTitle()
    val tldr = note.getTLDR()
    val body = note.getContent()
    Column {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {
        BackBtn()
            // Title
        BasicTextField(
            value = title,
            onValueChange = { /* Handle text change */ },
            modifier = Modifier.padding(bottom = 20.dp),
            textStyle = TextStyle(fontSize = 24.sp)
        )
            //TLDR
        Text(
            text = tldr,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 28.dp)
        )
            // Content
        BasicTextField(
            value = body,
            onValueChange = { /* Handle text change */ },
            textStyle = TextStyle(fontSize = 12.sp),
            modifier = Modifier.fillMaxSize()
        )
    }
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