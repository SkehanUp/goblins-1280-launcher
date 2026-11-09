package com.goblins.launcher1280.analysis

import java.util.Random

enum class Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT;

    fun isOpposite(other: Direction): Boolean = when (this) {
        UP -> other == DOWN
        DOWN -> other == UP
        LEFT -> other == RIGHT
        RIGHT -> other == LEFT
    }
}

data class GridPoint(val x: Int, val y: Int)

data class SnakeState(
    val width: Int = 20,
    val height: Int = 11,
    val snake: List<GridPoint>,
    val direction: Direction,
    val food: GridPoint,
    val score: Int = 0,
    val highScore: Int = 0,
    val isGameOver: Boolean = false,
    val isPaused: Boolean = false,
)

object SnakeEngine {
    fun initialState(
        width: Int = 20,
        height: Int = 11,
        highScore: Int = 0,
        randomSeed: Long = 42L,
    ): SnakeState {
        require(width >= 3 && height >= 1 && width * height > 3) {
            "The board must fit a three-segment snake and at least one food cell"
        }

        val centerX = maxOf(2, width / 2)
        val centerY = height / 2
        val snake = listOf(
            GridPoint(centerX, centerY),
            GridPoint(centerX - 1, centerY),
            GridPoint(centerX - 2, centerY),
        )
        val food = randomFoodPicker(snake.toSet(), width, height, randomSeed)

        return SnakeState(
            width = width,
            height = height,
            snake = snake,
            direction = Direction.RIGHT,
            food = food,
            highScore = highScore,
        )
    }

    fun steer(state: SnakeState, requested: Direction): SnakeState {
        if (state.isGameOver || state.isPaused || state.direction.isOpposite(requested)) return state
        return state.copy(direction = requested)
    }

    fun step(
        state: SnakeState,
        nextFoodPicker: (Set<GridPoint>, Int, Int) -> GridPoint = ::defaultFoodPicker,
    ): SnakeState {
        if (state.isGameOver || state.isPaused) return state

        val head = state.snake.first()
        val nextHead = when (state.direction) {
            Direction.UP -> GridPoint(head.x, head.y - 1)
            Direction.DOWN -> GridPoint(head.x, head.y + 1)
            Direction.LEFT -> GridPoint(head.x - 1, head.y)
            Direction.RIGHT -> GridPoint(head.x + 1, head.y)
        }

        if (nextHead.x !in 0 until state.width || nextHead.y !in 0 until state.height) {
            return state.copy(isGameOver = true)
        }

        val ateFood = nextHead == state.food
        val collisionBody = if (ateFood) state.snake else state.snake.dropLast(1)
        if (nextHead in collisionBody) return state.copy(isGameOver = true)

        val movedSnake = buildList {
            add(nextHead)
            addAll(state.snake.take(if (ateFood) state.snake.size else state.snake.lastIndex))
        }

        if (!ateFood) return state.copy(snake = movedSnake)

        val newScore = state.score + 10
        val highScore = maxOf(state.highScore, newScore)
        val occupied = movedSnake.toSet()
        val availableCells = state.width.toLong() * state.height - occupied.size
        if (availableCells <= 0) {
            return state.copy(
                snake = movedSnake,
                score = newScore,
                highScore = highScore,
                isGameOver = true,
            )
        }

        val nextFood = nextFoodPicker(occupied, state.width, state.height)
        require(nextFood.x in 0 until state.width && nextFood.y in 0 until state.height) {
            "Food picker returned a point outside the board"
        }
        require(nextFood !in occupied) { "Food picker returned an occupied point" }

        return state.copy(
            snake = movedSnake,
            food = nextFood,
            score = newScore,
            highScore = highScore,
        )
    }

    fun defaultFoodPicker(occupied: Set<GridPoint>, width: Int, height: Int): GridPoint {
        require(width > 0 && height > 0) { "Board dimensions must be positive" }
        for (y in 0 until height) {
            for (x in 0 until width) {
                val point = GridPoint(x, y)
                if (point !in occupied) return point
            }
        }
        throw IllegalArgumentException("No unoccupied cell is available for food")
    }

    private fun randomFoodPicker(
        occupied: Set<GridPoint>,
        width: Int,
        height: Int,
        randomSeed: Long,
    ): GridPoint {
        val available = buildList {
            for (y in 0 until height) {
                for (x in 0 until width) {
                    val point = GridPoint(x, y)
                    if (point !in occupied) add(point)
                }
            }
        }
        return available[Random(randomSeed).nextInt(available.size)]
    }
}
