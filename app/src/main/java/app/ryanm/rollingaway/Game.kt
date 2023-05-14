package app.ryanm.rollingaway

import android.content.Context
import android.content.Context.SENSOR_SERVICE
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

    private val attribs = GameAttributes(0f, 0f, 0f, 0f, 0f, 0f)

    private lateinit var thread: Thread
    private lateinit var scene: Scene

    fun init(startScene: Scene) {
        running = true
        scene = startScene

        keepScreenOn = true
    }

    override fun run() {
        attribs.screenWidth = context.resources.displayMetrics.widthPixels.toFloat()
        attribs.screenHeight = context.resources.displayMetrics.heightPixels.toFloat()
        attribs.screenDensity = context.resources.displayMetrics.density

        val sensorManager = context.getSystemService(SENSOR_SERVICE) as SensorManager
        val rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        sensorManager.registerListener(this, rotationSensor, 10000)

        var lastTick: Long = System.currentTimeMillis()
        var deltaT: Float

        while(running) {
            if(!paused) {
                deltaT = (System.currentTimeMillis() - lastTick) / 1000f
                lastTick = System.currentTimeMillis()
                scene.update(deltaT, attribs)

                val canvas = holder.lockCanvas()
                if(canvas != null) {
                    canvas.drawColor(Color.BLACK)
                    scene.render(deltaT, canvas)

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
        if(event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            attribs.xRot = event.values[0]
            attribs.yRot = event.values[1]
            attribs.zRot = event.values[2]
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }
}