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
                Row {
                    FavoriteBtn(note = note, modifier = Modifier.padding(0.dp, 5.dp, 0.dp, 0.dp))
                    ShareBtn(note = note, modifier = Modifier.padding(0.dp, 5.dp, 0.dp, 0.dp))
                }
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
        ) { index, chunk ->
            when (chunk) {
                is TextObj -> {
                    BasicTextField(
                        value = chunk.text.value,
                        onValueChange = { newValue: String ->
                            // If this chunk directly follows a URL, it must keep its
                            // leading line break so typed text isn't ingested into the URL
                            val followsUrl = chunksState.value.getOrNull(index - 1) is UrlObj
                            chunk.text.value = if (followsUrl && !newValue.startsWith("\n")) {
                                "\n$newValue"
                            } else {
                                newValue
                            }
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
    val regex = "(https?://(?:(?!https?://)[\\w./?=&%-])+)".toRegex()
    val chunks = mutableListOf<TextOrUrl>()
    val matches = regex.findAll(text).toList()
    var lastIndex = 0
    var existingIndex = 0

    matches.forEachIndexed { matchIndex, match ->
        // Text before match
        if (match.range.first > lastIndex) {
            var startText = text.substring(lastIndex, match.range.first)

            // If this text immediately follows a previous URL, ensure it starts
            // with a line break so it doesn't get re-absorbed into that URL
            if (matchIndex > 0 && !startText.startsWith("\n")) {
                startText = "\n$startText"
            }

            // Try to reuse existing TextObj if the content is the same
            val existingTextObj = existingChunks.getOrNull(existingIndex) as? TextObj
            if (existingTextObj != null && existingTextObj.text.value == startText) {
                chunks.add(existingTextObj)
            } else {
                chunks.add(TextObj(mutableStateOf(startText)))
            }
            existingIndex++
        } else if (matchIndex > 0) {
            // Two URLs with nothing between them - insert a line break so the
            // user has a place to type that won't be absorbed into either URL
            val existingTextObj = existingChunks.getOrNull(existingIndex) as? TextObj
            if (existingTextObj != null && existingTextObj.text.value == "\n") {
                chunks.add(existingTextObj)
            } else {
                chunks.add(TextObj(mutableStateOf("\n")))
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
        var endText = text.substring(lastIndex)

        // If this text immediately follows a URL, ensure it starts with a line
        // break so it doesn't get re-absorbed into that URL
        if (matches.isNotEmpty() && !endText.startsWith("\n")) {
            endText = "\n$endText"
        }

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

    // If the last chunk is a URL, add a text chunk starting with a line break
    // at the end so the user has a place to type that won't be absorbed into the URL
    if (chunks.isNotEmpty() && chunks.last() is UrlObj) {
        chunks.add(TextObj(mutableStateOf("\n")))
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
