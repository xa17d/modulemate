package at.xa1.modulemate.module

class TestModuleScanner(
    private val modules: List<Module>
) : ModulesScanner {
    override fun scan(): List<Module> = modules
}

fun testModules(vararg modules: Module): Modules {
    val result = Modules(TestModuleScanner(modules.toList()))
    result.filter = ""
    return result
}
