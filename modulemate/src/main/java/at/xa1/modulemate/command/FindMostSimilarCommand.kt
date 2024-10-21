package at.xa1.modulemate.command

fun findMostSimilarCommand(
    commands: List<String>,
    input: String,
): String {
    val mostSimilarCommand =
        commands.fold(CommandSimilarity("", Int.MAX_VALUE)) { mostSimilar, currentCommand ->
            val distance = levenshteinDistance(currentCommand, input)

            if (distance < mostSimilar.distance) {
                CommandSimilarity(currentCommand, distance)
            } else {
                mostSimilar
            }
        }

    return mostSimilarCommand.command
}

private data class CommandSimilarity(
    val command: String,
    val distance: Int,
)

fun findMostSimilarWord(
    word: String,
    wordList: List<String>,
): String? {
    var minDistance = Int.MAX_VALUE
    var mostSimilarWord: String? = null
    for (w in wordList) {
        val distance = levenshteinDistance(word, w)
        if (distance < minDistance) {
            minDistance = distance
            mostSimilarWord = w
        }
    }
    return mostSimilarWord
}

private fun levenshteinDistance(
    s1: String,
    s2: String,
): Int {
    val m = s1.length
    val n = s2.length
    val d = Array(m + 1) { IntArray(n + 1) }
    for (i in 0..m) {
        d[i][0] = i
    }
    for (j in 0..n) {
        d[0][j] = j
    }
    for (j in 1..n) {
        for (i in 1..m) {
            if (s1[i - 1] == s2[j - 1]) {
                d[i][j] = d[i - 1][j - 1]
            } else {
                d[i][j] = minOf(d[i - 1][j], d[i][j - 1], d[i - 1][j - 1]) + 1
            }
        }
    }
    return d[m][n]
}
