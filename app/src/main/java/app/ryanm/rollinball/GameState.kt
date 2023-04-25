package app.ryanm.rollinball

data class GameState(
    // Size of screen
    var screenWidth: Float,
    var screenHeight: Float,

    // Used for input
    var xRot: Float,
    var yRot: Float,
    var zRot: Float,

    var rotationMatrix: FloatArray = floatArrayOf(1f, 0f, 0f, 0f,
                                             1f, 0f, 0f, 0f,
                                             1f, 0f, 0f, 0f,
                                             1f, 0f, 0f, 0f)
)
