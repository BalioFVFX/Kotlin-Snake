package entity.snake

import entity.Symbol
import entity.base.Entity
import entity.food.Effect
import entity.food.base.Food
import java.util.*

class Snake(body: Array<Tail>) : Entity() {

    private val _tail = ArrayDeque<Tail>()
    private var healthPoints = DEFAULT_HEALTH_POINTS
    private var oldX = 0
    private var oldY = 0

    val tail: Iterable<Tail>
        get() = _tail.asIterable()

    val size: Int
        get() = _tail.size

    init {
        _tail.addAll(body)
    }

    override val symbol: Symbol
        get() = Symbol.SNAKE

    fun eat(food: Food) {
        when (food.effect) {
            Effect.INCREASE -> increaseTail()
            Effect.REVERSE -> reverseTail()
        }
    }

    fun move(x: Int, y: Int) {
        _tail.addFirst(Tail(x, y))
        _tail.removeLast().also { oldTail ->
            oldX = oldTail.x
            oldY = oldTail.y
        }
        checkCollision()
    }

    fun isAlive(): Boolean {
        return healthPoints > 0
    }

    fun takeDamage(damage: Int) {
        healthPoints -= damage
    }

    private fun increaseTail() {
        _tail.addLast(Tail(oldX, oldY))
    }

    private fun reverseTail() {
        _tail.reversed().also { reversed ->
            _tail.clear()
            _tail.addAll(reversed)
        }
    }

    private fun checkCollision() {
        val tailIterator = _tail.iterator()
        val head = tailIterator.next()

        var currentHead: Tail
        while (tailIterator.hasNext()) {
            currentHead = tailIterator.next()

            if (head.x == currentHead.x && head.y == currentHead.y) {
                healthPoints = 0
                return
            }
        }
    }

    companion object {
        private const val DEFAULT_HEALTH_POINTS = 100
    }
}