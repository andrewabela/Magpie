package page.newlevel.magpie.storage

import android.content.Context
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import page.newlevel.magpie.db.Database
import page.newlevel.notes.storage.Note
import page.newlevel.notes.storage.StorageAbstract

class LocalDB(context: Context) : StorageAbstract() {
    private val database = Database(AndroidSqliteDriver(Database.Schema, context, "notes.db"))

    override fun listNotes(
        offset: Int,
        limit: Int
    ): Pair<List<Note>, Boolean> {
        // Query one extra record to determine if there are more results
        val dbNotes = database.setupdbQueries.listNotes((limit + 1).toLong(), offset.toLong()).executeAsList()

        // hasMore is true only if we got more records than the limit
        val hasMore = dbNotes.size > limit

        // Return only the requested limit of records
        val notesToReturn = dbNotes.take(limit).map { dbNote ->
            Note(
                uuid = dbNote.id.toString(),
                getTitle = { dbNote.title },
                getContent = { dbNote.content },
                editContent = { content ->
                    database.setupdbQueries.updateContent(content, dbNote.id)
                },
                rm = { database.setupdbQueries.deleteNoteById(dbNote.id) }
            )
        }
        return Pair(notesToReturn, hasMore)
    }

    override fun createNote(note: Note) {
        database.setupdbQueries.insertNote(note.getTitle(), note.getContent())
    }
}