package at.xa1.modulemate.command

import at.xa1.modulemate.module.Module
import at.xa1.modulemate.module.ModuleType
import at.xa1.modulemate.module.Modules
import at.xa1.modulemate.system.FakeShell
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GradleCommandStepTest {

    private val fakeShell = FakeShell()

    @Test
    fun `gradle shell command is constructed correctly`() {

        fakeShell.failOnUnexpectedCommand = false

        GradleCommandStep(
            shell = fakeShell,
            flags = listOf("-Pflag1", "-Pflag2"),
            modules = Modules(
                listOf(
                    Module(":test-core", "", "", ModuleType.JAVA_LIB),
                    Module(":test-android", "", "", ModuleType.ANDROID_LIB),
                    Module(":test-app", "", "", ModuleType.ANDROID_APP),
                    Module(":test-other", "", "", ModuleType.OTHER),
                )
            ),
            javaLibraryTasks = listOf("javaTask1", "javaTask2"),
            androidLibTasks = listOf("androidLibTask1", "androidLibTask2"),
            androidAppTasks = listOf("androidAppTask1", "androidAppTask2"),
        ).run()

        assertEquals(
            listOf(
                "./gradlew",
                "-Pflag1",
                "-Pflag2",
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
}
