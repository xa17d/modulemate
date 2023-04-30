package at.xa1.modulemate

import java.io.File

object Modulemate {
    const val VERSION = "0.4.0"

    const val MODULEMATE_FOLDER: String = ".modulemate"

    val home: File by lazy {
        val jarLocation = File(Modulemate::class.java.protectionDomain.codeSource.location.toURI())
        findModulemateHomeInParents(jarLocation) ?: error("Cannot find folder where modulemate is located.")
    }

    private fun findModulemateHomeInParents(folder: File): File? {
        val modulemateFolderCandidate = File(folder, MODULEMATE_FOLDER)
        val homeFileCandidate = File(modulemateFolderCandidate, "modulemate-home")
        if (homeFileCandidate.exists()) {
            return folder
        }

        val parent = folder.parentFile ?: return null
        return findModulemateHomeInParents(parent)
    }
}
