package at.xa1.modulemate.command.variable

class VariableSet(
    private val parent: Variables? = null
) : Variables {
    private val variables = mutableMapOf<String, Variable>()

    override fun get(name: String): String {
        val variable = getOrNull(name)
        if (variable != null) {
            return variable.getValue()
        }

        if (parent == null) {
            error("Unknown variable: $name")
        }

        return parent.get(name)
    }

    private fun getOrNull(name: String): Variable? = variables[name]

    fun add(variable: Variable) {
        variables[variable.name] = variable
    }
}
