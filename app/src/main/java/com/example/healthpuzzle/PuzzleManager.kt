package com.example.healthpuzzle

object PuzzleManager {
    private val allPuzzlePieces = listOf("🧩1", "🧩2", "🧩3", "🧩4", "🧩5")
    val collectedPuzzles = mutableSetOf<String>()
    var puzzleMasterCount = 0
        private set

    fun collectPuzzle(): String? {
        val uncollected = allPuzzlePieces.filterNot { collectedPuzzles.contains(it) }
        if (uncollected.isNotEmpty()) {
            val puzzle = uncollected.random()
            collectedPuzzles.add(puzzle)
            puzzleMasterCount++
            return puzzle
        }
        return null
    }

    fun getPuzzleCount(): Int = collectedPuzzles.size
}