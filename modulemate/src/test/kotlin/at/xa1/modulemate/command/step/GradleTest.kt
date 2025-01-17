package at.xa1.modulemate.command.step

import at.xa1.modulemate.command.testCommandContext
import at.xa1.modulemate.module.Module
import at.xa1.modulemate.module.ModuleType
import at.xa1.modulemate.module.testModules
import at.xa1.modulemate.system.FakeShell
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GradleTest {
    private val fakeShell =
        FakeShell().apply {
            failOnUnexpectedCommand = false
        }

    @Test
    fun `gradle shell command is constructed correctly`() {
        val modules =
            testModules(
                Module(":test-android", "", "", ModuleType.ANDROID_LIB),
                Module(":test-app", "", "", ModuleType.ANDROID_APP),
                Module(":test-core", "", "", ModuleType.KOTLIN_LIB),
                Module(":test-other", "", "", ModuleType.OTHER),
            )

        Gradle(
            shell = fakeShell,
            richConsole = true,
            kotlinLibFlags = listOf("-PkotlinLibFlag1", "-PkotlinLibFlag2"),
            androidLibFlags = listOf("-PandroidLibFlag1", "-PandroidLibFlag2"),
            androidAppFlags = listOf("-PandroidAppFlag1", "-PandroidAppFlag2"),
            kotlinLibTasks = listOf("javaTask1", "javaTask2"),
            androidLibTasks = listOf("androidLibTask1", "androidLibTask2"),
            androidAppTasks = listOf("androidAppTask1", "androidAppTask2"),
        ).run(testCommandContext(modules = modules))

        assertEquals(
            listOf(
                "./gradlew",
                "--console=rich",
                "-PandroidLibFlag1",
                "-PandroidLibFlag2",
                "-PandroidAppFlag1",
                "-PandroidAppFlag2",
                "-PkotlinLibFlag1",
                "-PkotlinLibFlag2",
                ":test-android:androidLibTask1",
                ":test-android:androidLibTask2",
                ":test-app:androidAppTask1",
                ":test-app:androidAppTask2",
                ":test-core:javaTask1",
                ":test-core:javaTask2",
            ),
            fakeShell.actualCommands.single(),
        )
    }

    @Test
    fun `gradle shell command only contains flags of module types present`() {
        val modules =
            testModules(
                Module(":test-core", "", "", ModuleType.KOTLIN_LIB),
            )

        Gradle(
            shell = fakeShell,
            richConsole = false,
            kotlinLibFlags = listOf("-PkotlinLibFlag1", "-PkotlinLibFlag2"),
            androidLibFlags = listOf("-PandroidLibFlag1", "-PandroidLibFlag2"),
            androidAppFlags = listOf("-PandroidAppFlag1", "-PandroidAppFlag2"),
            kotlinLibTasks = listOf("javaTask1", "javaTask2"),
            androidLibTasks = listOf("androidLibTask1", "androidLibTask2"),
            androidAppTasks = listOf("androidAppTask1", "androidAppTask2"),
        ).run(testCommandContext(modules = modules))

        assertEquals(
            listOf(
                "./gradlew",
                "-PkotlinLibFlag1",
                "-PkotlinLibFlag2",
                ":test-core:javaTask1",
                ":test-core:javaTask2",
            ),
            fakeShell.actualCommands.single(),
        )
    }
}
