package at.xa1.modulemate.command.step

import at.xa1.modulemate.cli.Cli
import at.xa1.modulemate.command.CommandContext
import at.xa1.modulemate.command.CommandResult
import at.xa1.modulemate.command.successToCommandResult
import at.xa1.modulemate.command.variable.replacePlaceholders
import at.xa1.modulemate.system.Browser
import java.net.URLEncoder
import java.nio.charset.Charset

class Browser(
    private val browser: Browser,
    private val urlPattern: String,
) : CommandStep {
    override fun run(context: CommandContext): CommandResult {
        val url =
            context.variables.replacePlaceholders(urlPattern) { value ->
                URLEncoder.encode(value, Charset.forName("UTF8"))
            }

        Cli.stepCommand(url)

        val success = browser.open(url)

        return success.successToCommandResult()
    }
}
