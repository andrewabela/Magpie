package page.newlevel.magpie.storage
import page.newlevel.notes.storage.StorageAbstract

class Faker : StorageAbstract() {
    // Hardcoded dummy data for testing purposes
    private val dummyNotes = listOf(
        "Note one two three four five six seven eight nine ten eleven twelve thirteen fourteen fifteen sixteen seventeen eighteen nineteen twenty" to "Content of Note one two three four five six seven eight nine ten eleven twelve thirteen fourteen fifteen sixteen seventeen eighteen nineteen twenty",
        "Note 2" to "Content of Note 2",
        "Note 3" to "Content of Note 3",
        "Note 4" to "Content of Note 4",
        "Note 5" to "Content of Note 5",
        "Note 6" to "Content of Note 6",
        "Note 7" to "Content of Note 7",
        "Note 8" to "Content of Note 8",
        "Note 9" to "Content of Note 9",
        "Note 10" to "Content of Note 10"
    )

    override fun listNotes(offset: Int, limit: Int): Pair<List<page.newlevel.notes.storage.Note>, Boolean> {
        val notes = dummyNotes.drop(offset).take(limit).mapIndexed { index, (title, content) ->
            page.newlevel.notes.storage.Note(
                uuid = "uuid-$index",
                getTitle = lambda@{
                    println("Getting title for note $index")
                    return@lambda title
                },
                getContent = lambda@{
                    println("Getting content for note $index")
                    return@lambda content
                },
                editContent = { newContent: String ->
                    println("Editing content for note $index to: $newContent")
                },

                isFavorite = lambda@{
                    println("Checking if note $index is favorite")
                    return@lambda kotlin.random.Random.nextBoolean()
                },

                setFavorite = { favorite: Boolean ->
                    println("Setting favorite status for note $index to: $favorite")
                },
                rm = {
                    println("Removing note $index")
                }
            )
        }
        val hasMore = offset + limit < dummyNotes.size
        return Pair(notes, hasMore)
    }

    override fun createNote(note: page.newlevel.notes.storage.Note) {
        // No-op for faker
    }


}