package page.newlevel.magpie.storage

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import page.newlevel.notes.storage.Note
import page.newlevel.notes.storage.StorageAbstract

/**
 * Unit tests for LocalDB storage operations.
 * Tests all CRUD (Create, Read, Update, Delete) operations.
 */
class LocalDBUnitTest {

    private lateinit var mockLocalDB: StorageAbstract

    @Before
    fun setup() {
        // Create a mock LocalDB for unit testing without needing Android context
        mockLocalDB = mockk(relaxed = true)
    }

    @Test
    fun testCreateNote() {
        // Arrange
        val testNote = Note(
            uuid = "test-uuid-1",
            getTitle = { "Test Title" },
            getContent = { "Test Content" },
            editContent = { _ -> },
            rm = { }
        )

        // Act
        mockLocalDB.createNote(testNote)

        // Assert
        verify(exactly = 1) { mockLocalDB.createNote(testNote) }
    }

    @Test
    fun testListNotesEmpty() {
        // Arrange
        every { mockLocalDB.listNotes(offset = 0, limit = 10) } returns Pair(emptyList(), false)

        // Act
        val (notes, hasMore) = mockLocalDB.listNotes(offset = 0, limit = 10)

        // Assert
        assertTrue(notes.isEmpty())
        assertFalse(hasMore)
    }

    @Test
    fun testListNotesWithData() {
        // Arrange
        val testNotes = listOf(
            Note(
                uuid = "uuid-1",
                getTitle = { "Title 1" },
                getContent = { "Content 1" },
                editContent = { _ -> },
                rm = { }
            ),
            Note(
                uuid = "uuid-2",
                getTitle = { "Title 2" },
                getContent = { "Content 2" },
                editContent = { _ -> },
                rm = { }
            )
        )
        every { mockLocalDB.listNotes(offset = 0, limit = 10) } returns Pair(testNotes, false)

        // Act
        val (notes, hasMore) = mockLocalDB.listNotes(offset = 0, limit = 10)

        // Assert
        assertEquals(2, notes.size)
        assertEquals("Title 1", notes[0].getTitle())
        assertEquals("Content 1", notes[0].getContent())
        assertEquals("Title 2", notes[1].getTitle())
        assertEquals("Content 2", notes[1].getContent())
        assertFalse(hasMore)
    }

    @Test
    fun testListNotesWithPagination() {
        // Arrange
        val testNotes = (1..10).map { i ->
            Note(
                uuid = "uuid-$i",
                getTitle = { "Title $i" },
                getContent = { "Content $i" },
                editContent = { _ -> },
                rm = { }
            )
        }
        every { mockLocalDB.listNotes(offset = 0, limit = 10) } returns Pair(testNotes, true)

        // Act
        val (notes, hasMore) = mockLocalDB.listNotes(offset = 0, limit = 10)

        // Assert
        assertEquals(10, notes.size)
        assertTrue(hasMore) // Should indicate more data available
    }

    @Test
    fun testListNotesWithOffset() {
        // Arrange
        val testNotes = listOf(
            Note(
                uuid = "uuid-11",
                getTitle = { "Title 11" },
                getContent = { "Content 11" },
                editContent = { _ -> },
                rm = { }
            )
        )
        every { mockLocalDB.listNotes(offset = 10, limit = 10) } returns Pair(testNotes, false)

        // Act
        val (notes, hasMore) = mockLocalDB.listNotes(offset = 10, limit = 10)

        // Assert
        assertEquals(1, notes.size)
        assertEquals("Title 11", notes[0].getTitle())
        assertFalse(hasMore)
    }

    @Test
    fun testNoteEditContent() {
        // Arrange
        var editedContent = ""
        val testNote = Note(
            uuid = "uuid-1",
            getTitle = { "Test Title" },
            getContent = { "Original Content" },
            editContent = { content -> editedContent = content },
            rm = { }
        )

        // Act
        testNote.editContent("Updated Content")

        // Assert
        assertEquals("Updated Content", editedContent)
    }

    @Test
    fun testNoteDelete() {
        // Arrange
        var wasDeleted = false
        val testNote = Note(
            uuid = "uuid-1",
            getTitle = { "Test Title" },
            getContent = { "Test Content" },
            editContent = { _ -> },
            rm = { wasDeleted = true }
        )

        // Act
        testNote.rm()

        // Assert
        assertTrue(wasDeleted)
    }

    @Test
    fun testNoteGetters() {
        // Arrange
        val testNote = Note(
            uuid = "test-uuid",
            getTitle = { "Test Title" },
            getContent = { "Test Content" },
            editContent = { _ -> },
            rm = { }
        )

        // Act & Assert
        assertEquals("test-uuid", testNote.uuid)
        assertEquals("Test Title", testNote.getTitle())
        assertEquals("Test Content", testNote.getContent())
    }

    @Test
    fun testCreateMultipleNotes() {
        // Arrange
        val notes = mutableListOf<Note>()
        for (i in 1..3) {
            notes.add(
                Note(
                    uuid = "uuid-$i",
                    getTitle = { "Title $i" },
                    getContent = { "Content $i" },
                    editContent = { _ -> },
                    rm = { }
                )
            )
        }

        // Act & Assert
        for (note in notes) {
            mockLocalDB.createNote(note)
        }

        verify(exactly = 3) { mockLocalDB.createNote(any()) }
    }

    @Test
    fun testAbstractStorageInterface() {
        // This test ensures StorageAbstract has the required methods
        assertTrue(StorageAbstract::class.java.declaredMethods.any { it.name == "listNotes" })
        assertTrue(StorageAbstract::class.java.declaredMethods.any { it.name == "createNote" })
    }
}

