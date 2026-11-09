package com.goblins.launcher1280.analysis

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AnalysisTest {
    @Test
    fun charToDigitMapsLettersDigitsSpacesAndIgnoresPunctuation() {
        assertEquals('2', T9SearchEngine.charToDigit('b'))
        assertEquals('7', T9SearchEngine.charToDigit('S'))
        assertEquals('0', T9SearchEngine.charToDigit(' '))
        assertEquals('5', T9SearchEngine.charToDigit('5'))
        assertNull(T9SearchEngine.charToDigit('-'))
    }

    @Test
    fun encodeLabelAndWordInitialsProduceT9Digits() {
        assertEquals("46645306277", T9SearchEngine.encodeLabel("Google Maps"))
        assertEquals("46", T9SearchEngine.encodeWordInitials("Google Maps"))
        assertEquals("2288", T9SearchEngine.encodeLabel("cat-8"))
    }

    @Test
    fun initialsAndWordBoundaryQueriesMatch() {
        assertEquals(245, T9SearchEngine.scoreMatch("A Big", "20244", "22"))
        assertEquals(200 - 7, T9SearchEngine.scoreMatch("Google Maps", "46645306277", "6"))
        assertNull(T9SearchEngine.scoreMatch("Google Maps", "46645306277", "999"))
    }

    @Test
    fun filterAndRankOrdersMatchesAndLeavesBlankQueryUnfiltered() {
        val apps = listOf("Google Maps", "Gmail", "Calculator")
        val ranked = T9SearchEngine.filterAndRank(
            items = apps,
            queryDigits = "46",
            labelSelector = { it },
            digitsSelector = T9SearchEngine::encodeLabel,
        )

        assertEquals(listOf("Gmail", "Google Maps"), ranked)
        assertEquals(apps, T9SearchEngine.filterAndRank(apps, "  ", { it }, T9SearchEngine::encodeLabel))
    }

    @Test
    fun initialStateIsDeterministicAndKeepsHighScore() {
        val first = SnakeEngine.initialState(highScore = 30, randomSeed = 7L)
        val second = SnakeEngine.initialState(highScore = 30, randomSeed = 7L)

        assertEquals(first, second)
        assertEquals(20, first.width)
        assertEquals(11, first.height)
        assertEquals(3, first.snake.size)
        assertEquals(30, first.highScore)
        assertFalse(first.isGameOver)
        assertTrue(first.food !in first.snake)
    }

    @Test
    fun stepMovesSnakeForward() {
        val state = SnakeState(
            width = 8,
            height = 6,
            snake = listOf(GridPoint(3, 2), GridPoint(2, 2), GridPoint(1, 2)),
            direction = Direction.RIGHT,
            food = GridPoint(7, 5),
        )

        val moved = SnakeEngine.step(state)

        assertEquals(listOf(GridPoint(4, 2), GridPoint(3, 2), GridPoint(2, 2)), moved.snake)
        assertEquals(0, moved.score)
    }

    @Test
    fun eatingFoodIncreasesScoreGrowsSnakeAndUpdatesHighScore() {
        val state = SnakeState(
            width = 8,
            height = 6,
            snake = listOf(GridPoint(3, 2), GridPoint(2, 2), GridPoint(1, 2)),
            direction = Direction.RIGHT,
            food = GridPoint(4, 2),
            highScore = 5,
        )

        val moved = SnakeEngine.step(state) { occupied, _, _ -> GridPoint(7, 5).also { assertFalse(it in occupied) } }

        assertEquals(4, moved.snake.size)
        assertEquals(GridPoint(4, 2), moved.snake.first())
        assertEquals(GridPoint(7, 5), moved.food)
        assertEquals(10, moved.score)
        assertEquals(10, moved.highScore)
    }

    @Test
    fun wallAndSelfCollisionsEndTheGame() {
        val wallState = SnakeState(
            width = 4,
            height = 4,
            snake = listOf(GridPoint(3, 1), GridPoint(2, 1), GridPoint(1, 1)),
            direction = Direction.RIGHT,
            food = GridPoint(0, 0),
        )
        val selfState = SnakeState(
            width = 4,
            height = 4,
            snake = listOf(
                GridPoint(2, 2),
                GridPoint(2, 1),
                GridPoint(1, 1),
                GridPoint(1, 2),
                GridPoint(0, 2),
            ),
            direction = Direction.LEFT,
            food = GridPoint(3, 3),
        )

        assertTrue(SnakeEngine.step(wallState).isGameOver)
        assertTrue(SnakeEngine.step(selfState).isGameOver)
    }

    @Test
    fun steeringCannotReverseIntoTheSnake() {
        val state = SnakeEngine.initialState()

        assertEquals(state, SnakeEngine.steer(state, Direction.LEFT))
        assertEquals(Direction.UP, SnakeEngine.steer(state, Direction.UP).direction)
    }
}
