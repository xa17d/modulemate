package at.xa1.modulemate.cli

import kotlin.math.max

class CliTable {
    private var columnWidth = emptyArray<Int>()
    private val rows = mutableListOf<List<Any>>()

    fun row(vararg columns: Any) {
        require(columns.isNotEmpty())

        if (columnWidth.isEmpty()) {
            columnWidth = Array(columns.size) { 0 }
        }

        require(columnWidth.size == columns.size) {
            "Column count don't match for each row."
        }

        columns.forEachIndexed { index, value ->

            val length =
                when (value) {
                    is FormattedCell -> value.content.length
                    else -> value.toString().length
                }

            columnWidth[index] = max(columnWidth[index], length)
        }

        rows.add(columns.toList())
    }

    override fun toString(): String =
        buildString {
            rows.forEachIndexed { _, row ->
                row.forEachIndexed { columnIndex, cellValue ->
                    val columnFormat =
                        if (columnIndex == 0) {
                            CliFormat.BOLD
                        } else {
                            ""
                        }

                    val content: String
                    val formatting: String

                    when (cellValue) {
                        is FormattedCell -> {
                            content = cellValue.content
                            formatting = cellValue.formatting
                        }

                        else -> {
                            content = cellValue.toString()
                            formatting = ""
                        }
                    }

                    val maxLength = columnWidth[columnIndex]
                    val cell = " $columnFormat$formatting${content.padEnd(maxLength)} "

                    append(cell)
                    append(CliFormat.RESET)
                }
                appendLine()
            }
        }

    data class FormattedCell(
        val content: String,
        val formatting: String,
    )
}
