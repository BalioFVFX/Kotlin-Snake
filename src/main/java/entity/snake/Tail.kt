package entity.snake

import entity.Symbol
import entity.base.Entity

/**
 * Class that represents the entity.snake Tail with X,Y positions
 */
class Tail(val x: Int, val y: Int) : Entity() {

    override val symbol: Symbol
        get() = Symbol.SNAKE
}