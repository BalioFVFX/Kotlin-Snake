package game

import Constants
import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.input.KeyType
import com.googlecode.lanterna.terminal.Terminal
import entity.Representable
import entity.Symbol
import entity.food.Apple
import entity.food.Effect
import entity.food.Pear
import entity.food.base.Food
import entity.snake.Snake
import entity.snake.Tail
import entity.solid.Grass
import entity.solid.base.SolidEntity
import exception.InvalidBoardException
import ext.println
import java.util.*
import kotlin.random.Random

class Game(private val terminal: Terminal) {

    private var currentX = 0
    private var currentY = 0
    private var lastKeyType = KeyType.ArrowRight
    private var currentKeyType = lastKeyType
    private var board: Array<Array<SolidEntity?>> = emptyArray()
    private lateinit var snake: Snake
    private var food: Food? = null
    private var foodCounter = 0
    private val pressedKeysQueue = ArrayDeque<KeyType>()
    private var maxSnakeLength = 0

    /**
     * The only method that is needed to start any level of the game.
     * A game level is any 2D array that contains Solid Entities
     *
     * ######################
     * #                    #
     * #                    #
     * #                    #
     * #                    #
     * #                    #
     * #                    #
     * #                    #
     * ######################
     *
     *
     */
    fun startLevel(level: Array<Array<SolidEntity?>>) {
        board = level
        maxSnakeLength = board.flatten().filter { it?.symbol == Symbol.GRASS }.count()
        createSnake()

        currentX = snake.tail.first().x
        currentY = snake.tail.first().y

        while (snake.isAlive() && !isGameWin()) {
            currentKeyType = getLatestInput() ?: lastKeyType
            calculateDirection(currentKeyType).also { processed ->
                if (!processed) {
                    currentKeyType = lastKeyType
                }
            }

            snake.move(currentX, currentY)
            checkCollision()
            spawnFoodIfNeeded()
            redraw()

            lastKeyType = currentKeyType
            foodCounter++
            Thread.sleep(DEFAULT_SPEED)
        }

        terminal.setCursorPosition(0, board.size)

        if (isGameWin()) {
            terminal.setForegroundColor(WIN_COLOR)
            terminal.println(WIN_MESSAGE)
        } else {
            terminal.setForegroundColor(GAME_OVER_COLOR)
            terminal.println(GAME_OVER)
        }

        terminal.flush()
        Thread.sleep(END_SCREEN_LENGTH)
        terminal.clearScreen()
        terminal.setCursorPosition(0, 0)
        terminal.flush()
    }

    private fun redraw() {
        clearScene()
        draw()
        terminal.flush()
    }

    /**
     * Gets the latest input from the terminal's stream while it prevents spamming the same keys over and over again.
     *
     * Example:
     *
     * RIGHT RIGHT RIGHT TOP will result:
     * RIGHT TOP
     */
    private fun getLatestInput(): KeyType? {
        var keyType = terminal.pollInput()?.keyType

        while (keyType != null) {
            pressedKeysQueue.contains(keyType).also { containsIt ->
                if (!containsIt) {
                    pressedKeysQueue.addLast(keyType)
                }
            }
            keyType = terminal.pollInput()?.keyType
        }

        return pressedKeysQueue.firstOrNull()?.also { pressedKeysQueue.removeFirst() }
    }

    /**
     * Checks for collisions. While everything that is a Solid Object such as GRASS, BRICK, APPLE, PEAR can collide
     * with the snake only the BRICK is taking 100HP from the snake, the grass does 0 damage. The apple increases
     * the snake length by 1 and the pear reverses the direction of the snake
     */
    private fun checkCollision() {
        board[currentY][currentX]?.collideWith(snake)

        if (food?.isEaten == true && food?.effect == Effect.REVERSE) {
            reverseControls(snake.tail.elementAt(0), snake.tail.elementAt(1))
        }
    }

    private fun isGameWin(): Boolean {
        return snake.size == maxSnakeLength
    }

    /**
     * When the Pear is eaten this method is called. It reverses the controls
     */
    private fun reverseControls(head: Tail, tail: Tail) {
        val newKeyType = if (head.x > tail.x) {
            KeyType.ArrowRight
        } else if (head.x < tail.x) {
            KeyType.ArrowLeft
        } else {
            if (head.y > tail.y) {
                KeyType.ArrowDown
            } else {
                KeyType.ArrowUp
            }
        }

        currentX = head.x
        currentY = head.y

        pressedKeysQueue.clear()
        pressedKeysQueue.add(newKeyType)
        currentKeyType = newKeyType
        lastKeyType = newKeyType
    }

    /**
     * This method is responsible for spawning food on the board
     * If the food was eaten it will spawn it no matter what. Otherwise the food keeps it position on the board
     * while it's time to disappear based on the FOOD_SPAWN_RATIO
     *
     * Every 5th food is a Pear
     *
     * The food spawns at random position
     */
    private fun spawnFoodIfNeeded() {
        if (food?.isEaten == true || foodCounter % FOOD_SPAWN_RATIO == 0) {
            foodCounter = 0

            food?.let {
                board[it.y][it.x] = Grass() // Remove the old food
            }

            var foodXPosition = 0
            var foodYPosition = 0
            var hasFoodSpawnCollision = true

            // While the spawned food collides with a Brick or the Snake generate new positions
            while (hasFoodSpawnCollision) {

                hasFoodSpawnCollision = false

                if (isGameWin()) {
                    return
                }

                foodXPosition = Random.nextInt(1, board[0].size)
                foodYPosition = Random.nextInt(1, board.size)

                if (board[foodYPosition][foodXPosition]?.symbol != Symbol.GRASS) {
                    hasFoodSpawnCollision = true
                    continue
                }

                snake.tail.forEach { tail ->
                    if (tail.x == foodXPosition && tail.y == foodYPosition) {
                        hasFoodSpawnCollision = true
                        return@forEach
                    }
                }
            }

            // Use the generated position for the Food and make an Apple or a Pear
            if (Random.nextInt(PEAR_PROBABILITY) == 0) {
                food = Pear(foodXPosition, foodYPosition) // pear
            } else {
                food = Apple(foodXPosition, foodYPosition)
            }

            board[foodYPosition][foodXPosition] = food
        }
    }

    /**
     * This method changes the currentX and currentY based on the keyType parameter
     * while it also validates the input. For example if the last pressed key was Arrow.Left
     * this means that the snake is going to the left and if the keyType parameter is Arrow.Right
     * that will not change the currentX and currentY values so the snake will keep travelling on the
     * same direction it was travelling before.
     *
     * @param keyType The key that is currently pressed
     *
     * @return True if the keyType is valid and if it changed the currentX and currentY values, otherwise false
     */
    private fun calculateDirection(keyType: KeyType): Boolean {
        return when (keyType) {
            KeyType.ArrowLeft -> tryToMoveLeft()
            KeyType.ArrowUp -> tryToMoveUp()
            KeyType.ArrowRight -> tryToMoveRight()
            KeyType.ArrowDown -> tryToMoveDown()
            else -> false
        }
    }

    private fun tryToMoveLeft(): Boolean {
        if (lastKeyType == KeyType.ArrowRight) {
            currentX++
            return false
        }
        currentX--
        return true
    }

    private fun tryToMoveRight(): Boolean {
        if (lastKeyType == KeyType.ArrowLeft) {
            currentX--
            return false
        }
        currentX++
        return true
    }

    private fun tryToMoveUp(): Boolean {
        if (lastKeyType == KeyType.ArrowDown) {
            currentY++
            return false
        }
        currentY--
        return true
    }

    private fun tryToMoveDown(): Boolean {
        if (lastKeyType == KeyType.ArrowUp) {
            currentY--
            return false
        }
        currentY++
        return true
    }

    /**
     * This method clears the screen and resets the cursor positions to 0,0
     */
    private fun clearScene() {
        terminal.clearScreen()
        terminal.setCursorPosition(0, 0)
    }

    /**
     * This method draws items that are displayed on the board and
     * after that it draws the snake on top of it
     */
    private fun draw() {
        board.forEachIndexed { y, arrayOfSolidEntitys ->
            arrayOfSolidEntitys.forEachIndexed { x, solidEntity ->
                draw(x, y, solidEntity)
            }
        }

        val tailIterator = snake.tail.iterator()

        terminal.setForegroundColor(SNAKE_HEAD_COLOR)

        tailIterator.next().also {
            draw(it.x, it.y, it)
        }

        terminal.setForegroundColor(DEFAULT_COLOR)
        while (tailIterator.hasNext()) {
            tailIterator.next().also {
                draw(it.x, it.y, it)
            }
        }
    }

    /**
     * Basic drawing method
     * If the representable is null it will draw a GRASS instead
     * @param x Position
     * @param y Position
     * @param representable Your object
     */
    private fun draw(x: Int, y: Int, representable: Representable?) {
        terminal.setCursorPosition(x, y)
        terminal.putCharacter(representable?.symbol?.char ?: Symbol.GRASS.char)
    }

    /**
     * The board must be created before running this method
     * This method will search for free space to place the snake
     * The snake will be placed horizontally with it's length meaning:
     *
     *  "###############
     *  "###  # #***   #
     *  "#             #
     *  "#             #
     *  "###############
     */
    private fun createSnake() {
        val rows = board.size
        val cols = board[0].size

        val tail = Array(DEFAULT_SNAKE_LENGTH) { Tail(0, 0) }
        var tailCreated = false

        for (y in 1 until rows) {
            for (x in 1 until cols step DEFAULT_SNAKE_LENGTH) {
                if (x + DEFAULT_SNAKE_LENGTH > cols) {
                    break
                }
                for (tailPosition in 0 until DEFAULT_SNAKE_LENGTH) {
                    if (board[y][x + tailPosition]?.symbol == Symbol.BRICK) {
                        tailCreated = false
                        break
                    }
                    tail[DEFAULT_SNAKE_LENGTH - 1 - tailPosition] = Tail(x + tailPosition, y)
                    tailCreated = true
                }
                if (tailCreated) {
                    snake = Snake(tail)
                    return
                }
            }
        }
        throw InvalidBoardException(Constants.INVALID_BOARD)
    }

    companion object {
        private const val DEFAULT_SPEED = 250L
        private const val DEFAULT_SNAKE_LENGTH = 3
        private const val FOOD_SPAWN_RATIO = 10
        private val SNAKE_HEAD_COLOR = TextColor.ANSI.GREEN
        private val DEFAULT_COLOR = TextColor.ANSI.DEFAULT
        private val GAME_OVER_COLOR = TextColor.ANSI.RED
        private val WIN_COLOR = TextColor.ANSI.GREEN
        private const val GAME_OVER = "GAME OVER!"
        private const val END_SCREEN_LENGTH = 2500L
        private const val WIN_MESSAGE = "YOU WIN!"
        private const val PEAR_PROBABILITY = 5
    }
}