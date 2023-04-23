package at.xa1.modulemate

import java.io.File

object Modulemate {
    const val VERSION = "1.0.0-beta01"

    const val MODULEMATE_FOLDER: String = ".modulemate"
    fun getHome(): File {
        val jarLocation = File(Modulemate::class.java.protectionDomain.codeSource.location.toURI())
        return findModulemateHomeInParents(jarLocation) ?: error("Cannot find folder where modulemate is located.")
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
