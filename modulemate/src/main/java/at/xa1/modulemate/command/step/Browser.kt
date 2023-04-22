package at.xa1.modulemate.command.step

import at.xa1.modulemate.command.CommandResult
import at.xa1.modulemate.command.successToCommandResult
import at.xa1.modulemate.command.variable.Variables
import at.xa1.modulemate.command.variable.replacePlaceholders
import at.xa1.modulemate.system.Browser
import java.net.URLEncoder
import java.nio.charset.Charset

class Browser(
    private val browser: Browser,
    private val variables: Variables,
    private val urlPattern: String
) : CommandStep {

    override fun run(): CommandResult {
        val url = variables.replacePlaceholders(urlPattern) { value ->
            URLEncoder.encode(value, Charset.forName("UTF8"))
        }
        val success = browser.open(url)

        return success.successToCommandResult()
    }
}
