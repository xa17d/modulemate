package at.xa1.modulemate.ui

import at.xa1.modulemate.cli.CliColor

fun ScreenContext.textBox(text: String, hint: String, emoji: String, width: Int) {
    print("╭")
    repeat(width - 2) { print("─") }
    print("╮\n")

    print("│ ")
    print(emoji)
    print(" ")

    val contentLength = if (text.isEmpty()) {
        print(CliColor.YELLOW)
        print(hint)
        print(CliColor.RESET)
        hint.length
    } else {
        print(text)
        text.length
    }

    repeat(width - contentLength - 6) { print(" ") }
    print("│\n")

    print("╰")
    repeat(width - 2) { print("─") }
    print("╯\n")
}
