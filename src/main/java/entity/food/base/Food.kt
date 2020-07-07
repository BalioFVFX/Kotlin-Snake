package entity.food.base

import entity.food.Effect
import entity.snake.Snake
import entity.solid.base.SolidEntity

/**
 * Base class for Food
 * Every food has it's own unique effect
 */
abstract class Food(val x: Int, val y: Int, private var _isEaten: Boolean = false) : SolidEntity() {

    abstract val effect: Effect

    override fun collideWith(snake: Snake) {
        snake.eat(this)
        _isEaten = true
    }

    val isEaten
        get() = _isEaten
}