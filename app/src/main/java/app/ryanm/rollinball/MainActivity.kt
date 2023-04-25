package app.ryanm.rollinball

import android.app.Activity
import android.os.Bundle
import app.ryanm.rollinball.level.Level1

class MainActivity : Activity() {
    private lateinit var game: Game

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        game = Game(this)
        setContentView(game)

        val scene = Level1()

        game.init(scene)
    }

    override fun onResume() {
        super.onResume()
        game.resume()
    }

    override fun onPause() {
        super.onPause()
        game.pause()
    }
}