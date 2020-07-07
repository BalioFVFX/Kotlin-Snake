package entity.food

import entity.Symbol
import entity.food.base.Food

class Apple(x: Int, y: Int) : Food(x, y) {
    override val effect: Effect
        get() = Effect.INCREASE

    override val symbol: Symbol
        get() = Symbol.APPLE
}