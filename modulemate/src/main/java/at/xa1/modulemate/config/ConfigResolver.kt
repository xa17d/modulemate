package at.xa1.modulemate.config

import java.io.File
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class ConfigResolver(
    private val repositoryRoot: File,
    private val userHome: File = File(System.getProperty("user.home"))
) {
    private fun getConfig(root: File): Config? {
        val configFile = File(File(root, ".modulemate"), "config.json")
        if (configFile.exists()) {
            try {
                return Json.decodeFromString<Config>(configFile.readText())
            } catch (e: SerializationException) {
                throw IllegalArgumentException("Serialization error in config file: $configFile", e)
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("Format error in config file: $configFile", e)
            }
        }
        return null
    }
    fun getConfig(): Config {
        return getConfig(repositoryRoot) ?:
            getConfig(userHome) ?:
            defaultConfig
    }
}
