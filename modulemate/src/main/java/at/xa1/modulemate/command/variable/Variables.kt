package at.xa1.modulemate.command.variable

interface Variables {
    fun get(name: String): String

    fun getAll(): List<Variable>
}

fun Variables.replacePlaceholders(patternString: String, encoder: VariableEncoder = NopVariableEncoder): String {
    return patternString.replace(Regex("\\{([^\\}]+)\\}")) { matchResult ->
        val variableName = matchResult.groupValues[1]
        val value = get(variableName)
        encoder.encode(value)
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
    fun getValue(): String = value()
}
