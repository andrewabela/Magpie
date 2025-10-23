package page.newlevel.notes.storage

data class Note(
    val uuid: String,
    val getTitle: () -> String,
    val getContent: () -> String,
    val editContent: (content: String) -> Unit,
    val rm: () -> Unit
)
