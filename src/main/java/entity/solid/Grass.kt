package entity.solid

import entity.Symbol
import entity.snake.Snake
import entity.solid.base.SolidEntity

class Grass : SolidEntity() {

    override fun collideWith(snake: Snake) {
        snake.takeDamage(DAMAGE)
    }

    override val symbol: Symbol
        get() = Symbol.GRASS

    companion object {
        private const val DAMAGE = 0
    }
}