package at.xa1.modulemate.cli

object CliFormat {
    const val RESET: String = "\u001b[0m"

    const val BLACK: String = "\u001b[30m"
    const val RED: String = "\u001b[31m"
    const val GREEN: String = "\u001b[32m"
    const val YELLOW: String = "\u001b[33m"
    const val BLUE: String = "\u001b[34m"
    const val MAGENTA: String = "\u001b[35m"
    const val CYAN: String = "\u001b[36m"
    const val WHITE: String = "\u001b[37m"

    const val BRIGHT_BLACK: String = "\u001b[90m"
    const val BRIGHT_RED: String = "\u001b[91m"
    const val BRIGHT_GREEN: String = "\u001b[92m"
    const val BRIGHT_YELLOW: String = "\u001b[93m"
    const val BRIGHT_BLUE: String = "\u001b[94m"
    const val BRIGHT_MAGENTA: String = "\u001b[95m"
    const val BRIGHT_CYAN: String = "\u001b[96m"
    const val BRIGHT_WHITE: String = "\u001b[97m"

    const val BACKGROUND_BLACK: String = "\u001b[40m"
    const val BACKGROUND_RED: String = "\u001b[41m"
    const val BACKGROUND_GREEN: String = "\u001b[42m"
    const val BACKGROUND_YELLOW: String = "\u001b[43m"
    const val BACKGROUND_BLUE: String = "\u001b[44m"
    const val BACKGROUND_MAGENTA: String = "\u001b[45m"
    const val BACKGROUND_CYAN: String = "\u001b[46m"
    const val BACKGROUND_WHITE: String = "\u001b[47m"

    const val BACKGROUND_BRIGHT_BLACK: String = "\u001b[100m"
    const val BACKGROUND_BRIGHT_RED: String = "\u001b[101m"
    const val BACKGROUND_BRIGHT_GREEN: String = "\u001b[102m"
    const val BACKGROUND_BRIGHT_YELLOW: String = "\u001b[103m"
    const val BACKGROUND_BRIGHT_BLUE: String = "\u001b[104m"
    const val BACKGROUND_BRIGHT_MAGENTA: String = "\u001b[105m"
    const val BACKGROUND_BRIGHT_CYAN: String = "\u001b[106m"
    const val BACKGROUND_BRIGHT_WHITE: String = "\u001b[107m"

    const val BOLD: String = "\u001b[1m"
    const val UNDERLINE: String = "\u001b[4m"
    const val REVERSED: String = "\u001b[7m"

    fun cursorUp(n: Int): String = "\u001b[${n}A"

    fun cursorDown(n: Int): String = "\u001b[${n}B"

    fun cursorRight(n: Int): String = "\u001b[${n}C"

    fun cursorLeft(n: Int): String = "\u001b[${n}D"

    const val CLEAR_UNTIL_END_OF_SCREEN: String = "\u001b[0J"
    const val CLEAR_TO_BEGINNING_OF_SCREEN: String = "\u001b[1J"
    const val CLEAR_ENTIRE_SCREEN: String = "\u001b[2J"

    const val CLEAR_UNTIL_END_OF_LINE: String = "\u001b[0K"
    const val CLEAR_UNTIL_START_OF_LINE: String = "\u001b[1K"
    const val CLEAR_ENTIRE_LINE: String = "\u001b[2K"
}
