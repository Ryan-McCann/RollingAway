package app.ryanm.rollingaway

import android.app.Activity
import android.os.Bundle

class MainActivity : Activity() {
    private lateinit var game: Game

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        game = Game(this)
        setContentView(game)

        val scene = World(resources)

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