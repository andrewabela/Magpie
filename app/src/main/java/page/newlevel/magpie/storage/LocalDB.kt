package page.newlevel.magpie.storage

import android.content.Context
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import page.newlevel.magpie.db.Database
import page.newlevel.notes.storage.Note
import page.newlevel.notes.storage.StorageAbstract
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
            val noteId = dbNote.id
            Note(
                uuid = noteId.toString(),
                getTitle = { dbNote.title },
                getContent = { dbNote.content },
                editContent = { content ->
                    database.setupdbQueries.updateContent(content, noteId)
                },
                editTitle = { title ->
                    database.setupdbQueries.updateTitle(title, noteId)
                },
                isFavorite = {
                    database.setupdbQueries.getNoteById(noteId).executeAsOne().is_favorite != 0L
                },
                setFavorite = { favorite ->
                    database.setupdbQueries.updateFavorite(if (favorite) 1L else 0L, noteId)
                },
                rm = { database.setupdbQueries.deleteNoteById(noteId) }
            )
        }
        return Pair(notesToReturn, hasMore)
    }

    override fun createNote(): Note {
        val title = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault()).format(Date())
        database.setupdbQueries.insertNote(title, "")

        val dbNote = database.setupdbQueries.getLastInsertedNote().executeAsOne()
        val noteId = dbNote.id

        return Note(
            uuid = noteId.toString(),
            getTitle = {
                database.setupdbQueries.getNoteById(noteId).executeAsOne().title
            },
            getContent = {
                database.setupdbQueries.getNoteById(noteId).executeAsOne().content
            },
            editContent = { content ->
                database.setupdbQueries.updateContent(content, noteId)
            },
            editTitle = { title ->
                database.setupdbQueries.updateTitle(title, noteId)
            },
            isFavorite = {
                database.setupdbQueries.getNoteById(noteId).executeAsOne().is_favorite != 0L
            },
            setFavorite = { favorite ->
                database.setupdbQueries.updateFavorite(if (favorite) 1L else 0L, noteId)
            },
            rm = { database.setupdbQueries.deleteNoteById(noteId) }
        )
    }

    override fun exportAllNotes(): String {
        val allNotes = database.setupdbQueries.getAllNotes().executeAsList()
        val jsonArray = org.json.JSONArray()

        for (note in allNotes) {
            val noteObj = org.json.JSONObject()
            noteObj.put("title", note.title)
            noteObj.put("content", note.content)
            noteObj.put("is_favorite", note.is_favorite)
            noteObj.put("created_at", note.created_at)
            noteObj.put("edited_at", note.edited_at)
            jsonArray.put(noteObj)
        }

        return jsonArray.toString(2)
    }

    override fun importNotes(jsonData: String): Int {
        val jsonArray = org.json.JSONArray(jsonData)
        var count = 0

        for (i in 0 until jsonArray.length()) {
            try {
                val noteObj = jsonArray.getJSONObject(i)
                val title = noteObj.getString("title")
                val content = noteObj.getString("content")
                val isFavorite = noteObj.optLong("is_favorite", 0L)

                // Insert note
                database.setupdbQueries.insertNote(title, content)

                // Update favorite status if needed
                if (isFavorite != 0L) {
                    val lastNote = database.setupdbQueries.getLastInsertedNote().executeAsOne()
                    database.setupdbQueries.updateFavorite(isFavorite, lastNote.id)
                }

                count++
            } catch (e: Exception) {
                e.printStackTrace()
                // Continue with next note even if one fails
            }
        }

        return count
    }

    override fun deleteAllNotes(): Int {
        val (notes, _) = listNotes(0, Int.MAX_VALUE)
        val count = notes.size
        database.setupdbQueries.deleteAllNotes()
        return count
    }

}