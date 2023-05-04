package app.ryanm.rollingaway

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import androidx.core.content.res.ResourcesCompat
import kotlin.math.abs
import kotlin.math.round

class World(resources: Resources): Scene {
    private val mapWidth = 20
    private val mapHeight = 43
    private var tileSize: Int = resources.displayMetrics.widthPixels / mapWidth

    private val textPaint = Paint()

    private val player = Player(tileSize.toFloat())

    private val redEnemy = Enemy(tileSize.toFloat(), resources, R.drawable.red_enemy)
    private val blueEnemy = Enemy(tileSize.toFloat(), resources, R.drawable.blue_enemy)
    private val orangeEnemy = Enemy(tileSize.toFloat(), resources, R.drawable.orange_enemy)
    private val pinkEnemy = Enemy(tileSize.toFloat(), resources, R.drawable.pink_enemy)

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

    private val initmap = arrayOf(
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

    private val tilemap: Array<IntArray> = initmap.clone()

    init{
        player.x = 8*tileSize.toFloat()
        player.y = 15*tileSize.toFloat()

        redEnemy.x = 4*tileSize.toFloat()
        redEnemy.y = 21*tileSize.toFloat()

        blueEnemy.x = 3*tileSize.toFloat()
        blueEnemy.y = 21*tileSize.toFloat()

        orangeEnemy.x = 18*tileSize.toFloat()
        orangeEnemy.y = 21*tileSize.toFloat()
        orangeEnemy.direction = Direction.LEFT

        pinkEnemy.x = 16*tileSize.toFloat()
        pinkEnemy.y = 21*tileSize.toFloat()
        pinkEnemy.direction = Direction.LEFT

        textPaint.color = Color.WHITE
        textPaint.textSize = 36f
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
            player.score += 200 // add 200 points for each pellet
            tilemap[tileY][tileX] = 0
        } else if(tilemap[tileY][tileX] == 19) {
            player.pellets += 1 // track how many pellets player has picked up
            player.score += 2000 // add 2000 points for power pellets
            tilemap[tileY][tileX] = 0
        }

        player.rect.left = player.x - player.radius
        player.rect.top = player.y - player.radius
        player.rect.right = player.x + player.radius
        player.rect.bottom = player.y + player.radius
    }

    private fun moveEnemy(enemy: Enemy, targetX: Int, targetY: Int, deltaT: Float) {
        enemy.x = round(enemy.x)
        enemy.y = round(enemy.y)

        val tileX: Int = enemy.x.toInt() * tileSize
        val tileY: Int = enemy.y.toInt() * tileSize

        var changeDir = false

        if(enemy.direction == Direction.RIGHT) {
            if (enemy.x + enemy.speed * deltaT >= tileX*tileSize) {
                changeDir = true
            }
            else {
                enemy.x += enemy.speed * deltaT
            }
        }

        if(enemy.direction == Direction.LEFT) {
            if(enemy.x - enemy.speed * deltaT > tileX*tileSize)
                enemy.x -= enemy.speed * deltaT
            else {
                changeDir = true
            }
        }
        if(enemy.direction == Direction.UP) {
            if (enemy.y - enemy.speed * deltaT > tileY*tileSize)
                enemy.y -= enemy.speed * deltaT
            else {
                changeDir = true
            }
        }
        if(enemy.direction == Direction.DOWN) {
            if (enemy.y + enemy.speed * deltaT > tileY*tileSize)
                changeDir = true
            else {
                enemy.y += enemy.speed * deltaT
            }
        }

        if(changeDir) {
            Log.i("Change Dir:", "True")
            val directions = getAvailableEnemyDirections(enemy.x.toInt(), enemy.y.toInt(), enemy.direction)

            if(directions.isNotEmpty()) {
                enemy.direction = directions[0]

                when(enemy.direction) {
                    Direction.UP -> Log.i("Current:", "Up")
                    Direction.DOWN -> Log.i("Current:", "Down")
                    Direction.LEFT -> Log.i("Current:", "Left")
                    Direction.RIGHT -> Log.i("Current:", "Right")
                }

                when(enemy.direction) {
                    Direction.UP -> {
                        enemy.y -= enemy.speed * deltaT
                    }
                    Direction.DOWN -> {
                        enemy.y += enemy.speed * deltaT
                    }
                    Direction.LEFT -> {
                        enemy.x -= enemy.speed * deltaT
                    }
                    Direction.RIGHT -> {
                        enemy.x += enemy.speed * deltaT
                    }
                }
            }
            else
                Log.i("Directions:", "Is empty")
        }

        if(enemy.x > (mapWidth-1)*tileSize.toFloat()) {
            enemy.x = 0f
            enemy.y = (mapHeight/2)*tileSize.toFloat()
        }
        else if(enemy.x < 0) {
            enemy.x = (mapWidth-1)*tileSize.toFloat()
            enemy.y = ((mapHeight/2)*tileSize).toFloat()
        }

        // Collision rects
        enemy.rect.left = enemy.x - enemy.radius
        enemy.rect.top = enemy.y - enemy.radius
        enemy.rect.right = enemy.rect.left + 2*enemy.radius
        enemy.rect.bottom = enemy.rect.top + 2*enemy.radius
    }

    private fun getAvailableEnemyDirections(x: Int, y: Int, direction: Direction): ArrayList<Direction> {
        val tileX = x / tileSize
        val tileY = y / tileSize
        val directionList = ArrayList<Direction>()

        if(tileX-1 <= 0) {
            if(direction != Direction.RIGHT)
                directionList.add(Direction.LEFT)
        }

        if(tileX-1 > 0) {
            if(tilemap[tileY][tileX-1] == 0 || tilemap[tileY][tileX-1] == 18 || tilemap[tileX][tileY-1] == 19)
                if(direction != Direction.RIGHT)
                    directionList.add(Direction.LEFT)
        }

        if(tileX+1 < mapWidth)
            if(tilemap[tileY][tileX+1] == 0 || tilemap[tileY][tileX+1] == 18 || tilemap[tileY][tileX+1] == 19)
                if(direction != Direction.LEFT)
                    directionList.add(Direction.RIGHT)

        if(tileX+1 >= mapWidth)
            if(direction != Direction.LEFT)
                directionList.add(Direction.RIGHT)

        if(tileY-1 > 0)
            if(tilemap[tileY-1][tileX] == 0 || tilemap[tileY-1][tileX] == 18 || tilemap[tileY-1][tileX] == 19)
                if(direction != Direction.DOWN)
                    directionList.add(Direction.UP)

        if(tileY+1 < tilemap.size)
            if(tilemap[tileY+1][tileX] == 0 || tilemap[tileY+1][tileX] == 18 || tilemap[tileY+1][tileX] == 19)
                if(direction != Direction.UP)
                    directionList.add(Direction.DOWN)

        return directionList
    }

    private fun moveEnemies(deltaT: Float, attribs: GameAttributes) {
        /**
         * Code handling behavior of enemies
         */

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
                blueTargetX = player.x.toInt()/tileSize
                blueTargetY = player.y.toInt()/tileSize
            }
            EnemyState.SCATTER -> { // move towards top right corner
                blueTargetY = 0
                blueTargetX = mapWidth
            }
            EnemyState.FRIGHTENED -> { // move in random directions
                blueTargetX = (0 until mapWidth).random()
                blueTargetY = (tilemap.indices).random()
            }
        }

        val pinkTargetX: Int; val pinkTargetY: Int = when(pinkEnemy.state) {
            EnemyState.PURSUIT -> { player.x.toInt()/tileSize; player.y.toInt()/tileSize }// move towards player
            EnemyState.SCATTER -> { 0; mapWidth }// move towards top right corner
            EnemyState.FRIGHTENED -> { (0 until mapWidth).random(); (tilemap.indices).random() } // move in random directions
        }

        val orangeTargetX: Int; val orangeTargetY: Int = when(orangeEnemy.state) {
            EnemyState.PURSUIT -> { player.x.toInt()/tileSize; player.y.toInt()/tileSize }// move towards player
            EnemyState.SCATTER -> { 0; mapWidth }// move towards top right corner
            EnemyState.FRIGHTENED -> { (0 until mapWidth).random(); (tilemap.indices).random() } // move in random directions
        }

        moveEnemy(redEnemy, redTargetX, redTargetY, deltaT)
        //moveEnemy(blueEnemy, blueTargetTile, deltaT)
        //moveEnemy(pinkEnemy, pinkTargetTile, deltaT)
        //moveEnemy(orangeEnemy, orangeTargetTile, deltaT)

        if(player.rect.intersects(redEnemy.rect.left, redEnemy.rect.top, redEnemy.rect.right, redEnemy.rect.bottom) ||
            player.rect.intersects(blueEnemy.rect.left, blueEnemy.rect.top, blueEnemy.rect.right, blueEnemy.rect.bottom) ||
            player.rect.intersects(pinkEnemy.rect.left, pinkEnemy.rect.top, pinkEnemy.rect.right, pinkEnemy.rect.bottom) ||
            player.rect.intersects(orangeEnemy.rect.left, orangeEnemy.rect.top, orangeEnemy.rect.right, orangeEnemy.rect.bottom)) {
            player.lives--

            player.state = PlayerState.DYING

            if(player.lives < 0) {
                player.state = PlayerState.GAMEOVER
            }
        }
    }

    override fun update(deltaT: Float, attribs: GameAttributes) {
        when(player.state) {
            PlayerState.ALIVE -> {
                movePlayer(deltaT, attribs)
                moveEnemies(deltaT, attribs)
            }
            PlayerState.DYING -> {
                player.x = 8*tileSize.toFloat()
                player.y = 15*tileSize.toFloat()

                redEnemy.x = 4*tileSize.toFloat()
                redEnemy.y = 21*tileSize.toFloat()

                blueEnemy.x = 3*tileSize.toFloat()
                blueEnemy.y = 21*tileSize.toFloat()

                orangeEnemy.x = 18*tileSize.toFloat()
                orangeEnemy.y = 21*tileSize.toFloat()
                orangeEnemy.direction = Direction.LEFT

                pinkEnemy.x = 16*tileSize.toFloat()
                pinkEnemy.y = 21*tileSize.toFloat()
                pinkEnemy.direction = Direction.LEFT

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

        canvas.drawText("X: "+redEnemy.x+" Y: "+redEnemy.y,
            300F, mapHeight*tileSize + 36f, textPaint)
    }
}