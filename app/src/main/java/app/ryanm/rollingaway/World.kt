package app.ryanm.rollingaway

import android.content.res.Resources
import android.graphics.Canvas
import androidx.core.content.res.ResourcesCompat
import app.ryanm.rollingaway.character.Player
import kotlin.math.abs

class World(resources: Resources): Scene {
    private val mapWidth = 20
    private val mapHeight = 43
    private var tileSize: Int = resources.displayMetrics.widthPixels / mapWidth

    private val player = Player(tileSize.toFloat())

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

    private val tilemap: Array<Int> = arrayOf(
         5,  1,  1,  1,  1,  1,  1,  1,  1, 14, 13,  1,  1,  1,  1,  1,  1,  1,  1,  6,
         3,  0,  0,  0,  0,  0,  0,  0,  0,  4,  3,  0,  0,  0,  0,  0,  0,  0,  0,  4,
         3,  0,  9,  2,  2,  2,  2, 10,  0,  4,  3,  0,  9,  2,  2,  2,  2, 10,  0,  4,
         3,  0,  4, 13,  1,  1,  1, 12,  0, 11, 12,  0, 11,  1,  1,  1, 14,  3,  0,  4,
         3,  0,  4,  3,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  4,  3,  0,  4,
         3,  0,  4,  3,  0,  9,  2,  2,  2,  2,  2,  2,  2,  2, 10,  0,  4,  3,  0,  4,
         3,  0,  4,  3,  0, 11,  1,  1, 14, 17, 17, 13,  1,  1, 12,  0,  4,  3,  0,  4,
         3,  0,  4,  3,  0,  0,  0,  0,  4, 17, 17,  3,  0,  0,  0,  0,  4,  3,  0,  4,
         3,  0,  4,  3,  0,  9, 10,  0, 11,  1,  1, 12,  0,  9, 10,  0,  4,  3,  0,  4,
         3,  0,  4,  3,  0,  4,  3,  0,  0,  0,  0,  0,  0,  4,  3,  0,  4,  3,  0,  4,
         3,  0,  4,  3,  0,  4,  3,  0,  9,  2,  2, 10,  0,  4,  3,  0,  4,  3,  0,  4,
         3,  0,  4,  3,  0,  4,  3,  0, 11,  1,  1, 12,  0,  4,  3,  0,  4,  3,  0,  4,
         3,  0,  4,  3,  0,  4,  3,  0,  0,  0,  0,  0,  0,  4,  3,  0,  4,  3,  0,  4,
         3,  0,  4,  3,  0,  4,  3,  0,  9,  2,  2, 10,  0,  4,  3,  0,  4,  3,  0,  4,
         3,  0, 11, 12,  0,  4,  3,  0, 11,  1,  1, 12,  0,  4,  3,  0, 11, 12,  0,  4,
         3,  0,  0,  0,  0,  4,  3,  0,  0,  0,  0,  0,  0,  4,  3,  0,  0,  0,  0,  4,
         7,  2,  2, 10,  0,  4, 15,  2,  2,  2,  2,  2,  2, 16,  3,  0,  9,  2,  2,  8,
         0,  0,  0,  3,  0,  4, 13,  1,  1,  1,  1,  1,  1, 14,  3,  0,  4,  0,  0,  0,
         0,  0,  0,  3,  0,  4,  3,  0,  0,  0,  0,  0,  0,  4,  3,  0,  4,  0,  0,  0,
         0,  0,  0,  3,  0,  4,  3,  0,  9,  2,  2, 10,  0,  4,  3,  0,  4,  0,  0,  0,
         1,  1,  1, 12,  0, 11, 12,  0,  4, 17, 17,  3,  0, 11, 12,  0, 11,  1,  1,  1, // Center
         0,  0,  0,  0,  0,  0,  0,  0,  4, 17, 17,  3,  0,  0,  0,  0,  0,  0,  0,  0, // Center
         2,  2,  2, 10,  0,  9, 10,  0,  4, 17, 17,  3,  0,  9, 10,  0,  9,  2,  2,  2, // Center
         0,  0,  0,  3,  0,  4,  3,  0, 11,  1,  1, 12,  0,  4,  3,  0,  4,  0,  0,  0,
         0,  0,  0,  3,  0,  4,  3,  0,  0,  0,  0,  0,  0,  4,  3,  0,  4,  0,  0,  0,
         0,  0,  0,  3,  0,  4, 15,  2,  2,  2,  2,  2,  2, 16,  3,  0,  4,  0,  0,  0,
         5,  1,  1, 12,  0,  4, 13,  1,  1,  1,  1,  1,  1, 14,  3,  0, 11,  1,  1,  6,
         3,  0,  0,  0,  0,  4,  3,  0,  0,  0,  0,  0,  0,  4,  3,  0,  0,  0,  0,  4,
         3,  0,  9, 10,  0,  4,  3,  0,  9,  2,  2, 10,  0,  4,  3,  0,  9, 10,  0,  4,
         3,  0,  4,  3,  0,  4,  3,  0, 11,  1,  1, 12,  0,  4,  3,  0,  4,  3,  0,  4,
         3,  0,  4,  3,  0,  4,  3,  0,  0,  0,  0,  0,  0,  4,  3,  0,  4,  3,  0,  4,
         3,  0,  4,  3,  0,  4,  3,  0,  9,  2,  2, 10,  0,  4,  3,  0,  4,  3,  0,  4,
         3,  0,  4,  3,  0,  4,  3,  0, 11,  1,  1, 12,  0,  4,  3,  0,  4,  3,  0,  4,
         3,  0,  4,  3,  0,  4,  3,  0,  0,  0,  0,  0,  0,  4,  3,  0,  4,  3,  0,  4,
         3,  0,  4,  3,  0, 11, 12,  0,  9,  2,  2, 10,  0, 11, 12,  0,  4,  3,  0,  4,
         3,  0,  4,  3,  0,  0,  0,  0,  4, 17, 17,  3,  0,  0,  0,  0,  4,  3,  0,  4,
         3,  0,  4,  3,  0,  9,  2,  2, 16, 17, 17, 15,  2,  2, 10,  0,  4,  3,  0,  4,
         3,  0,  4,  3,  0, 11,  1,  1,  1,  1,  1,  1,  1,  1, 12,  0,  4,  3,  0,  4,
         3,  0,  4,  3,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  4,  3,  0,  4,
         3,  0,  4, 15,  2,  2,  2, 10,  0,  9, 10,  0,  9,  2,  2,  2, 16,  3,  0,  4,
         3,  0, 11,  1,  1,  1,  1, 12,  0,  4,  3,  0, 11,  1,  1,  1,  1, 12,  0,  4,
         3,  0,  0,  0,  0,  0,  0,  0,  0,  4,  3,  0,  0,  0,  0,  0,  0,  0,  0,  4,
         7,  2,  2,  2,  2,  2,  2,  2,  2, 16, 15,  2,  2,  2,  2,  2,  2,  2,  2,  8,
    )

    private fun getTileX(tileIndex: Int): Int {
        return (tileIndex % mapWidth) * tileSize
    }

    private fun getTileY(tileIndex: Int): Int {
        return (tileIndex / mapWidth) * tileSize
    }


    override fun update(deltaT: Float, attribs: GameAttributes) {
        val deltaX = -attribs.xRot * player.speed * deltaT
        val deltaY =  attribs.yRot * player.speed * deltaT

        var tileIndex: Int = player.y.toInt() / tileSize * mapWidth + player.x.toInt() / tileSize

        if(tileIndex < 0)
            tileIndex = 0
        else if(tileIndex >= tilemap.size)
            tileIndex = tilemap.size - 1

        var leftBlocked = true
        var rightBlocked = true
        var upBlocked = true
        var downBlocked = true

        if(tileIndex-1 < 0 || tileIndex % mapWidth == 0) // If no tile exists to the left of the current tile
            leftBlocked = false
        else if(tilemap[tileIndex-1] == 0) // If the tile to the left is not blank
            leftBlocked = false

        if(tileIndex+1 >= tilemap.size || tileIndex % mapWidth == mapWidth-1 ) // If no tile exists to the right
            rightBlocked = false
        else if(tilemap[tileIndex+1] == 0)
            rightBlocked = false

        if(tileIndex-mapWidth < 0) // If no tile exists above current tile
            upBlocked = false
        else if(tilemap[tileIndex-mapWidth] == 0) // If tile above current tile is not blank
            upBlocked = false

        if(tileIndex+mapWidth >= tilemap.size) // If no tile exists below current tile
            downBlocked = false
        else if(tilemap[tileIndex+mapWidth] == 0) // If tile below is blank
            downBlocked = false

        if(leftBlocked && deltaX < 0 && player.x + deltaX > getTileX(tileIndex)) // If prev tile is blocked but there's space to move on current tile
            leftBlocked = false

        if(rightBlocked && deltaX > 0 && player.x + deltaX < getTileX(tileIndex)) // If next tile is blocked but there's space to move on current tile
            rightBlocked = false

        if(upBlocked && deltaY < 0 && player.y + deltaY > getTileY(tileIndex)) // If prev tile is blocked but there's space to move on current tile
            upBlocked = false

        if(downBlocked && deltaY > 0 && player.y + deltaY < getTileY(tileIndex)) // If next tile is blocked but there's space to move on current tile
            downBlocked = false

        if((!downBlocked || !upBlocked) && (abs(player.x-getTileX(tileIndex)) > abs(deltaX))) {// If up or down movement isn't blocked but player isn't centered on tile
            if (!downBlocked)
                downBlocked = true
            if (!upBlocked)
                upBlocked = true
        }

        if((!leftBlocked || !rightBlocked) && (abs(player.y-getTileY(tileIndex)) > abs(deltaY))) {// If left or right movement isn't blocked but player isn't centered on tile
            if (!leftBlocked)
                leftBlocked = true
            if (!rightBlocked)
                rightBlocked = true
        }

        if(abs(deltaX) > abs(deltaY)) { // If tilted more in x direction than y direction
            if((!leftBlocked && deltaX < 0) || (!rightBlocked && deltaX > 0)) // If player can move left or right
                player.x += deltaX
            else
                if((!upBlocked && deltaY < 0) || (!downBlocked && deltaY > 0)) // If player can move up or down
                        player.y += deltaY
        } else { // If tilted more in y direction than x direction
            if((!upBlocked && deltaY < 0) || (!downBlocked && deltaY > 0)) // If player can move up or down
                player.y += deltaY
            else
                if((!leftBlocked && deltaX < 0) || (!rightBlocked && deltaX > 0)) // If player can move left or right
                    player.x += deltaX
        }

        if(abs(player.x-getTileX(tileIndex)) < abs(deltaX))
            player.x = getTileX(tileIndex).toFloat()

        if(abs(player.y-getTileY(tileIndex)) < abs(deltaY))
            player.y = getTileY(tileIndex).toFloat()

        if(player.x > (mapWidth-1)*tileSize.toFloat()) {
            player.x = 0f
            player.y = getTileY((mapHeight/2)*mapWidth).toFloat()
        }
        else if(player.x < 0) {
            player.x = (mapWidth-1)*tileSize.toFloat()
            player.y = getTileY((mapHeight / 2)*mapWidth).toFloat()
        }

        if(player.y > mapHeight*tileSize.toFloat())
            player.y = 0f
        if(player.y < 0)
            player.y = mapHeight*tileSize.toFloat()

        player.rect.left = player.x - player.radius
        player.rect.right = player.x + player.radius
        player.rect.top = player.y - player.radius
        player.rect.bottom = player.y + player.radius
    }

    override fun render(canvas: Canvas) {
        for(i in tilemap.indices) {
            val x = i%mapWidth * tileSize
            val y = i/mapWidth * tileSize

            when (tilemap[i]) {
                1 -> {
                    straightWall?.setBounds(x, y, x+tileSize, y+tileSize)

                    canvas.save()
                    canvas.rotate(90f, x+tileSize/2f, y+tileSize/2f)
                    straightWall?.draw(canvas)
                    canvas.restore()
                }
                2 -> {
                    straightWall?.setBounds(x, y, x+tileSize, y+tileSize)

                    canvas.save()
                    canvas.rotate(270f, x+tileSize/2f, y+tileSize/2f)
                    straightWall?.draw(canvas)
                    canvas.restore()
                }
                3 -> {
                    straightWall?.setBounds(x, y, x+tileSize, y+tileSize)
                    straightWall?.draw(canvas)
                }
                4 -> {
                    straightWall?.setBounds(x, y, x+tileSize, y+tileSize)

                    canvas.save()
                    canvas.rotate(180f, x+tileSize/2f, y+tileSize/2f)
                    straightWall?.draw(canvas)
                    canvas.restore()
                }
                5 -> {
                    topLeftCorner?.setBounds(x, y, x+tileSize, y+tileSize)
                    topLeftCorner?.draw(canvas)
                }
                6 -> {
                    topRightCorner?.setBounds(x, y, x+tileSize, y+tileSize)
                    topRightCorner?.draw(canvas)
                }
                7 -> {
                    bottomLeftCorner?.setBounds(x, y, x+tileSize, y+tileSize)
                    bottomLeftCorner?.draw(canvas)
                }
                8 -> {
                    bottomRightCorner?.setBounds(x, y, x+tileSize, y+tileSize)
                    bottomRightCorner?.draw(canvas)
                }
                9 -> {
                    islandTopLeft?.setBounds(x, y, x+tileSize, y+tileSize)
                    islandTopLeft?.draw(canvas)
                }
                10 -> {
                    islandTopRight?.setBounds(x, y, x+tileSize, y+tileSize)
                    islandTopRight?.draw(canvas)
                }
                11 -> {
                    islandBottomLeft?.setBounds(x, y, x+tileSize, y+tileSize)
                    islandBottomLeft?.draw(canvas)
                }
                12 -> {
                    islandBottomRight?.setBounds(x, y, x+tileSize, y+tileSize)
                    islandBottomRight?.draw(canvas)
                }
                13 -> {
                    islandInnerTopLeft?.setBounds(x, y, x+tileSize, y+tileSize)
                    islandInnerTopLeft?.draw(canvas)
                }
                14 -> {
                    islandInnerTopRight?.setBounds(x, y, x+tileSize, y+tileSize)
                    islandInnerTopRight?.draw(canvas)
                }
                15 -> {
                    islandInnerBottomLeft?.setBounds(x, y, x+tileSize, y+tileSize)
                    islandInnerBottomLeft?.draw(canvas)
                }
                16 -> {
                    islandInnerBottomRight?.setBounds(x, y, x+tileSize, y+tileSize)
                    islandInnerBottomRight?.draw(canvas)
                }
                17 -> {
                    islandSolid?.setBounds(x, y, x+tileSize, y+tileSize)
                    islandSolid?.draw(canvas)
                }
            }
        }

        player.render(canvas)
    }
}