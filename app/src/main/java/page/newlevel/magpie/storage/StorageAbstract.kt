package page.newlevel.notes.storage

abstract class StorageAbstract {

    abstract fun listNotes(
        offset: Int,
        limit: Int
    ): Pair<List<Note>, Boolean>

    abstract fun createNote(): Note

    abstract fun exportAllNotes(): String // Returns JSON string of all notes

    abstract fun importNotes(jsonData: String): Int // Returns number of imported notes

    abstract fun deleteAllNotes(): Int // Returns number of deleted notes

//    abstract fun getNote(uuid: String): Note

}