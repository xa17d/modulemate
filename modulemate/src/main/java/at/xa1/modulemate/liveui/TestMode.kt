package at.xa1.modulemate.liveui

import at.xa1.modulemate.ui.Ui
import at.xa1.modulemate.ui.UiUserInput

class TestMode(private val ui: Ui) : LiveUiMode {
    override fun print(input: UiUserInput?) {
        ui.createScreenContext().printScreen {
            print("TestMode!")
            flush()
        }
    }
}
