package at.xa1.modulemate.ui

import at.xa1.modulemate.cli.CliFormat

data class TextBox(
    val text: String = "",
    val hint: String = "",
    val emoji: String = "💬",
    val cursor: Int = 0,
)

fun ScreenContext.print(
    textBox: TextBox,
    width: Int,
) {
    print("╭")
    repeat(width - 2) { print("─") }
    print("╮\n")

    print("│ ")
    print(textBox.emoji)
    print(" ")

    val contentLength =
        if (textBox.text.isEmpty()) {
            print(CliFormat.YELLOW)
            print(textBox.hint)
            print(CliFormat.RESET)
            textBox.hint.length
        } else {
            print(textBox.text)
            textBox.text.length
        }

    repeat(width - contentLength - 6) { print(" ") }
    print("│\n")

    print("╰")
    repeat(width - 2) { print("─") }
    print("╯\n")
}

fun TextBox.reduce(userInput: UiUserInput): TextBox {
    return when (userInput) {
        UiUserInput.Arrow.Left -> {
            copy(cursor = (cursor - 1).coerceAtLeast(0))
        }

        UiUserInput.Arrow.Right -> {
            copy(cursor = (cursor + 1).coerceAtMost(text.length))
        }

        UiUserInput.Backspace -> {
            if (cursor > 0) {
                copy(
                    text = text.substring(0, cursor - 1) + text.substring(cursor),
                    cursor = cursor - 1,
                )
            } else {
                this
            }
        }

        is UiUserInput.Char -> {
            copy(
                text = text.substring(0, cursor) + userInput.char + text.substring(cursor),
                cursor = cursor + 1,
            )
        }

        UiUserInput.Delete -> {
            if (cursor < text.length) {
                copy(text = text.substring(0, cursor) + text.substring(cursor + 1))
            } else {
                this
            }
        }

        else -> this
    }
}
