package at.xa1.modulemate.command

class Variables {
    private val variables = mutableMapOf<String, Variable>()

    fun get(name: String): String {
        val variable = variables[name] ?: error("Unknown variable: $name")
        return variable.getValue()
    }

    fun add(variable: Variable) {
        variables[variable.name] = variable
    }

    fun replacePlaceholders(patternString: String, encoder: VariableEncoder = NopVariableEncoder): String {
        return patternString.replace(Regex("\\{([^\\}]+)\\}")) { matchResult ->
            val variableName = matchResult.groupValues[1]
            val value = get(variableName)
            encoder.encode(value)
        }
    }
}

fun interface VariableEncoder {
    fun encode(value: String): String
}

object NopVariableEncoder : VariableEncoder {
    override fun encode(value: String): String {
        return value
    }
}

class Variable(
    val name: String,
    private val value: () -> String
) {
    private var valueCache: String? = null
    fun getValue(): String {
        val cachedValue = valueCache
        if (cachedValue != null) {
            return cachedValue
        }

        val resolvedValue = value()
        valueCache = resolvedValue
        return resolvedValue
    }
}
