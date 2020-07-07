# Kotlin Snake 🐍

### Technologies
The Kotlin programming language and the Lanterna library which is used for printing on the terminal.

---

#### Introduction
The board consists of:
* Bricks ('#') 🧱
* Grass (' ') 🟩
* Apple ('o') 🍎
* Pear ('d') 🍐

The snake is walking on top of the board and is represented by the ('*') character. Also the snake's head has a green color 🐍

----
### In details...

The Brick, Grass, Apple and the Pear are classes that inherit the SolidEntity class so every one of them has a collideWith(snake).
The Brick takes 100 health points from the snake while the Grass takes 0 health points.

Each food has it's very own effect. For example when the apple is eaten the snake tail increases but when the pear is eaten the
snake direction changes.

In order to play the game you only need to provide a 2D array with solid entities (which can be created with the LevelParser) to the Game class.
The Game class will create the snake at appropriate position and after that the game starts!  💥

---

### Designin your own levels

Game level is any .txt file which follows the following criterias:

1. The first line represents the width (Integer)
2. The second line represents the height (Integer)
3. The game field must be surrounded by walls, you can place additional walls inside the field by your design.

Example:

```
30
10
##############################
#                            #
#    ######                  #
#         #              #   #
#         #              #   #
#                        #   #
#                            #
#                            #
#                            #
##############################
```

----

### How to play

1. Download the project then put your level inside the root directory of the project. 📩
2. Compile 📊
3. Enter the file name with it's file extension type (or use the provided level1.txt) 📝
4. Play 🎮
