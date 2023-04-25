package app.ryanm.rollinball.level

import android.graphics.Canvas
import app.ryanm.rollinball.GameState
import app.ryanm.rollinball.Scene
import app.ryanm.rollinball.character.Player

class Level1: Scene {
    private val player = Player()

    override fun update(state: GameState) {
        player.update(state)
    }

    override fun render(canvas: Canvas) {
        player.render(canvas)
    }
}