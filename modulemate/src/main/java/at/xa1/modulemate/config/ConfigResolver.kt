package at.xa1.modulemate.config

import at.xa1.modulemate.Modulemate
import at.xa1.modulemate.Modulemate.MODULEMATE_FOLDER
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File

/**
 * Finds, parses, and returns a list of relevant configurations.
 *
 * Locations searches:
 * - `{REPOSITORY_ROOT}/.modulemate/config.json`
 * - `~/.modulemate/config.json`
 * - `{MODULEMATE_HOME}/.modulemate/defaultConfig.json`
 */
class ConfigResolver(
    private val repositoryRoot: File,
    private val userHome: File = File(System.getProperty("user.home"))
) {

    /**
     * @return A list of all configurations found, ordered by priority. Item at index 0 has the highest priority.
     */
    fun getConfigs(): List<ConfigSource> {
        val repositoryConfig = getConfigSourceOrNull(repositoryRoot)
        val userConfig = getConfigSourceOrNull(userHome)
        val defaultConfig = getDefaultConfigOrNull()

        return listOfNotNull(
            repositoryConfig,
            userConfig,
            defaultConfig
        )
    }

    private fun getConfigSourceOrNull(root: File): ConfigSource? {
        val configFile = File(File(root, MODULEMATE_FOLDER), CONFIG_FILE)
        return if (configFile.exists()) {
            readConfigFile(configFile)
        } else {
            null
        }
    }

    private fun readConfigFile(configFile: File): ConfigSource {
        try {
            val config = Json.decodeFromString<Config>(configFile.readText())
            return ConfigSource(
                source = configFile,
                config = config
            )
        } catch (e: SerializationException) {
            throw IllegalArgumentException("Serialization error in config file: $configFile", e)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Format error in config file: $configFile", e)
        }
    }

    private fun getDefaultConfigOrNull(): ConfigSource? {
        val defaultConfigFile = File(File(Modulemate.getHome(), MODULEMATE_FOLDER), DEFAULT_CONFIG_FILE)
        return if (defaultConfigFile.exists()) {
            readConfigFile(defaultConfigFile)
        } else {
            null
        }
    }

    companion object {
        private const val CONFIG_FILE: String = "config.json"
        private const val DEFAULT_CONFIG_FILE: String = "defaultConfig.json"
    }
}
