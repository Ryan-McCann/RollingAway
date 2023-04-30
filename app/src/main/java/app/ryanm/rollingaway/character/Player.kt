package app.ryanm.rollingaway.character

import android.graphics.*

class Player(var radius: Float) : Character {
    var x = 500f
    var y = 500f
    var speed = 100f

    var rect: RectF = RectF()

    override fun render(canvas: Canvas) {
        val paint = Paint()
        paint.color = Color.RED
        canvas.drawCircle(x+radius/2, y+radius/2, radius, paint)
    }
}