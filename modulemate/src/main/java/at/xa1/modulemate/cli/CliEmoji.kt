package at.xa1.modulemate.cli

import at.xa1.modulemate.cli.CliEmoji.Companion
import at.xa1.modulemate.cli.CliEmoji.Companion.TOOLBOX
import at.xa1.modulemate.cli.CliEmoji.Terminal.APPLE_TERMINAL
import at.xa1.modulemate.cli.CliEmoji.Terminal.JETBRAINS_JEDITERM
import at.xa1.modulemate.cli.CliEmoji.Terminal.UNKNOWN

/**
 * This is a dirty hack to fix Emojis on certain terminals.
 *
 * Use emojis defined in [Companion] (e.g. [TOOLBOX]) instead of using Emoji-Strings directly.
 * The Apple and JetBrains Terminal only uses 1 char width for some Emojis and 2 chars for others.
 * This class tries to detect the Terminal it is running on and fix the spacing within [toString].
 */
internal class CliEmoji private constructor(
    private val raw: String,
    private val appleNeedsExtraChar: Boolean = false,
    private val jetBrainsNeedsExtraChar: Boolean = false,
) {
    override fun toString(): String =
        if (appleNeedsExtraChar && terminal == APPLE_TERMINAL ||
            jetBrainsNeedsExtraChar && terminal == JETBRAINS_JEDITERM
        ) {
            "$raw "
        } else {
            raw
        }

    companion object {
        private val terminal: Terminal by lazy {
            when {
                System.getenv("TERM_PROGRAM") == "Apple_Terminal" -> APPLE_TERMINAL
                System.getenv("TERMINAL_EMULATOR") == "JetBrains-JediTerm" -> JETBRAINS_JEDITERM
                else -> UNKNOWN
            }
        }

        /**
         * 🧰
         */
        val TOOLBOX: CliEmoji = CliEmoji("\uD83E\uDDF0")

        /**
         * ⚠️
         */
        val WARNING_SIGN: CliEmoji = CliEmoji("⚠\uFE0F", appleNeedsExtraChar = true)

        /**
         * 👋
         */
        val WAVING_HAND: CliEmoji = CliEmoji("\uD83D\uDC4B")

        /**
         * 🕹
         */
        val JOYSTICK: CliEmoji = CliEmoji("\uD83D\uDD79", appleNeedsExtraChar = true)

        /**
         * ⚡️
         */
        val HIGH_VOLTAGE: CliEmoji = CliEmoji("⚡\uFE0F")

        /**
         * ℹ️
         */
        val I_ENCLOSED: CliEmoji = CliEmoji("ℹ\uFE0F", appleNeedsExtraChar = true)

        /**
         * 🔍
         */
        val MAGNIFYING_GLASS: CliEmoji = CliEmoji("\uD83D\uDD0D")

        /**
         * 📦
         */
        val PACKAGE: CliEmoji = CliEmoji("\uD83D\uDCE6")

        /**
         * ✅
         */
        val WHITE_HEAVY_CHECK_MARK: CliEmoji = CliEmoji("\u2705", jetBrainsNeedsExtraChar = true)

        /**
         * ▶️
         */
        val PLAY_BUTTON: CliEmoji = CliEmoji("▶\uFE0F", appleNeedsExtraChar = true)

        /**
         * 🎉
         */
        val PARTY_POPPER: CliEmoji = CliEmoji("\uD83C\uDF89")

        fun printTest() {
            val definedEmojis =
                Companion::class.java.methods
                    .filter { method -> method.name.startsWith("get") && method.returnType == CliEmoji::class.java }
                    .map { field ->
                        field.isAccessible = true
                        field.invoke(Companion) as CliEmoji
                    }

            println("Terminal: $terminal")
            println("Corrected:  Raw:")
            println("|12|        |12|")
            definedEmojis.forEach { emoji ->
                println("|$emoji|        |${emoji.raw}|")
            }
        }
    }

    private enum class Terminal {
        UNKNOWN,
        JETBRAINS_JEDITERM,
        APPLE_TERMINAL,
    }
}
