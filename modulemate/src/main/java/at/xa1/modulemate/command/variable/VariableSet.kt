package at.xa1.modulemate.command.variable

class VariableSet(
    private val parent: VariableSet? = null
) : Variables {
    private val variables = mutableMapOf<String, Variable>()

    override fun get(name: String): String {
        val variable = getOrNull(name) ?: parent?.getOrNull(name) ?: error("Unknown variable: $name")
        return variable.getValue()
    }

    private fun getOrNull(name: String): Variable? = variables[name]

    fun add(variable: Variable) {
        variables[variable.name] = variable
    }
}
