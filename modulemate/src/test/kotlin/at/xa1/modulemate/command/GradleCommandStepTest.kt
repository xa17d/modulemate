package at.xa1.modulemate.command

import at.xa1.modulemate.module.Module
import at.xa1.modulemate.module.ModuleType
import at.xa1.modulemate.module.testModules
import at.xa1.modulemate.system.FakeShell
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GradleCommandStepTest {

    private val fakeShell = FakeShell().apply {
        failOnUnexpectedCommand = false
    }

    @Test
    fun `gradle shell command is constructed correctly`() {
        GradleCommandStep(
            shell = fakeShell,
            kotlinLibFlags = listOf("-PkotlinLibFlag1", "-PkotlinLibFlag2"),
            androidLibFlags = listOf("-PandroidLibFlag1", "-PandroidLibFlag2"),
            androidAppFlags = listOf("-PandroidAppFlag1", "-PandroidAppFlag2"),
            modules = testModules(
                Module(":test-core", "", "", ModuleType.KOTLIN_LIB),
                Module(":test-android", "", "", ModuleType.ANDROID_LIB),
                Module(":test-app", "", "", ModuleType.ANDROID_APP),
                Module(":test-other", "", "", ModuleType.OTHER)
            ),
            kotlinLibTasks = listOf("javaTask1", "javaTask2"),
            androidLibTasks = listOf("androidLibTask1", "androidLibTask2"),
            androidAppTasks = listOf("androidAppTask1", "androidAppTask2")
        ).run()

        assertEquals(
            listOf(
                "./gradlew",
                "-PkotlinLibFlag1",
                "-PkotlinLibFlag2",
                "-PandroidLibFlag1",
                "-PandroidLibFlag2",
                "-PandroidAppFlag1",
                "-PandroidAppFlag2",
                ":test-core:javaTask1",
                ":test-core:javaTask2",
                ":test-android:androidLibTask1",
                ":test-android:androidLibTask2",
                ":test-app:androidAppTask1",
                ":test-app:androidAppTask2"
            ),
            fakeShell.actualCommands.single()
        )
    }

    @Test
    fun `gradle shell command only contains flags of module types present`() {
        GradleCommandStep(
            shell = fakeShell,
            kotlinLibFlags = listOf("-PkotlinLibFlag1", "-PkotlinLibFlag2"),
            androidLibFlags = listOf("-PandroidLibFlag1", "-PandroidLibFlag2"),
            androidAppFlags = listOf("-PandroidAppFlag1", "-PandroidAppFlag2"),
            modules = testModules(
                Module(":test-core", "", "", ModuleType.KOTLIN_LIB)
            ),
            kotlinLibTasks = listOf("javaTask1", "javaTask2"),
            androidLibTasks = listOf("androidLibTask1", "androidLibTask2"),
            androidAppTasks = listOf("androidAppTask1", "androidAppTask2")
        ).run()

        assertEquals(
            listOf(
                "./gradlew",
                "-PkotlinLibFlag1",
                "-PkotlinLibFlag2",
                ":test-core:javaTask1",
                ":test-core:javaTask2"
            ),
            fakeShell.actualCommands.single()
        )
    }
}
