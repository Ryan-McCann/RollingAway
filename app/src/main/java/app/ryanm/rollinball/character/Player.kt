package app.ryanm.rollinball.character

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import app.ryanm.rollinball.GameState
import kotlin.math.abs

class Player: Character {
    private var x = 0f
    private var y = 0f
    private var radius = 30f
    private var speed = 25f

    override fun update(gameState: GameState) {
        val width = gameState.screenWidth
        val height = gameState.screenHeight

        if(abs(gameState.xRot) > abs(gameState.yRot) && abs(gameState.xRot) > 0.01)
            x += gameState.xRot * speed
        else if(abs(gameState.yRot) > abs(gameState.xRot) && abs(gameState.yRot) > 0.01)
            y -= gameState.yRot * speed

        if(x > width)
            x = 0f
        else if(x < 0)
            x = width

        if(y > height)
            y = 0f
        if(y < 0)
            y = height
    }

    override fun render(canvas: Canvas) {
        val paint = Paint()
        paint.color = Color.RED
        canvas.drawCircle(x, y, radius, paint)
    }
}