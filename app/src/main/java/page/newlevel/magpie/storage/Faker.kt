package page.newlevel.magpie.storage
import page.newlevel.notes.storage.StorageAbstract

class Faker : StorageAbstract() {
    // Hardcoded dummy data for testing purposes
    private val dummyNotes = listOf(
        "Note 1" to "Content of Note 1",
        "Note 2" to "Content of Note 2",
        "Note 3" to "Content of Note 3",
        "Note 4" to "Content of Note 4",
        "Note 5" to "Content of Note 5"
    )

    override fun listNotes(offset: Int, limit: Int): Pair<List<page.newlevel.notes.storage.Note>, Boolean> {
        val notes = dummyNotes.drop(offset).take(limit).mapIndexed { index, (title, content) ->
            page.newlevel.notes.storage.Note(
                uuid = "uuid-$index",
                getTitle = { title },
                getContent = { content },
                editContent = { _ -> },
                rm = { }
            )
        }
        val hasMore = offset + limit < dummyNotes.size
        return Pair(notes, hasMore)
    }

    override fun createNote(note: page.newlevel.notes.storage.Note) {
        // No-op for faker
    }


}