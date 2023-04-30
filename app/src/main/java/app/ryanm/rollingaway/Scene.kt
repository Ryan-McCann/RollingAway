package app.ryanm.rollingaway

import android.graphics.Canvas

interface Scene {
    fun update(deltaT: Float, attribs: GameAttributes)
    fun render(canvas: Canvas)
}