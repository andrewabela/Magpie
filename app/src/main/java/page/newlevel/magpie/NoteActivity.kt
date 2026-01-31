package page.newlevel.magpie
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.MutableState

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
                Scaffold(
                    containerColor = colorResource(id = R.color.note_page_bg),
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
                ) { innerPadding ->
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
    val chunksState = remember { mutableStateOf<List<TextOrUrl>>(emptyList()) }

    LaunchedEffect(Unit) {
        chunksState.value = splitTextAndUrls(bodyState.value, emptyList())
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom))
    ) {
        item {
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
                onValueChange = { newValue: String ->
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
        }

        // Content chunks with stable keys - no rerendering on typing
        itemsIndexed(
            items = chunksState.value,
            key = { index, chunk ->
                when (chunk) {
                    is TextObj -> "text_$index"
                    is UrlObj -> "url_${chunk.url.value}_$index"
                }
            }
        ) { _, chunk ->
            when (chunk) {
                is TextObj -> {
                    BasicTextField(
                        value = chunk.text.value,
                        onValueChange = { newValue: String ->
                            chunk.text.value = newValue
                            val fullText = chunksState.value.joinToString("") {
                                when (it) {
                                    is TextObj -> it.text.value
                                    is UrlObj -> it.url.value
                                }
                            }
                            note.editContent(fullText)
                            bodyState.value = fullText
                        },
                        textStyle = TextStyle(
                            fontFamily = lexendDeca,
                            fontSize = 22.sp,
                            lineHeight = 28.sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Light
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp)
                    )
                }
                is UrlObj -> {
                    WebLink(
                        url = chunk.url.value,
                        delFun = {
                            chunk.url.value = ""
                            val fullText = chunksState.value.joinToString("") {
                                when (it) {
                                    is TextObj -> it.text.value
                                    is UrlObj -> it.url.value
                                }
                            }
                            note.editContent(fullText)
                            bodyState.value = fullText

                            // remove this chunk
                            chunksState.value = splitTextAndUrls(fullText, chunksState.value)
                        },
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp)
                    )
                }
            }
        }
    }
}

private fun splitTextAndUrls(text: String, existingChunks: List<TextOrUrl> = emptyList()): List<TextOrUrl> {
    val regex = "(https?://[\\w./?=&%-]+)".toRegex()
    val chunks = mutableListOf<TextOrUrl>()
    val matches = regex.findAll(text).toList()
    var lastIndex = 0
    var existingIndex = 0

    matches.forEach { match ->
        // Text before match
        if (match.range.first > lastIndex) {
            val startText = text.substring(lastIndex, match.range.first)

            // Try to reuse existing TextObj if the content is the same
            val existingTextObj = existingChunks.getOrNull(existingIndex) as? TextObj
            if (existingTextObj != null && existingTextObj.text.value == startText) {
                chunks.add(existingTextObj)
            } else {
                chunks.add(TextObj(mutableStateOf(startText)))
            }
            existingIndex++
        }

        // The URL
        val url = match.value

        // Try to reuse existing UrlObj if the URL is the same
        val existingUrlObj = existingChunks.getOrNull(existingIndex) as? UrlObj
        if (existingUrlObj != null && existingUrlObj.url.value == url) {
            chunks.add(existingUrlObj)
        } else {
            chunks.add(UrlObj(mutableStateOf(url)))
        }
        existingIndex++
        lastIndex = match.range.last + 1
    }

    // Remaining text
    if (lastIndex < text.length) {
        val endText = text.substring(lastIndex)

        // Try to reuse existing TextObj if the content is the same
        val existingTextObj = existingChunks.getOrNull(existingIndex) as? TextObj
        if (existingTextObj != null && existingTextObj.text.value == endText) {
            chunks.add(existingTextObj)
        } else {
            chunks.add(TextObj(mutableStateOf(endText)))
        }
    }

    // Ensure at least one empty text if empty
    if (chunks.isEmpty()) {
        val existingTextObj = existingChunks.firstOrNull() as? TextObj
        if (existingTextObj != null && existingTextObj.text.value.isEmpty()) {
            chunks.add(existingTextObj)
        } else {
            chunks.add(TextObj(mutableStateOf("")))
        }
    } else if (chunks.first() is UrlObj) {
        chunks.add(0, TextObj(mutableStateOf("")))
    }

    // If the last chunk is a URL, add an empty text at the end for typing
    if (chunks.isNotEmpty() && chunks.last() is UrlObj) {
        // Try to reuse existing empty text at the end
            chunks.add(TextObj(mutableStateOf("")))
    }

    return chunks
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


sealed class TextOrUrl
data class TextObj(val text: MutableState<String>) : TextOrUrl()
data class UrlObj(val url: MutableState<String>) : TextOrUrl()
