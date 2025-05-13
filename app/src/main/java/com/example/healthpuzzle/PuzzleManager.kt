object PuzzleManager {

    private val sections = listOf(
        "헬린이" to 9,
        "헬스러" to 9
    )

    val collectedPuzzles = mutableSetOf<String>()

    fun collectPuzzle(): String? {
        for ((section, count) in sections) {
            val allPieces = (0 until count).map { "$section-$it" }
            val uncollected = allPieces.filterNot { collectedPuzzles.contains(it) }

            if (uncollected.isNotEmpty()) {
                val puzzle = uncollected.random()
                collectedPuzzles.add(puzzle)
                return puzzle
            } else {
                // 다음 섹션으로 넘어감
                continue
            }
        }

        return null // 모든 퍼즐을 수집한 경우
    }

    fun getPuzzleCount(): Int = collectedPuzzles.size
}
