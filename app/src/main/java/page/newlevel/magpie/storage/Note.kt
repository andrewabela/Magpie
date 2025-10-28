package page.newlevel.notes.storage

data class Note(
    val uuid: String,
    val getTitle: () -> String,
    val getContent: () -> String,
    val editContent: (content: String) -> Unit,
    val isFavorite: () -> Boolean = { false },
    val setFavorite: (favorite: Boolean) -> Unit = {},
    val rm: () -> Unit,
    val getTLDR: () -> String = {
        val content = getContent()
        if (content.length <= 10) {
            content
        } else {
            content.substring(0, 10) + "...."
        }
    }
)


