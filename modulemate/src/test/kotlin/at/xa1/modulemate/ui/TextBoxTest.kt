package at.xa1.modulemate.ui

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TextBoxTest {
    @Test
    fun `character can be added to empty text`() {
        assertEquals(
            TextBox(text = "H", cursor = 1),
            TextBox(text = "").reduce(UiUserInput.Char('H'))
        )
    }

    @Test
    fun `character can be added to text`() {
        assertEquals(
            TextBox(text = "Hi!", cursor = 3),
            TextBox(text = "Hi", cursor = 2).reduce(UiUserInput.Char('!'))
        )
    }

    @Test
    fun `character can be added mid text`() {
        assertEquals(
            TextBox(text = "ABC", cursor = 2),
            TextBox(text = "AC", cursor = 1).reduce(UiUserInput.Char('B'))
        )
    }

    @Test
    fun `character can be added at beginning of text`() {
        assertEquals(
            TextBox(text = "dog", cursor = 1),
            TextBox(text = "og", cursor = 0).reduce(UiUserInput.Char('d'))
        )
    }

    @Test
    fun `cursor can be moved left from end`() {
        assertEquals(
            TextBox(text = "Hi", cursor = 1),
            TextBox(text = "Hi", cursor = 2).reduce(UiUserInput.Arrow.Left)
        )
    }

    @Test
    fun `cursor cannot be moved left when at start`() {
        assertEquals(
            TextBox(text = "Hi", cursor = 0),
            TextBox(text = "Hi", cursor = 0).reduce(UiUserInput.Arrow.Left)
        )
    }

    @Test
    fun `cursor can be moved right to last char`() {
        assertEquals(
            TextBox(text = "Hi", cursor = 2),
            TextBox(text = "Hi", cursor = 1).reduce(UiUserInput.Arrow.Right)
        )
    }

    @Test
    fun `cursor cannot be moved right when at end`() {
        assertEquals(
            TextBox(text = "Hi", cursor = 2),
            TextBox(text = "Hi", cursor = 2).reduce(UiUserInput.Arrow.Right)
        )
    }

    @Test
    fun `backspace removes character at end when cursor at end`() {
        assertEquals(
            TextBox(text = "Hi", cursor = 2),
            TextBox(text = "Hi!", cursor = 3).reduce(UiUserInput.Backspace)
        )
    }

    @Test
    fun `backspace removes only character when cursor at end`() {
        assertEquals(
            TextBox(text = "", cursor = 0),
            TextBox(text = "H", cursor = 1).reduce(UiUserInput.Backspace)
        )
    }

    @Test
    fun `backspace does nothing when cursor is at beginning`() {
        assertEquals(
            TextBox(text = "Hi!", cursor = 0),
            TextBox(text = "Hi!", cursor = 0).reduce(UiUserInput.Backspace)
        )
    }

    @Test
    fun `backspace does nothing when text empty`() {
        assertEquals(
            TextBox(text = "", cursor = 0),
            TextBox(text = "", cursor = 0).reduce(UiUserInput.Backspace)
        )
    }

    @Test
    fun `delete does nothing when cursor is at end`() {
        assertEquals(
            TextBox(text = "Hi!", cursor = 3),
            TextBox(text = "Hi!", cursor = 3).reduce(UiUserInput.Delete)
        )
    }

    @Test
    fun `delete removes last character when cursor is before last`() {
        assertEquals(
            TextBox(text = "Hi", cursor = 2),
            TextBox(text = "Hi!", cursor = 2).reduce(UiUserInput.Delete)
        )
    }

    @Test
    fun `delete removes first character when cursor is at beginning`() {
        assertEquals(
            TextBox(text = "i!", cursor = 0),
            TextBox(text = "Hi!", cursor = 0).reduce(UiUserInput.Delete)
        )
    }
}
