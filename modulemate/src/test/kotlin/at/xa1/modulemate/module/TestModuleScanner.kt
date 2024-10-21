package at.xa1.modulemate.module

import at.xa1.modulemate.module.filter.AllModulesFilter

class TestModuleScanner(
    private val modules: List<Module>,
) : ModulesScanner {
    override fun scan(): List<Module> = modules
}

fun testModules(vararg modules: Module): Modules {
    val result = Modules(TestModuleScanner(modules.toList()))
    result.applyFilter(AllModulesFilter())
    return result
}
