package app.ryanm.rollingaway

import android.graphics.*

enum class PlayerState {
    ALIVE, DYING, GAMEOVER
}
class Player(var radius: Float) {
    var x = 500f
    var y = 500f
    var speed = 350f

    var score: Int = 0
    var pellets: Int = 0
    var lives: Int = 3
    var state: PlayerState = PlayerState.ALIVE

    var direction: Direction = Direction.UP

    var rect = RectF()

    fun render(canvas: Canvas) {
        val paint = Paint()
        paint.color = Color.RED
        canvas.drawCircle(x+radius/2, y+radius/2, radius, paint)
    }
}