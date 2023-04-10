package at.xa1.modulemate.command

import at.xa1.modulemate.system.Browser
import java.net.URLEncoder
import java.nio.charset.Charset

class BrowserCommand(
    val shortcut: String,
    private val browser: Browser,
    private val variables: Variables,
    private val urlPattern: String,
) {
    fun run() {
        val url = variables.replacePlaceholders(urlPattern) { value ->
            URLEncoder.encode(value, Charset.forName("UTF8"))
        }
        browser.open(url)
    }
}
