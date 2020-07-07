package entity.food

import entity.Symbol
import entity.food.base.Food

class Pear(x: Int, y: Int) : Food(x, y) {
    override val effect: Effect
        get() = Effect.REVERSE
    override val symbol: Symbol
        get() = Symbol.PEAR
}