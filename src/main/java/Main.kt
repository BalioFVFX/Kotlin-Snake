import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import entity.solid.base.SolidEntity
import exception.InvalidBoardException
import ext.println
import ext.readline
import game.Game
import util.LevelParser

const val SLEEP_TIME = 3000L
fun main() {
    val terminal = DefaultTerminalFactory().createTerminal()
    terminal.enterPrivateMode()

    terminal.println(Constants.TAKE_FILE_NAME)
    terminal.flush()

    val fileName = terminal.readline()
    val entities: Array<Array<SolidEntity?>>

    try {
        entities = LevelParser.read(fileName)
    } catch (ex: Exception) {
        terminal.println(Constants.INVALID_FILE)
        terminal.flush()

        Thread.sleep(SLEEP_TIME)
        terminal.close()
        return
    }

    terminal.setCursorVisible(false)

    val game = Game(terminal)

    try {
        game.startLevel(entities)
    } catch (boardException: InvalidBoardException) {
        terminal.clearScreen()
        terminal.println(boardException.message ?: Constants.DEFAULT_ERROR_MESSAGE)
        Thread.sleep(SLEEP_TIME)
    } finally {
        terminal.exitPrivateMode()
        terminal.close()
    }
}