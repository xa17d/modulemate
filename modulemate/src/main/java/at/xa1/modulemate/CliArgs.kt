package at.xa1.modulemate

/**
 * Helper class to read CLI arguments.
 * Each argument can only be consumed once.
 */
internal class CliArgs(args: Array<String>) {
    private val remainingArgs = args.toMutableList()

    fun nextOrNull(): String? {
        if (remainingArgs.isEmpty()) {
            return null
        }

        return remainingArgs.removeAt(0)
    }

    fun requireValue(key: String): String =
        getValueOrNull(key) ?: error("Argument expected but not provided: $key")

    fun getValueOrDefault(key: String, default: String): String =
        getValueOrNull(key) ?: default

    fun getValueOrNull(key: String): String? {
        val valueIndex = remainingArgs.indexOf(key)

        if (valueIndex != -1 && remainingArgs.size > valueIndex + 1) {
            val value = remainingArgs.removeAt(valueIndex + 1)
            remainingArgs.removeAt(valueIndex)
            return value
        }

        return null
    }

    fun requireBoolean(key: String): Boolean =
        getBooleanOrNull(key)
            ?: error("Argument expected but not provided: $key")

    fun getBooleanOrNull(key: String): Boolean? {
        val v = getValueOrNull(key) ?: return null
        return when (v) {
            "true" -> true
            "false" -> false
            else -> error("Invalid value for arg $key: $v. Must be `true` or `false`.")
        }
    }

    fun requireNoArgsLeft() {
        if (remainingArgs.isNotEmpty()) {
            error(
                "No more args expected, but there are unprocessed args: " +
                    remainingArgs.joinToString(" ") {
                        if (it.contains(" ")) {
                            "\"$it\""
                        } else {
                            it
                        }
                    }
            )
        }
    }
}
