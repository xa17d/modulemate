package at.xa1.modulemate.cli

import at.xa1.modulemate.cli.CliFormat.BLUE
import at.xa1.modulemate.cli.CliFormat.BOLD
import at.xa1.modulemate.cli.CliFormat.CLEAR_UNTIL_END_OF_LINE
import at.xa1.modulemate.cli.CliFormat.GREEN
import at.xa1.modulemate.cli.CliFormat.RESET
import at.xa1.modulemate.cli.CliFormat.UNDERLINE
import at.xa1.modulemate.cli.CliFormat.YELLOW

object Cli {
    fun heading(content: String, formatting: String, addendum: String? = null, addendumFormatting: String? = null) {
        print("$formatting $content")

        if (addendum != null) {
            print(RESET)
            print(addendumFormatting)
            print(addendum)
        }

        print("$CLEAR_UNTIL_END_OF_LINE\n$RESET$CLEAR_UNTIL_END_OF_LINE")
    }

    fun subHeading(content: String) {
        println("$UNDERLINE$BOLD$content$RESET")
    }

    fun line(content: String = "") {
        println(content)
    }

    fun prompt(default: String = ""): String {
        val defaultPrint = if (default.isEmpty()) {
            ""
        } else {
            "[$GREEN$default$BLUE]"
        }

        print("$BLUE$defaultPrint〉$RESET")

        return readln()
    }

    fun table(block: CliTable.() -> Unit) {
        val t = CliTable()
        t.block()
        print(t.toString())
    }

    fun stepCommand(command: String) {
        println("$YELLOW# $GREEN$command$RESET")
    }
}
