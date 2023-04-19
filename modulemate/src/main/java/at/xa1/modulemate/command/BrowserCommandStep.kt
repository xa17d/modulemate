package at.xa1.modulemate.command

import at.xa1.modulemate.system.Browser
import java.net.URLEncoder
import java.nio.charset.Charset

class BrowserCommandStep(
    override val runWhen: RunWhen,
    private val browser: Browser,
    private val variables: Variables,
    private val urlPattern: String,
) : CommandStep {

    override fun run(): CommandResult {
        val url = variables.replacePlaceholders(urlPattern) { value ->
            URLEncoder.encode(value, Charset.forName("UTF8"))
        }
        val success = browser.open(url)

        return success.successToCommandResult()
    }
}
