package page.newlevel.notes.storage

import com.google.mlkit.genai.summarization.Summarization
import com.google.mlkit.genai.summarization.SummarizationRequest
import com.google.mlkit.genai.summarization.SummarizerOptions

data class Note(
    val uuid: String,
    val getTitle: () -> String,
    val getContent: () -> String,
    val editContent: (content: String) -> Unit,
    val editTitle: (title: String) -> Unit = {},
    val isFavorite: () -> Boolean = { false },
    val setFavorite: (favorite: Boolean) -> Unit = {},
    val rm: () -> Unit
)
