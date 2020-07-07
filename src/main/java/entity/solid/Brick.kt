package entity.solid

import entity.Symbol
import entity.base.Entity
import entity.snake.Snake
import entity.solid.base.SolidEntity

class Brick() : SolidEntity() {
    override fun collideWith(snake: Snake) {
        snake.takeDamage(DAMAGE)
}

override val symbol: Symbol
    get() = Symbol.BRICK

    companion object {
        private const val DAMAGE = 100
    }
}