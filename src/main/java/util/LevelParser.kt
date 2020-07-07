package util

import entity.Symbol
import entity.solid.Brick
import entity.solid.Grass
import entity.solid.base.SolidEntity
import java.io.BufferedReader
import java.io.FileReader

/**
 * Level reader class that parses the given .txt file to level object
 * @throws NoSuchFileException
 * @throws NumberFormatException
 */
object LevelParser {
    /**
     * Reads the given file.
     * First it parses the width and then the height of the board.
     * Then it reads the board and saves it in 2D Array which contains Solid Entities
     * If there is an unknown character it will be replaced with Grass
     *
     * @param path must contain file extension
     * @return 2D Array which contains Solid Entities
     */
    fun read(path: String): Array<Array<SolidEntity?>> {
        val inputStream = BufferedReader(FileReader(path))
        val cols = inputStream.readLine().toInt()
        val rows = inputStream.readLine().toInt()

        var currentChar = Symbol.GRASS.char
        val entities: Array<Array<SolidEntity?>> = Array(rows) {
            arrayOfNulls<SolidEntity>(cols)
        }

        for (row in 0 until rows) {
            for (col in 0 until cols) {
                currentChar = inputStream.read().toChar()
                if (currentChar == Symbol.BRICK.char) {
                    entities[row][col] = Brick()
                } else {
                    entities[row][col] = Grass()
                }
            }
            inputStream.readLine()
        }

        return entities
    }
}