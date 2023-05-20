package at.xa1.modulemate.mode

import at.xa1.modulemate.module.Module
import at.xa1.modulemate.module.Modules
import at.xa1.modulemate.ui.ListBoxItemRenderer

class ModulesListItemRenderer(
    private val modules: Modules
) : ListBoxItemRenderer<Module> {
    override fun render(item: Module, isSelected: Boolean): String {
        val markers = if (modules.isActive(item)) {
            "  ✅  "
        } else {
            "     "
        }
        return markers + formatModule(item, !isSelected)
    }
}
