package ext

import com.googlecode.lanterna.input.KeyType
import com.googlecode.lanterna.terminal.Terminal

/**
 * Appends text to terminal
 */
fun Terminal.print(text: String) {
    text.forEach { symbol ->
        this.putCharacter(symbol)
    }
}

/**
 * Appends text to terminal and moves the cursor at new line
 */
fun Terminal.println(text: String) {
    text.forEach { symbol ->
        this.putCharacter(symbol)
    }
    this.setCursorPosition(0, this.cursorPosition.row + 1)
}

/**
 * Reads whole line from the terminal while it also provides a feedback by printing the typed character.
 * KeyType.Enter terminates and returns the input.
 * NOTE: No need to call .flush().
 * @return Input from the terminal.
 */
fun Terminal.readline(): String {
    val buffer = StringBuffer()

    var currentKeyStroke = this.readInput()

    while (currentKeyStroke.keyType != KeyType.Enter) {
        if (currentKeyStroke.keyType == KeyType.Character) {
            val character = currentKeyStroke.character
            buffer.append(character)
            this.putCharacter(character)
        } else if (currentKeyStroke.keyType == KeyType.Backspace) {
            if(this.removeLast()){
                buffer.deleteCharAt(this.cursorPosition.column)
            }
        }
        this.flush()
        currentKeyStroke = this.readInput()
    }

    return buffer.toString()
}

/**
 * Removes the last character at the current line
 * @return True if operation was successful otherwise false
 */
fun Terminal.removeLast(): Boolean {
    val xPosition = this.cursorPosition.column
    val yPosition = this.cursorPosition.row

    if (xPosition > 0) {
        val newXPosition = xPosition - 1
        this.setCursorPosition(newXPosition, yPosition)
        this.putCharacter(' ')
        this.setCursorPosition(newXPosition, yPosition)

        return true
    } else {
        return false
    }
}