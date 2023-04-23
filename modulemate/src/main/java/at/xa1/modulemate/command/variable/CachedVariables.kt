package at.xa1.modulemate.command.variable

class CachedVariables(
    private val delegate: Variables
) : Variables {

    private val cache = mutableMapOf<String, String>()
    override fun get(name: String): String = cache.getOrPut(name) { delegate.get(name) }
    override fun getAll(): List<Variable> = delegate.getAll()

    fun clearCache() {
        cache.clear()
    }
}
