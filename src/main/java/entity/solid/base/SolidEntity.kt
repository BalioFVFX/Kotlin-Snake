package entity.solid.base

import entity.base.Entity
import entity.snake.Snake

/**
 * Base class for everything that can collide with Snake such as Brick Grass and Food
 */
abstract class SolidEntity : Entity(){
    abstract fun collideWith(snake: Snake)
}