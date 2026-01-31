package page.newlevel.magpie.storage

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import org.junit.runner.RunWith
import org.json.JSONArray
import org.json.JSONObject

@RunWith(AndroidJUnit4::class)
class FakerTest {

    private lateinit var faker: Faker

    @Before
    fun setup() {
        faker = Faker()
    }

    @Test
    fun listNotes_returnsCorrectNumberOfNotes() {
        val (notes, _) = faker.listNotes(0, 5)
        assertEquals(5, notes.size)
    }

    @Test
    fun listNotes_respectsOffset() {
        val (firstBatch, _) = faker.listNotes(0, 1)
        val (secondBatch, _) = faker.listNotes(1, 1)

        // Verify we get different notes
        assertNotEquals(firstBatch[0].getTitle(), secondBatch[0].getTitle())
    }

    @Test
    fun listNotes_hasMoreIsTrueWhenMoreNotesExist() {
        val (_, hasMore) = faker.listNotes(0, 5)
        assertTrue(hasMore) // Faker has 13 notes, so requesting 5 should show hasMore = true
    }

    @Test
    fun listNotes_hasMoreIsFalseWhenNoMoreNotes() {
        val (_, hasMore) = faker.listNotes(0, 20)
        assertFalse(hasMore) // Requesting more than available should show hasMore = false
    }

    @Test
    fun listNotes_returnsEmptyWhenOffsetTooHigh() {
        val (notes, hasMore) = faker.listNotes(100, 5)
        assertEquals(0, notes.size)
        assertFalse(hasMore)
    }

    @Test
    fun createNote_returnsValidNote() {
        val note = faker.createNote()

        assertNotNull(note.uuid)
        assertEquals("new-note-uuid", note.uuid)

        // Verify title is formatted as date
        val title = note.getTitle()
        assertTrue(title.isNotEmpty())

        // Verify content
        val content = note.getContent()
        assertEquals("Aa Bb Cc Dd 1 2 3 4", content)
    }

    @Test
    fun exportAllNotes_returnsValidJSON() {
        val jsonString = faker.exportAllNotes()

        assertNotNull(jsonString)
        assertTrue(jsonString.isNotEmpty())

        // Verify it's valid JSON
        val jsonArray = JSONArray(jsonString)
        assertTrue(jsonArray.length() > 0)

        // Verify first note has required fields
        val firstNote = jsonArray.getJSONObject(0)
        assertTrue(firstNote.has("title"))
        assertTrue(firstNote.has("content"))
        assertTrue(firstNote.has("is_favorite"))
    }

    @Test
    fun exportAllNotes_exportsAllNotes() {
        val jsonString = faker.exportAllNotes()
        val jsonArray = JSONArray(jsonString)

        // Faker has 13 dummy notes
        assertEquals(13, jsonArray.length())
    }

    @Test
    fun exportAllNotes_containsCorrectData() {
        val jsonString = faker.exportAllNotes()
        val jsonArray = JSONArray(jsonString)

        val firstNote = jsonArray.getJSONObject(0)
        assertEquals("Note one two three four five six seven eight nine ten eleven twelve thirteen fourteen fifteen sixteen seventeen eighteen nineteen twenty", firstNote.getString("title"))
        assertTrue(firstNote.getString("content").startsWith("Content of Note one"))
    }

    @Test
    fun importNotes_returnsCorrectCount() {
        val jsonData = """
            [
                {"title": "Test 1", "content": "Content 1", "is_favorite": 0},
                {"title": "Test 2", "content": "Content 2", "is_favorite": 1},
                {"title": "Test 3", "content": "Content 3", "is_favorite": 0}
            ]
        """.trimIndent()

        val count = faker.importNotes(jsonData)
        assertEquals(3, count)
    }

    @Test
    fun importNotes_handlesEmptyArray() {
        val count = faker.importNotes("[]")
        assertEquals(0, count)
    }

    @Test
    fun importNotes_handlesValidJSONWithMultipleNotes() {
        val testNotes = JSONArray()
        for (i in 1..5) {
            val note = JSONObject()
            note.put("title", "Note $i")
            note.put("content", "Content $i")
            note.put("is_favorite", 0)
            testNotes.put(note)
        }

        val count = faker.importNotes(testNotes.toString())
        assertEquals(5, count)
    }

    @Test
    fun deleteAllNotes_returnsCorrectCount() {
        val count = faker.deleteAllNotes()
        assertEquals(13, count) // Faker has 13 dummy notes
    }

    @Test
    fun note_getTitleReturnsTitle() {
        val (notes, _) = faker.listNotes(1, 1)
        val note = notes[0]

        val title = note.getTitle()
        assertEquals("Note 2", title)
    }

    @Test
    fun note_getContentReturnsContent() {
        val (notes, _) = faker.listNotes(1, 1)
        val note = notes[0]

        val content = note.getContent()
        assertEquals("Content of Note 2", content)
    }

    @Test
    fun note_editContentDoesNotThrow() {
        val (notes, _) = faker.listNotes(0, 1)
        val note = notes[0]

        // Should not throw exception
        note.editContent("New content")
    }

    @Test
    fun note_isFavoriteReturnsBoolean() {
        val (notes, _) = faker.listNotes(0, 1)
        val note = notes[0]

        val isFav = note.isFavorite()
        // Verify isFavorite returns a boolean value
        assertNotNull(isFav)
    }

    @Test
    fun note_setFavoriteDoesNotThrow() {
        val (notes, _) = faker.listNotes(0, 1)
        val note = notes[0]

        // Should not throw exception
        note.setFavorite(true)
        note.setFavorite(false)
    }

    @Test
    fun note_removeDoesNotThrow() {
        val (notes, _) = faker.listNotes(0, 1)
        val note = notes[0]

        // Should not throw exception
        note.rm()
    }
}
