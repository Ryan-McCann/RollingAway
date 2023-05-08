package app.ryanm.rollingaway

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.core.content.res.ResourcesCompat
import kotlin.math.abs
import kotlin.math.sqrt

class World(resources: Resources): Scene {
    private val mapWidth = 20
    private val mapHeight = 43
    private var tileSize: Int = resources.displayMetrics.widthPixels / mapWidth

    private val textPaint = Paint()

    private val player = Player(tileSize.toFloat())
    private var level: Int = 1

    private val redEnemy = Enemy(tileSize.toFloat(), resources, R.drawable.red_enemy)
    private val blueEnemy = Enemy(tileSize.toFloat(), resources, R.drawable.blue_enemy)
    private val orangeEnemy = Enemy(tileSize.toFloat(), resources, R.drawable.orange_enemy)
    private val pinkEnemy = Enemy(tileSize.toFloat(), resources, R.drawable.pink_enemy)

    private var chaseTimer = 20f
    private var scatterTimer = 7f
    private var frightenedTimer = 0f

    private val straightWall = ResourcesCompat.getDrawable(resources, R.drawable.straight_wall, null)
    private val topLeftCorner = ResourcesCompat.getDrawable(resources, R.drawable.top_left_corner, null)
    private val topRightCorner = ResourcesCompat.getDrawable(resources, R.drawable.top_right_corner, null)
    private val bottomLeftCorner = ResourcesCompat.getDrawable(resources, R.drawable.bottom_left_corner, null)
    private val bottomRightCorner = ResourcesCompat.getDrawable(resources, R.drawable.bottom_right_corner, null)
    private val islandTopLeft = ResourcesCompat.getDrawable(resources, R.drawable.island_top_left, null)
    private val islandTopRight = ResourcesCompat.getDrawable(resources, R.drawable.island_top_right, null)
    private val islandBottomLeft = ResourcesCompat.getDrawable(resources, R.drawable.island_bottom_left, null)
    private val islandBottomRight = ResourcesCompat.getDrawable(resources, R.drawable.island_bottom_right, null)
    private val islandInnerTopLeft = ResourcesCompat.getDrawable(resources, R.drawable.island_inner_top_left, null)
    private val islandInnerTopRight = ResourcesCompat.getDrawable(resources, R.drawable.island_inner_top_right, null)
    private val islandInnerBottomLeft = ResourcesCompat.getDrawable(resources, R.drawable.island_inner_bottom_left, null)
    private val islandInnerBottomRight = ResourcesCompat.getDrawable(resources, R.drawable.island_inner_bottom_right, null)
    private val islandSolid = ResourcesCompat.getDrawable(resources, R.drawable.island_solid, null)
    private val ball = ResourcesCompat.getDrawable(resources, R.drawable.ball, null)
    private val powerBall = ResourcesCompat.getDrawable(resources, R.drawable.power_ball, null)

    private var tilemap: Array<IntArray> = arrayOf()

    private val initmap: Array<IntArray> = arrayOf(
        intArrayOf(5,  1,  1,  1,  1,  1,  1,  1,  1, 14, 13,  1,  1,  1,  1,  1,  1,  1,  1,  6),
        intArrayOf(3, 19, 18, 18, 18, 18, 18, 18, 18,  4,  3, 18, 18, 18, 18, 18, 18, 18, 19,  4),
        intArrayOf(3, 18,  9,  2,  2,  2,  2, 10, 18,  4,  3, 18,  9,  2,  2,  2,  2, 10, 18,  4),
        intArrayOf(3, 18,  4, 13,  1,  1,  1, 12, 18, 11, 12, 18, 11,  1,  1,  1, 14,  3, 18,  4),
        intArrayOf(3, 18,  4,  3, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18,  4,  3, 18,  4),
        intArrayOf(3, 18,  4,  3, 18,  9,  2,  2,  2,  2,  2,  2,  2,  2, 10, 18,  4,  3, 18,  4),
        intArrayOf(3, 18,  4,  3, 18, 11,  1,  1, 14, 17, 17, 13,  1,  1, 12, 18,  4,  3, 18,  4),
        intArrayOf(3, 18,  4,  3, 18, 18, 18, 18,  4, 17, 17,  3, 18, 18, 18, 18,  4,  3, 18,  4),
        intArrayOf(3, 18,  4,  3, 18,  9, 10, 18, 11,  1,  1, 12, 18,  9, 10, 18,  4,  3, 18,  4),
        intArrayOf(3, 18,  4,  3, 18,  4,  3, 18, 18, 18, 18, 18, 18,  4,  3, 18,  4,  3, 18,  4),
        intArrayOf(3, 18,  4,  3, 18,  4,  3, 18,  9,  2,  2, 10, 18,  4,  3, 18,  4,  3, 18,  4),
        intArrayOf(3, 18,  4,  3, 18,  4,  3, 18, 11,  1,  1, 12, 18,  4,  3, 18,  4,  3, 18,  4),
        intArrayOf(3, 18,  4,  3, 18,  4,  3, 18, 18, 18, 18, 18, 18,  4,  3, 18,  4,  3, 18,  4),
        intArrayOf(3, 18,  4,  3, 18,  4,  3, 18,  9,  2,  2, 10, 18,  4,  3, 18,  4,  3, 18,  4),
        intArrayOf(3, 18, 11, 12, 18,  4,  3,  0, 11,  1,  1, 12,  0,  4,  3, 18, 11, 12, 18,  4),
        intArrayOf(3, 18, 18, 18, 18,  4,  3,  0,  0,  0,  0,  0,  0,  4,  3, 18, 18, 18, 18,  4),
        intArrayOf(7,  2,  2, 10, 18,  4, 15,  2,  2,  2,  2,  2,  2, 16,  3, 18,  9,  2,  2,  8),
        intArrayOf(0,  0,  0,  3, 18,  4, 13,  1,  1,  1,  1,  1,  1, 14,  3, 18,  4,  0,  0,  0),
        intArrayOf(0,  0,  0,  3, 18,  4,  3, 18, 18, 18, 18, 18, 18,  4,  3, 18,  4,  0,  0,  0),
        intArrayOf(0,  0,  0,  3, 18,  4,  3, 18,  9,  2,  2, 10, 18,  4,  3, 18,  4,  0,  0,  0),
        intArrayOf(1,  1,  1, 12, 18, 11, 12, 18,  4, 17, 17,  3, 18, 11, 12, 18, 11,  1,  1,  1), // Center
        intArrayOf(0,  0,  0,  0, 18, 18, 18, 18,  4, 17, 17,  3, 18, 18, 18, 18,  0,  0,  0,  0),
        intArrayOf(2,  2,  2, 10, 18,  9, 10, 18,  4, 17, 17,  3, 18,  9, 10, 18,  9,  2,  2,  2),
        intArrayOf(0,  0,  0,  3, 18,  4,  3, 18, 11,  1,  1, 12, 18,  4,  3, 18,  4,  0,  0,  0),
        intArrayOf(0,  0,  0,  3, 18,  4,  3, 18, 18, 18, 18, 18, 18,  4,  3, 18,  4,  0,  0,  0),
        intArrayOf(0,  0,  0,  3, 18,  4, 15,  2,  2,  2,  2,  2,  2, 16,  3, 18,  4,  0,  0,  0),
        intArrayOf(5,  1,  1, 12, 18,  4, 13,  1,  1,  1,  1,  1,  1, 14,  3, 18, 11,  1,  1,  6),
        intArrayOf(3, 18, 18, 18, 18,  4,  3, 18, 18, 18, 18, 18, 18,  4,  3, 18, 18, 18, 18,  4),
        intArrayOf(3, 18,  9, 10, 18,  4,  3, 18,  9,  2,  2, 10, 18,  4,  3, 18,  9, 10, 18,  4),
        intArrayOf(3, 18,  4,  3, 18,  4,  3, 18, 11,  1,  1, 12, 18,  4,  3, 18,  4,  3, 18,  4),
        intArrayOf(3, 18,  4,  3, 18,  4,  3, 18, 18, 18, 18, 18, 18,  4,  3, 18,  4,  3, 18,  4),
        intArrayOf(3, 18,  4,  3, 18,  4,  3, 18,  9,  2,  2, 10, 18,  4,  3, 18,  4,  3, 18,  4),
        intArrayOf(3, 18,  4,  3, 18,  4,  3, 18, 11,  1,  1, 12, 18,  4,  3, 18,  4,  3, 18,  4),
        intArrayOf(3, 18,  4,  3, 18,  4,  3, 18, 18, 18, 18, 18, 18,  4,  3, 18,  4,  3, 18,  4),
        intArrayOf(3, 18,  4,  3, 18, 11, 12, 18,  9,  2,  2, 10, 18, 11, 12, 18,  4,  3, 18,  4),
        intArrayOf(3, 18,  4,  3, 18, 18, 18, 18,  4, 17, 17,  3, 18, 18, 18, 18,  4,  3, 18,  4),
        intArrayOf(3, 18,  4,  3, 18,  9,  2,  2, 16, 17, 17, 15,  2,  2, 10, 18,  4,  3, 18,  4),
        intArrayOf(3, 18,  4,  3, 18, 11,  1,  1,  1,  1,  1,  1,  1,  1, 12, 18,  4,  3, 18,  4),
        intArrayOf(3, 18,  4,  3, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18,  4,  3, 18,  4),
        intArrayOf(3, 18,  4, 15,  2,  2,  2, 10, 18,  9, 10, 18,  9,  2,  2,  2, 16,  3, 18,  4),
        intArrayOf(3, 18, 11,  1,  1,  1,  1, 12, 18,  4,  3, 18, 11,  1,  1,  1,  1, 12, 18,  4),
        intArrayOf(3, 19, 18, 18, 18, 18, 18, 18, 18,  4,  3, 18, 18, 18, 18, 18, 18, 18, 19,  4),
        intArrayOf(7,  2,  2,  2,  2,  2,  2,  2,  2, 16, 15,  2,  2,  2,  2,  2,  2,  2,  2,  8),
    )

    init{
        resetMaze()
        resetCharacters()

        textPaint.color = Color.WHITE
        textPaint.textSize = 36f
    }

    private fun resetChaseTimer() {
        chaseTimer = 20f
    }

    private fun resetScatterTimer() {
        scatterTimer = 7f
    }

    private fun resetFrightenedTimer() {
        frightenedTimer = 10f
    }

    private fun resetMaze() {
        tilemap = initmap.map { it.clone() }.toTypedArray()
        player.pellets = 0
    }
    private fun resetCharacters() {
        player.x = 8*tileSize.toFloat()
        player.y = 15*tileSize.toFloat()

        redEnemy.x = 3*tileSize.toFloat()
        redEnemy.tileX = 3
        redEnemy.nextTileX = 4
        redEnemy.y = 21*tileSize.toFloat()
        redEnemy.tileY = 21
        redEnemy.nextTileY = 21
        redEnemy.movingToTile = true
        redEnemy.direction = Direction.RIGHT

        blueEnemy.x = 2*tileSize.toFloat()
        blueEnemy.tileX = 2
        blueEnemy.nextTileX = 3
        blueEnemy.y = 21*tileSize.toFloat()
        blueEnemy.tileY = 21
        blueEnemy.nextTileY = 21
        blueEnemy.movingToTile = true
        blueEnemy.direction = Direction.RIGHT

        orangeEnemy.x = 18*tileSize.toFloat()
        orangeEnemy.tileX = 18
        orangeEnemy.nextTileX = 17
        orangeEnemy.y = 21*tileSize.toFloat()
        orangeEnemy.tileY = 21
        orangeEnemy.nextTileY = 21
        orangeEnemy.movingToTile = true
        orangeEnemy.direction = Direction.LEFT

        pinkEnemy.x = 16*tileSize.toFloat()
        pinkEnemy.tileX = 16
        pinkEnemy.nextTileX = 16
        pinkEnemy.y = 21*tileSize.toFloat()
        pinkEnemy.tileY = 21
        pinkEnemy.nextTileY = 21
        pinkEnemy.movingToTile = true
        pinkEnemy.direction = Direction.LEFT
    }

    private fun movePlayer(deltaT: Float, attribs: GameAttributes) {
        /**
         * Start of code to update player's movement based on which tile the player is currently on
         * as well as which direction the user's phone is tilted.
         */

        val deltaX = -attribs.xRot * player.speed * deltaT
        val deltaY =  attribs.yRot * player.speed * deltaT

        var leftBlocked = true
        var rightBlocked = true
        var upBlocked = true
        var downBlocked = true

        val tileX = player.x.toInt() / tileSize
        val tileY = player.y.toInt() / tileSize

        if(tileX-1 < 0) // If no tile exists to the left of the current tile
            leftBlocked = false
        else if(tilemap[tileY][tileX-1] == 0 || tilemap[tileY][tileX-1] == 18 || tilemap[tileY][tileX-1] == 19) // If the tile to the left is blank
            leftBlocked = false

        if(tileX+1 >= mapWidth) // If no tile exists to the right
            rightBlocked = false
        else if(tilemap[tileY][tileX+1] == 0|| tilemap[tileY][tileX+1] == 18 || tilemap[tileY][tileX+1] == 19)
            rightBlocked = false

        if(tileY-1 < 0) // If no tile exists above current tile
            upBlocked = false
        else if(tilemap[tileY-1][tileX] == 0 || tilemap[tileY-1][tileX] == 18 || tilemap[tileY-1][tileX] == 19) // If tile above current tile is not blank
            upBlocked = false

        if(tileY+1 >= tilemap.size) // If no tile exists below current tile
            downBlocked = false
        else if(tilemap[tileY+1][tileX] == 0 || tilemap[tileY+1][tileX] == 18 || tilemap[tileY+1][tileX] == 19) // If tile below is blank
            downBlocked = false

        if(leftBlocked && deltaX < 0 && player.x + deltaX > tileX * tileSize) // If prev tile is blocked but there's space to move on current tile
            leftBlocked = false

        if(rightBlocked && deltaX > 0 && player.x + deltaX < tileX * tileSize) // If next tile is blocked but there's space to move on current tile
            rightBlocked = false

        if(upBlocked && deltaY < 0 && player.y + deltaY > tileY * tileSize) // If prev tile is blocked but there's space to move on current tile
            upBlocked = false

        if(downBlocked && deltaY > 0 && player.y + deltaY < tileY * tileSize) // If next tile is blocked but there's space to move on current tile
            downBlocked = false

        if((!downBlocked || !upBlocked) && (abs(player.x-tileX*tileSize) > abs(deltaX))) {// If up or down movement isn't blocked but player isn't centered on tile
            if (!downBlocked)
                downBlocked = true
            if (!upBlocked)
                upBlocked = true
        }

        if((!leftBlocked || !rightBlocked) && (abs(player.y-tileY*tileSize) > abs(deltaY))) {// If left or right movement isn't blocked but player isn't centered on tile
            if (!leftBlocked)
                leftBlocked = true
            if (!rightBlocked)
                rightBlocked = true
        }

        if(abs(deltaX) > abs(deltaY)) { // If tilted more in x direction than y direction
            if((!leftBlocked && deltaX < 0) || (!rightBlocked && deltaX > 0)) {// If player can move left or right
                player.x += deltaX

                if(deltaX > 0)
                    player.direction = Direction.LEFT
                else
                    player.direction = Direction.RIGHT
            }
            else
                if((!upBlocked && deltaY < 0) || (!downBlocked && deltaY > 0)) {// If player can move up or down
                    player.y += deltaY

                    if(deltaY > 0)
                        player.direction = Direction.DOWN
                    else
                        player.direction = Direction.UP
                }
        } else { // If tilted more in y direction than x direction
            if((!upBlocked && deltaY < 0) || (!downBlocked && deltaY > 0)) {// If player can move up or down
                player.y += deltaY

                if(deltaY > 0)
                    player.direction = Direction.DOWN
                else
                    player.direction = Direction.UP
            }
            else
                if((!leftBlocked && deltaX < 0) || (!rightBlocked && deltaX > 0)) {// If player can move left or right
                    player.x += deltaX

                    if(deltaX > 0)
                        player.direction = Direction.LEFT
                    else
                        player.direction = Direction.RIGHT
                }
        }

        if(abs(player.x-tileX*tileSize) < abs(deltaX))
            player.x = tileX*tileSize.toFloat()

        if(abs(player.y-tileY*tileSize) < abs(deltaY))
            player.y = tileY*tileSize.toFloat()

        if(player.x > (mapWidth-1)*tileSize.toFloat()) {
            player.x = 0f
            player.y = (mapHeight/2 * tileSize).toFloat()
        }
        else if(player.x < 0) {
            player.x = (mapWidth-1)*tileSize.toFloat()
            player.y = (mapHeight / 2*tileSize).toFloat()
        }

        if(player.y > mapHeight*tileSize.toFloat())
            player.y = 0f
        if(player.y < 0)
            player.y = mapHeight*tileSize.toFloat()

        /**
         * Code handling collision between player and pellets
         */
        if(tilemap[tileY][tileX] == 18) {
            player.pellets += 1 // track how many pellets player has picked up
            player.score += 10 // add 200 points for each pellet
            tilemap[tileY][tileX] = 0
        } else if(tilemap[tileY][tileX] == 19) { // If power pellet picked up
            player.pellets += 1 // track how many pellets player has picked up
            player.score += 50 // add 2000 points for power pellets
            tilemap[tileY][tileX] = 0

            // Set enemies to frightened state
            redEnemy.state = EnemyState.FRIGHTENED
            blueEnemy.state = EnemyState.FRIGHTENED
            pinkEnemy.state = EnemyState.FRIGHTENED
            orangeEnemy.state = EnemyState.FRIGHTENED

            // Reset frightenedTimer
            resetFrightenedTimer()
        }

        if(player.pellets >= 280) {
            level += 1
            resetMaze()
            resetCharacters()
        }

        player.rect.left = player.x - player.radius
        player.rect.top = player.y - player.radius
        player.rect.right = player.x + player.radius
        player.rect.bottom = player.y + player.radius
    }

    private fun moveEnemy(enemy: Enemy, targetX: Int, targetY: Int, deltaT: Float) {
        val delta = enemy.speed * deltaT

        if(enemy.movingToTile) {
            when(enemy.direction) {
                Direction.UP -> {
                    if(enemy.y > enemy.nextTileY * tileSize)
                        enemy.y -= delta
                    else {
                        enemy.movingToTile = false
                        enemy.y = enemy.nextTileY * tileSize.toFloat()
                        enemy.tileY = enemy.nextTileY
                    }
                }
                Direction.DOWN -> {
                    if(enemy.y < enemy.nextTileY * tileSize) {
                        enemy.y += delta
                    } else {
                        enemy.movingToTile = false
                        enemy.y = enemy.nextTileY * tileSize.toFloat()
                        enemy.tileY = enemy.nextTileY
                    }
                }
                Direction.LEFT -> {
                    if(enemy.x > enemy.nextTileX * tileSize)
                        enemy.x -= delta
                    else {
                        enemy.movingToTile = false
                        enemy.x = enemy.nextTileX * tileSize.toFloat()
                        enemy.tileX = enemy.nextTileX
                    }
                }
                Direction.RIGHT -> {
                    if(enemy.x < enemy.nextTileX * tileSize) {
                        enemy.x += delta
                    } else {
                        enemy.movingToTile = false
                        enemy.x = enemy.nextTileX * tileSize.toFloat()
                        enemy.tileX = enemy.nextTileX
                    }
                }
            }
        }

        if(enemy.x > (mapWidth-1)*tileSize) {
            enemy.x = 0f
            enemy.tileX = 0
            enemy.nextTileX = 1
        }
        else if(enemy.x < 0) {
            enemy.x = (mapWidth - 1) * tileSize.toFloat()
            enemy.tileX = mapWidth-1
            enemy.nextTileX = mapWidth-2
        }

        if(enemy.y > (tilemap.size-1)*tileSize)
            enemy.y = 0f
        else if(enemy.y < 0)
            enemy.y = (tilemap.size-1)*tileSize.toFloat()

        if(!enemy.movingToTile) {
            val directions = getAvailableEnemyDirections(enemy.tileX, enemy.tileY, enemy.direction)

            if(directions.size == 1) { // Only one direction available
                enemy.direction = directions[0]
            }
            else { // multiple directions available, choose best direction based on target
                var distance = 1000

                for(direction in directions) {
                    when(direction) {
                        Direction.UP -> {
                            val tempDist = getTileDistance(enemy.tileX, enemy.tileY-1, targetX, targetY)
                            if(tempDist <= distance) {
                                distance = tempDist
                                enemy.direction = direction
                            }
                        }
                        Direction.DOWN -> {
                            val tempDist = getTileDistance(enemy.tileX, enemy.tileY+1, targetX, targetY)
                            if(tempDist < distance) {
                                distance = tempDist
                                enemy.direction = direction
                            } else if(tempDist == distance) {
                                if(enemy.direction == Direction.RIGHT)
                                    enemy.direction = direction
                            }
                        }
                        Direction.LEFT -> {
                            val tempDist = getTileDistance(enemy.tileX-1, enemy.tileY, targetX, targetY)
                            if(tempDist < distance) {
                                distance = tempDist
                                enemy.direction = direction
                            } else if(tempDist == distance) {
                                if(enemy.direction == Direction.RIGHT || enemy.direction == Direction.DOWN) {
                                    enemy.direction = direction
                                }
                            }
                        }
                        Direction.RIGHT -> {
                            val tempDist = getTileDistance(enemy.tileX+1, enemy.tileY, targetX, targetY)
                            if(tempDist < distance) {
                                distance = tempDist
                                enemy.direction = direction
                            }
                        }
                    }
                }
            }

            when(enemy.direction) {
                Direction.UP -> {
                    if(enemy.tileY - 1 < 0)
                        enemy.nextTileY = tilemap.size - 1
                    else
                        enemy.nextTileY = enemy.tileY - 1
                }
                Direction.DOWN -> {
                    if(enemy.tileY + 1 >= tilemap.size)
                        enemy.nextTileY = 0
                    else
                        enemy.nextTileY = enemy.tileY + 1
                }
                Direction.LEFT -> {
                    if(enemy.tileX - 1 < 0)
                        enemy.nextTileX = mapWidth - 1
                    else
                        enemy.nextTileX = enemy.tileX - 1
                }
                Direction.RIGHT -> {
                    if(enemy.tileX + 1 >= mapWidth)
                        enemy.nextTileX = 0
                    else
                        enemy.nextTileX = enemy.tileX + 1
                }
            }

            enemy.movingToTile = true
        }

        // Collision rects
        enemy.rect.left = enemy.x - enemy.radius
        enemy.rect.top = enemy.y - enemy.radius
        enemy.rect.right = enemy.rect.left + 2*enemy.radius
        enemy.rect.bottom = enemy.rect.top + 2*enemy.radius
    }

    private fun getTileDistance(tile1X: Int, tile1Y: Int, tile2X: Int, tile2Y: Int): Int {
        val x = (tile2X - tile1X).toFloat()
        val y = (tile2Y - tile1Y).toFloat()

        return sqrt(x*x + y*y).toInt()
    }

    private fun getAvailableEnemyDirections(tileX: Int, tileY: Int, direction: Direction): ArrayList<Direction> {
        val directionList = ArrayList<Direction>()

        val upTileY = tileY - 1

        val leftTileX = tileX - 1

        val downTileY = tileY + 1

        val rightTileX = tileX + 1

        if(upTileY >= 0)
            if(direction != Direction.DOWN)
                if(tilemap[upTileY][tileX] == 0 || tilemap[upTileY][tileX] == 18 || tilemap[upTileY][tileX] == 19)
                    directionList.add(Direction.UP)

        if(leftTileX >= 0) {
            if (direction != Direction.RIGHT)
                if (tilemap[tileY][leftTileX] == 0 || tilemap[tileY][leftTileX] == 18 || tilemap[tileY][leftTileX] == 19)
                    directionList.add(Direction.LEFT)
        } else
            if(direction != Direction.RIGHT)
                directionList.add(Direction.LEFT)

        if (downTileY < tilemap.size)
            if (direction != Direction.UP)
                if (tilemap[downTileY][tileX] == 0 || tilemap[downTileY][tileX] == 18 || tilemap[downTileY][tileX] == 19)
                    directionList.add(Direction.DOWN)

        if(rightTileX < mapWidth) {
            if (direction != Direction.LEFT)
                if (tilemap[tileY][rightTileX] == 0 || tilemap[tileY][rightTileX] == 18 || tilemap[tileY][rightTileX] == 19)
                    directionList.add(Direction.RIGHT)
        } else
            if(direction != Direction.LEFT)
                directionList.add(Direction.RIGHT)

        return directionList
    }

    private fun checkCollision(enemy: Enemy): Boolean {
        if(abs(player.x - enemy.x) < tileSize*2)
            if(abs(player.y - enemy.y) < tileSize*2)
                return player.rect.intersects(enemy.rect.left, enemy.rect.top, enemy.rect.right, enemy.rect.bottom)

        return false
    }

    private fun handleCollision(enemy: Enemy) {
        if(checkCollision(enemy)) {
            if(enemy.state != EnemyState.FRIGHTENED) {
                player.lives--

                player.state = PlayerState.DYING

                if (player.lives <= 0) {
                    player.state = PlayerState.GAMEOVER
                }
            } else {
                player.score += 400
            }
        }
    }

    private fun moveEnemies(deltaT: Float) {
        /**
         * Code handling behavior of enemies
         */

        if(frightenedTimer > 0) {
            frightenedTimer -= deltaT
            if(frightenedTimer <= 0) {
                redEnemy.state = EnemyState.SCATTER
                blueEnemy.state = EnemyState.SCATTER
                pinkEnemy.state = EnemyState.SCATTER
                orangeEnemy.state = EnemyState.SCATTER

                resetScatterTimer()
            }
        } else if(scatterTimer > 0) {
            scatterTimer -= deltaT
            if(scatterTimer <= 0) {
                redEnemy.state = EnemyState.PURSUIT
                blueEnemy.state = EnemyState.PURSUIT
                pinkEnemy.state = EnemyState.PURSUIT
                orangeEnemy.state = EnemyState.PURSUIT

                resetChaseTimer()
            }
        } else if(chaseTimer > 0) {
            chaseTimer -= deltaT
            if(chaseTimer <= 0) {
                redEnemy.state = EnemyState.SCATTER
                blueEnemy.state = EnemyState.SCATTER
                pinkEnemy.state = EnemyState.SCATTER
                orangeEnemy.state = EnemyState.SCATTER

                resetScatterTimer()
            }
        }

        val redTargetX: Int; val redTargetY: Int
        when(redEnemy.state) {
            EnemyState.PURSUIT -> { // move towards player
                redTargetX = player.x.toInt()/tileSize
                redTargetY = player.y.toInt()/tileSize
            }
            EnemyState.SCATTER -> { // move towards top right corner
                redTargetX = mapWidth
                redTargetY = 0
            }
            EnemyState.FRIGHTENED -> {  // move in random directions
                redTargetX = (0 until mapWidth).random()
                redTargetY = (tilemap.indices).random()
            }
        }

        val blueTargetX: Int; val blueTargetY: Int
        when(blueEnemy.state) { // move towards player
            EnemyState.PURSUIT -> {
                val redX: Int
                val redY: Int
                val playerX: Int
                val playerY: Int
                when(player.direction) {
                    Direction.UP -> {
                        playerX = player.x.toInt()/tileSize
                        playerY = player.y.toInt()/tileSize - 2
                    }
                    Direction.DOWN -> {
                        playerX = player.x.toInt()/tileSize
                        playerY = player.y.toInt()/tileSize + 2
                    }
                    Direction.LEFT -> {
                        playerX = player.x.toInt()/tileSize - 2
                        playerY = player.y.toInt()/tileSize
                    }
                    Direction.RIGHT -> {
                        playerX = player.x.toInt()/tileSize + 2
                        playerY = player.y.toInt()/tileSize
                    }
                }

                redX = playerX - redEnemy.tileX
                redY = playerY - redEnemy.tileY

                blueTargetX = if(redX+playerX in 0 until mapWidth)
                    redX + playerX
                else if(redX+playerX < 0)
                    0
                else if(redX+playerX >= mapWidth)
                    mapWidth - 1
                else
                    0

                blueTargetY = if(redY+playerY in tilemap.indices)
                    redY + playerY
                else if(redY+playerY < 0)
                    0
                else if(redY+playerY >= tilemap.size)
                    tilemap.size - 1
                else 0

            }
            EnemyState.SCATTER -> { // move towards bottom right corner
                blueTargetX = mapWidth
                blueTargetY = tilemap.size
            }
            EnemyState.FRIGHTENED -> { // move in random directions
                blueTargetX = (0 until mapWidth).random()
                blueTargetY = (tilemap.indices).random()
            }
        }

        val pinkTargetX: Int; val pinkTargetY: Int
        when(pinkEnemy.state) {
            EnemyState.PURSUIT -> { // move towards player
                when(player.direction) {
                    Direction.UP -> {
                        pinkTargetX = player.x.toInt() / tileSize
                        val playerTileY = player.y.toInt() / tileSize

                        pinkTargetY = if(playerTileY - 4 > 0)
                            playerTileY - 4
                        else
                            0
                    }
                    Direction.DOWN -> {
                        pinkTargetX = player.x.toInt() / tileSize
                        val playerTileY = player.y.toInt() / tileSize

                        pinkTargetY = if(playerTileY + 4 < tilemap.size)
                            playerTileY + 4
                        else
                            tilemap.size-1
                    }
                    Direction.LEFT -> {
                        val playerTileX = player.x.toInt() / tileSize
                        pinkTargetY = player.y.toInt() / tileSize

                        pinkTargetX = if(playerTileX - 4 > 0)
                            playerTileX - 4
                        else
                            0
                    }
                    Direction.RIGHT -> {
                        val playerTileX = player.x.toInt() / tileSize
                        pinkTargetY = player.y.toInt() / tileSize

                        pinkTargetX = if(playerTileX + 4 < mapWidth)
                            playerTileX + 2
                        else
                            mapWidth-1
                    }
                }
            }
            EnemyState.SCATTER -> { // move towards top left corner
                pinkTargetX = 0
                pinkTargetY = 0
            }
            EnemyState.FRIGHTENED -> { // move in random directions
                pinkTargetX = (0 until mapWidth).random()
                pinkTargetY = (tilemap.indices).random()
            }
        }

        val orangeTargetX: Int; val orangeTargetY: Int
        when(orangeEnemy.state) {
            EnemyState.PURSUIT -> {
                val playerX = player.x.toInt()/tileSize
                val playerY = player.y.toInt()/tileSize

                if(sqrt((playerX-orangeEnemy.tileX)*(playerX-orangeEnemy.tileX).toFloat()+(playerY-orangeEnemy.tileY)*(playerY-orangeEnemy.tileY)) >= 8) {
                    orangeTargetX = playerX
                    orangeTargetY = playerY
                } else {
                    orangeTargetX = 0
                    orangeTargetY = tilemap.size
                }
            }// move towards player
            EnemyState.SCATTER -> {
                orangeTargetX = 0
                orangeTargetY = tilemap.size
            }// move towards bottom left corner
            EnemyState.FRIGHTENED -> {
                orangeTargetX = (0 until mapWidth).random()
                orangeTargetY = (tilemap.indices).random()
            } // move in random directions
        }

        moveEnemy(redEnemy, redTargetX, redTargetY, deltaT)
        moveEnemy(blueEnemy, blueTargetX, blueTargetY, deltaT)
        moveEnemy(pinkEnemy, pinkTargetX, pinkTargetY, deltaT)
        moveEnemy(orangeEnemy, orangeTargetX, orangeTargetY, deltaT)

        handleCollision(redEnemy)
        handleCollision(orangeEnemy)
        handleCollision(pinkEnemy)
        handleCollision(blueEnemy)
    }

    override fun update(deltaT: Float, attribs: GameAttributes) {
        when(player.state) {
            PlayerState.ALIVE -> {
                movePlayer(deltaT, attribs)
                moveEnemies(deltaT)
            }
            PlayerState.DYING -> {
                resetCharacters()
                player.state = PlayerState.ALIVE
            }
            PlayerState.GAMEOVER -> {

            }
        }
    }

    override fun render(canvas: Canvas) {
        for(y in tilemap.indices) {
            for(x in tilemap[y].indices) {
                when (tilemap[y][x]) {
                    1 -> {
                        straightWall?.setBounds(x*tileSize, y*tileSize, x*tileSize + tileSize, y*tileSize + tileSize)

                        canvas.save()
                        canvas.rotate(90f, x*tileSize + tileSize / 2f, y*tileSize + tileSize / 2f)
                        straightWall?.draw(canvas)
                        canvas.restore()
                    }
                    2 -> {
                        straightWall?.setBounds(x*tileSize, y*tileSize, x*tileSize + tileSize, y*tileSize + tileSize)

                        canvas.save()
                        canvas.rotate(270f, x*tileSize + tileSize / 2f, y*tileSize + tileSize / 2f)
                        straightWall?.draw(canvas)
                        canvas.restore()
                    }
                    3 -> {
                        straightWall?.setBounds(x*tileSize, y*tileSize, x*tileSize + tileSize, y*tileSize + tileSize)
                        straightWall?.draw(canvas)
                    }
                    4 -> {
                        straightWall?.setBounds(x*tileSize, y*tileSize, x*tileSize + tileSize, y*tileSize + tileSize)

                        canvas.save()
                        canvas.rotate(180f, x*tileSize + tileSize / 2f, y*tileSize + tileSize / 2f)
                        straightWall?.draw(canvas)
                        canvas.restore()
                    }
                    5 -> {
                        topLeftCorner?.setBounds(x*tileSize, y*tileSize, x*tileSize + tileSize, y*tileSize + tileSize)
                        topLeftCorner?.draw(canvas)
                    }
                    6 -> {
                        topRightCorner?.setBounds(x*tileSize, y*tileSize, x*tileSize + tileSize, y*tileSize + tileSize)
                        topRightCorner?.draw(canvas)
                    }
                    7 -> {
                        bottomLeftCorner?.setBounds(x*tileSize, y*tileSize, x*tileSize + tileSize, y*tileSize + tileSize)
                        bottomLeftCorner?.draw(canvas)
                    }
                    8 -> {
                        bottomRightCorner?.setBounds(x*tileSize, y*tileSize, x*tileSize + tileSize, y*tileSize + tileSize)
                        bottomRightCorner?.draw(canvas)
                    }
                    9 -> {
                        islandTopLeft?.setBounds(x*tileSize, y*tileSize, x*tileSize + tileSize, y*tileSize + tileSize)
                        islandTopLeft?.draw(canvas)
                    }
                    10 -> {
                        islandTopRight?.setBounds(x*tileSize, y*tileSize, x*tileSize + tileSize, y*tileSize + tileSize)
                        islandTopRight?.draw(canvas)
                    }
                    11 -> {
                        islandBottomLeft?.setBounds(x*tileSize, y*tileSize, x*tileSize + tileSize, y*tileSize + tileSize)
                        islandBottomLeft?.draw(canvas)
                    }
                    12 -> {
                        islandBottomRight?.setBounds(x*tileSize, y*tileSize, x*tileSize + tileSize, y*tileSize + tileSize)
                        islandBottomRight?.draw(canvas)
                    }
                    13 -> {
                        islandInnerTopLeft?.setBounds(x*tileSize, y*tileSize, x*tileSize + tileSize, y*tileSize + tileSize)
                        islandInnerTopLeft?.draw(canvas)
                    }
                    14 -> {
                        islandInnerTopRight?.setBounds(x*tileSize, y*tileSize, x*tileSize + tileSize, y*tileSize + tileSize)
                        islandInnerTopRight?.draw(canvas)
                    }
                    15 -> {
                        islandInnerBottomLeft?.setBounds(x*tileSize, y*tileSize, x*tileSize + tileSize, y*tileSize + tileSize)
                        islandInnerBottomLeft?.draw(canvas)
                    }
                    16 -> {
                        islandInnerBottomRight?.setBounds(x*tileSize, y*tileSize, x*tileSize + tileSize, y*tileSize + tileSize)
                        islandInnerBottomRight?.draw(canvas)
                    }
                    17 -> {
                        islandSolid?.setBounds(x*tileSize, y*tileSize, x*tileSize + tileSize, y*tileSize + tileSize)
                        islandSolid?.draw(canvas)
                    }
                    18 -> {
                        ball?.setBounds(x*tileSize, y*tileSize, x*tileSize + tileSize, y*tileSize + tileSize)
                        ball?.draw(canvas)
                    }
                    19 -> {
                        powerBall?.setBounds(x*tileSize, y*tileSize, x*tileSize + tileSize, y*tileSize + tileSize)
                        powerBall?.draw(canvas)
                    }
                }
            }
        }

        player.render(canvas)

        redEnemy.render(canvas)
        orangeEnemy.render(canvas)
        blueEnemy.render(canvas)
        pinkEnemy.render(canvas)

        canvas.drawText("Score: "+player.score, 0f, mapHeight*tileSize + 36f, textPaint)
        canvas.drawText("Lives: "+player.lives, mapWidth.toFloat()/2f*tileSize, mapHeight*tileSize+36f, textPaint)
        canvas.drawText("Level: $level", mapWidth.toFloat()*tileSize-100, mapHeight*tileSize+36f, textPaint)
        canvas.drawText("Pellets: "+player.pellets, 0f, mapHeight*tileSize + 72f, textPaint)
    }
}