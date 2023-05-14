package at.xa1.modulemate.ui

import org.jline.utils.NonBlockingReader
import java.awt.event.KeyEvent

sealed interface UiUserInput {
    class Unknown(private val codes: IntArray) : UiUserInput {
        override fun toString(): String = "Unknown(${codes.toList()})"

        companion object {
            fun of(vararg codes: Int): Unknown = Unknown(codes)
        }
    }

    data class Char(val char: kotlin.Char) : UiUserInput

    object Return : UiUserInput {
        const val code: Int = 13
    }

    object Backspace : UiUserInput {
        const val code: Int = 127
    }

    /**
     * `Del` key (`⌦`) that deletes the character ahead of the cursor.
     *
     */
    object Delete : UiUserInput

    object Escape : UiUserInput {
        const val code: Int = 27
    }

    object Tab : UiUserInput {
        const val code: Int = 9
    }

    object Shift {
        object Tab : UiUserInput
    }

    object EndOfInput : UiUserInput
    sealed interface Arrow : UiUserInput {
        object Left : Arrow
        object Up : Arrow
        object Down : Arrow
        object Right : Arrow
    }
}

internal fun readUserInput(reader: NonBlockingReader): UiUserInput {
    val code = reader.read()

    when (code) {
        UiUserInput.Return.code -> return UiUserInput.Return
        UiUserInput.Escape.code -> return readEscape(reader)
        UiUserInput.Backspace.code -> return UiUserInput.Backspace
        UiUserInput.Tab.code -> return UiUserInput.Tab
        NonBlockingReader.EOF -> return UiUserInput.EndOfInput
    }

    val char = Char(code)

    if (isPrintableChar(char)) {
        return UiUserInput.Char(char)
    }

    return UiUserInput.Unknown.of(code)
}

private fun readEscape(reader: NonBlockingReader): UiUserInput {
    return when (val escaped = reader.read(10).toChar()) {
        '[' -> {
            val indicator = reader.read()

            when (indicator.toChar()) {
                'A' -> UiUserInput.Arrow.Up
                'B' -> UiUserInput.Arrow.Down
                'C' -> UiUserInput.Arrow.Right
                'D' -> UiUserInput.Arrow.Left
                '3' -> {
                    when (val subIndicator = reader.read().toChar()) {
                        '~' -> UiUserInput.Delete
                        else -> UiUserInput.Unknown.of(
                            UiUserInput.Escape.code,
                            escaped.code,
                            indicator,
                            subIndicator.code
                        )
                    }
                }

                'Z' -> UiUserInput.Shift.Tab

                else -> UiUserInput.Unknown.of(UiUserInput.Escape.code, escaped.code, indicator)
            }
        }

        '~' -> UiUserInput.Delete
        NonBlockingReader.READ_EXPIRED.toChar() -> UiUserInput.Escape
        NonBlockingReader.EOF.toChar() -> UiUserInput.EndOfInput
        else -> UiUserInput.Unknown.of(UiUserInput.Escape.code, escaped.code)
    }
}

private fun isPrintableChar(char: Char): Boolean {
    val block = Character.UnicodeBlock.of(char)
    return !Character.isISOControl(char) &&
        char != KeyEvent.CHAR_UNDEFINED &&
        block != null &&
        block !== Character.UnicodeBlock.SPECIALS
}
