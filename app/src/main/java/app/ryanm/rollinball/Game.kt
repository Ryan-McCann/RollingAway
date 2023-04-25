package app.ryanm.rollinball

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.content.res.Configuration
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import android.view.SurfaceView

class Game(context: Context): SurfaceView(context), Runnable, SensorEventListener {
    private var running = false
    private var paused = false

    private val state = GameState(0f, 0f, 0f, 0f, 0f)

    private lateinit var thread: Thread
    private lateinit var scene: Scene

    fun init(startScene: Scene) {
        running = true
        scene = startScene

        keepScreenOn = true
    }

    override fun run() {
        state.screenWidth = context.resources.displayMetrics.widthPixels.toFloat()
        state.screenHeight = context.resources.displayMetrics.heightPixels.toFloat()

        val sensorManager = context.getSystemService(SENSOR_SERVICE) as SensorManager
        val rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

        sensorManager.registerListener(this, rotationSensor, 100000)

        while(running) {
            if(!paused) {
                scene.update(state)

                val canvas = holder.lockCanvas()
                if(canvas != null) {
                    canvas.drawColor(Color.BLACK)
                    scene.render(canvas)

                    holder.unlockCanvasAndPost(canvas)
                }
            }
        }
    }

    fun pause() {
        running = false
        try {
            thread.join()
        } catch (e: InterruptedException) {
            Log.e("Error:", "joining thread")
        }
    }

    fun resume() {
        thread = Thread(this)

        thread.start()
        running = true
    }

    override fun onSensorChanged(event: SensorEvent) {
        if(event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
            val orientation = context.resources.configuration.orientation

            SensorManager.getRotationMatrixFromVector( state.rotationMatrix, event.values)

            if(orientation == Configuration.ORIENTATION_LANDSCAPE) {
                state.xRot = event.values[0]
                state.yRot = event.values[1]
            } else {
                state.xRot = event.values[1]
                state.yRot = -event.values[0]
            }

            Log.i("Orientation:", orientation.toString())

            state.zRot = event.values[2]
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }
}