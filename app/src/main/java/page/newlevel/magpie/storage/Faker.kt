package page.newlevel.magpie.storage
import page.newlevel.notes.storage.Note
import page.newlevel.notes.storage.StorageAbstract

class Faker : StorageAbstract() {
    // Hardcoded dummy data for testing purposes
    private val dummyNotes = listOf(
        "Note one two three four five six seven eight nine ten eleven twelve thirteen fourteen fifteen sixteen seventeen eighteen nineteen twenty" to "Content of Note one two three four five six seven eight nine ten\neleven twelve thirteen fourteen fifteen sixteen seventeen\neighteen\nnineteen\n\ntwenty",
        "Note 2" to "Content of Note 2",
        "Note 3" to "Content of Note 3",
        "Note 4" to "Content of Note 4",
        "Note 5" to "Content of Note 5",
        "Note 6" to "Content of Note 6",
        "Note 7" to "Content of Note 7",
        "Note 8" to "Content of Note 8",
        "Note 9" to "Content of Note 9",
        "Note 10" to "Content of Note 10",
        "Lorem ipsum dolor sit." to "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec vitae ante sem. Aliquam egestas leo ac dolor pretium, eu scelerisque sem interdum. Suspendisse ante dui, sagittis at fringilla et, blandit quis lectus. In viverra pulvinar tortor, vel finibus purus. Vestibulum hendrerit est a sapien eleifend, vel vestibulum metus vestibulum. Fusce pellentesque enim sapien, et volutpat nisl pharetra vitae. Nunc sagittis luctus nibh, in molestie nunc consequat in. Etiam dignissim dui in interdum dapibus. In dictum molestie purus in pretium. Nam congue mi ipsum, vel efficitur magna malesuada ut. Aenean tempus lorem et egestas lacinia. Maecenas tempus hendrerit suscipit. Donec congue urna at est efficitur fringilla.\n"+
                "Ut eget quam eget odio ornare iaculis. Fusce lacinia sollicitudin sapien ac pharetra. Duis sed diam nisi. Proin consectetur purus justo, et efficitur nulla viverra ut. Ut in dolor in nulla tincidunt blandit id non felis. Maecenas placerat ac mauris pretium blandit. Pellentesque ut congue eros. Nulla malesuada tellus non metus ornare placerat. Nam blandit feugiat sem ut feugiat. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Aliquam fringilla, quam non tempor efficitur, ex enim tristique erat, et efficitur ex justo et magna. Sed sit amet nulla elit. Aenean dictum, mi in consequat condimentum, urna elit pharetra dui, quis accumsan libero tellus vel tellus. Sed vehicula lectus eget libero lacinia congue. Quisque a massa tortor. Cras at metus non leo laoreet hendrerit.\n"+
                "Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. In pellentesque turpis quis ligula iaculis, a malesuada lacus ornare. Suspendisse in mauris et lorem accumsan fringilla. Quisque non tortor vitae magna placerat vulputate nec et odio. Maecenas iaculis venenatis elit sit amet tempor. In hac habitasse platea dictumst. Morbi finibus egestas faucibus. Etiam in enim eget felis aliquet sagittis a eu justo. Quisque auctor eu nibh vitae mollis. Fusce eget eleifend leo. Praesent cursus, lorem ut pellentesque scelerisque, risus sapien faucibus purus, eget venenatis nisl nisl elementum odio. Pellentesque semper purus vitae volutpat mollis. Curabitur sit amet nibh at risus pulvinar mollis.\n"+
                "Suspendisse vestibulum eros nisi. Donec ut augue sed arcu egestas ultrices sed et diam. Pellentesque vitae dictum orci, et posuere eros. Phasellus sapien enim, sodales in nunc sit amet, sollicitudin hendrerit augue. Proin accumsan dui justo, quis aliquet nibh imperdiet id. Nunc dignissim nisl hendrerit, molestie nunc ac, eleifend neque. Nulla eu eleifend ante. Sed lorem est, lacinia vitae dictum tincidunt, convallis eget leo. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae; Morbi non urna non elit accumsan suscipit. Aenean eget dolor semper, sodales nunc eu, congue massa. Vestibulum gravida purus eget quam commodo, nec ultricies felis euismod. Etiam gravida et magna eu tincidunt. Pellentesque molestie urna ac posuere pulvinar. Suspendisse efficitur leo vitae ipsum aliquam aliquam. Fusce eget interdum elit. ",
        "AAAAAAAAAAAAAAAAllllllllllllllllllllllllllllllllllllll" to "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
        "Note 13" to "Content of Note 13"
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

    override fun createNote(): Note {
        val note : Note = Note(
            uuid = "new-note-uuid",
            getTitle = { "New" },
            getContent = { "Aa Bb Cc Dd 1 2 3 4" },
            editContent = { content: String ->
                println("Editing content of new note to: $content")
            },
            rm = {
                println("Removing new note")
            }
        )
        println("Creating note: $note")
        return note
    }


}