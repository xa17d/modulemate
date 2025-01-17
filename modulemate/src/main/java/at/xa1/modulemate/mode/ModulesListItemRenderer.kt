package at.xa1.modulemate.mode

import at.xa1.modulemate.cli.CliEmoji
import at.xa1.modulemate.module.Module
import at.xa1.modulemate.module.Modules
import at.xa1.modulemate.ui.ListItemRenderer

class ModulesListItemRenderer(
    private val modules: Modules,
) : ListItemRenderer<Module> {
    override fun render(
        item: Module,
        isSelected: Boolean,
    ): String {
        val markers =
            if (modules.isActive(item)) {
                "  ${CliEmoji.WHITE_HEAVY_CHECK_MARK} "
            } else {
                "     "
            }
        return markers + formatModule(item, !isSelected)
    }
}
