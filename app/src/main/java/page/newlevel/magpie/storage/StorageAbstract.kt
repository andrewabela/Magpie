package page.newlevel.notes.storage

abstract class StorageAbstract {

    abstract fun listNotes(
        offset: Int,
        limit: Int
    ): Pair<List<Note>, Boolean>

    abstract fun createNote(): Note

//    abstract fun getNote(uuid: String): Note

}