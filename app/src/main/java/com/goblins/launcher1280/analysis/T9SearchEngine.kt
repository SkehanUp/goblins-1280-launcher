package com.goblins.launcher1280.analysis

object T9SearchEngine {
    fun charToDigit(ch: Char): Char? = when (ch.lowercaseChar()) {
        in 'a'..'c' -> '2'
        in 'd'..'f' -> '3'
        in 'g'..'i' -> '4'
        in 'j'..'l' -> '5'
        in 'm'..'o' -> '6'
        in 'p'..'s' -> '7'
        in 't'..'v' -> '8'
        in 'w'..'z' -> '9'
        in '0'..'9' -> ch
        ' ' -> '0'
        else -> null
    }

    fun encodeLabel(label: String): String = buildString {
        label.forEach { ch -> charToDigit(ch)?.let(::append) }
    }

    fun encodeWordInitials(label: String): String = buildString {
        var foundLetterInWord = false
        label.forEach { ch ->
            if (ch.isLetterOrDigit()) {
                if (!foundLetterInWord && ch.isLetter()) {
                    charToDigit(ch)?.let(::append)
                    foundLetterInWord = true
                }
            } else {
                foundLetterInWord = false
            }
        }
    }

    fun scoreMatch(label: String, precomputedDigits: String, queryDigits: String): Int? {
        if (queryDigits.isEmpty()) return null

        if (precomputedDigits.startsWith(queryDigits)) return 300 - label.length

        val initials = encodeWordInitials(label)
        if (initials.startsWith(queryDigits)) return 250 - label.length

        wordStartIndex(label, queryDigits)?.let { return 200 - it }

        val substringIndex = precomputedDigits.indexOf(queryDigits)
        if (substringIndex >= 0) return 100 - substringIndex

        return null
    }

    fun <T> filterAndRank(
        items: List<T>,
        queryDigits: String,
        labelSelector: (T) -> String,
        digitsSelector: (T) -> String,
    ): List<T> {
        if (queryDigits.isBlank()) return items

        return items.withIndex()
            .mapNotNull { (index, item) ->
                val score = scoreMatch(
                    label = labelSelector(item),
                    precomputedDigits = digitsSelector(item),
                    queryDigits = queryDigits,
                )
                score?.let { RankedItem(item, it, index) }
            }
            .sortedWith(compareByDescending<RankedItem<T>> { it.score }.thenBy { it.index })
            .map { it.item }
    }

    private fun wordStartIndex(label: String, queryDigits: String): Int? {
        var inWord = false
        label.forEachIndexed { index, ch ->
            if (ch.isLetterOrDigit()) {
                if (!inWord && encodeLabel(label.substring(index)).startsWith(queryDigits)) return index
                inWord = true
            } else {
                inWord = false
            }
        }
        return null
    }

    private data class RankedItem<T>(val item: T, val score: Int, val index: Int)
}
