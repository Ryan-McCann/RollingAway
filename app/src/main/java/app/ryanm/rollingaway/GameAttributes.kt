package app.ryanm.rollingaway

data class GameAttributes(
    // Size of screen
    var screenWidth: Float,
    var screenHeight: Float,
    var screenDensity: Float,

    // Used for input
    var xRot: Float,
    var yRot: Float,
    var zRot: Float
)
