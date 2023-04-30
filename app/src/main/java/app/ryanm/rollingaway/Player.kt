package app.ryanm.rollingaway

import android.graphics.*

enum class Direction {
    UP, DOWN, LEFT, RIGHT
}
class Player(var radius: Float) {
    var x = 500f
    var y = 500f
    var speed = 100f

    var score: Int = 0
    var pellets: Int = 0

    var direction: Direction = Direction.UP

    fun render(canvas: Canvas) {
        val paint = Paint()
        paint.color = Color.RED
        canvas.drawCircle(x+radius/2, y+radius/2, radius, paint)
    }
}