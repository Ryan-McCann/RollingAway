package app.ryanm.rollinball.character

import android.graphics.Canvas
import app.ryanm.rollinball.GameState

interface Character {
    fun update(gameState: GameState)
    fun render(canvas: Canvas)
}