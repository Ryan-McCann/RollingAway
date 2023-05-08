package app.ryanm.rollingaway

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat

enum class EnemyState {
    PURSUIT, SCATTER, FRIGHTENED
}

class Enemy(var radius: Float, resources: Resources, @DrawableRes id: Int) {
    var x = 0.0f
    var y = 0.0f

    var tileX: Int = 0
    var tileY: Int = 0

    var nextTileX: Int = 1
    var nextTileY: Int = 0

    var movingToTile = false

    var speed = 350f
    var direction = Direction.RIGHT

    var state = EnemyState.SCATTER

    private var enemyImg: Bitmap

    var rect = RectF()

    private val paint = Paint()

    init {
        val drawable = ResourcesCompat.getDrawable(resources, id, null)
        enemyImg = Bitmap.createBitmap(2*radius.toInt(), 2*radius.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(enemyImg)
        drawable?.setBounds(0, 0, radius.toInt()*2, radius.toInt()*2)
        drawable?.draw(canvas)
    }

    fun render(canvas: Canvas) {
        canvas.drawBitmap(enemyImg, x-radius/2, y-radius/2, paint)
    }
}