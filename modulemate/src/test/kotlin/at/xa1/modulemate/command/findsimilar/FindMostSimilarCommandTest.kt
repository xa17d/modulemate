package at.xa1.modulemate.command.findsimilar

import at.xa1.modulemate.command.findMostSimilarCommand
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class FindMostSimilarCommandTest {
    @Test
    fun `find activeWork by axtvewor`() {
        assertEquals(
            "activeWork",
            findMostSimilarCommand(
                listOf("activeWork", "conflictAnalysis", "changedModules"),
                "axtvewor",
            ),
        )
    }

    @Test
    fun `find conflictAnalysis by conflicts`() {
        assertEquals(
            "conflictAnalysis",
            findMostSimilarCommand(
                listOf("activeWork", "conflictAnalysis", "changedModules"),
                "conflicts",
            ),
        )
    }
}
