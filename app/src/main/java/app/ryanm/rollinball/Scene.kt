package app.ryanm.rollinball

import android.graphics.Canvas

interface Scene {
    fun update(gameState: GameState)
    fun render(canvas: Canvas)
}