package com.example.prueba.presentation

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.ToneGenerator
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.prueba.R

class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var vibrator: Vibrator
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var sensorManager: SensorManager
    private var sensor: Sensor? = null
    private var sensorType = Sensor.TYPE_ACCELEROMETER

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Configuración de Vibrador (Diapositiva 18)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        // Configuración de MediaPlayer (Diapositiva 19)
        try {
            mediaPlayer = MediaPlayer.create(this, R.raw.archivo)
        } catch (e: Exception) {
            Log.e("MainActivity", "Error al cargar el audio", e)
        }

        // Configuración de Sensores (Diapositiva 22)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager.getDefaultSensor(sensorType)

        startSensor()

        // Configuración de Botón e Intent (Diapositivas 6, 11, 18, 19)
        val boton: Button = findViewById(R.id.boton)
        val context = this

        boton.setOnClickListener {
            // Vibrar si el dispositivo lo soporta
            if (vibrator.hasVibrator()) {
                vibrator.vibrate(500)
            }

            // Emitir tono de sistema
            try {
                val toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
                toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 250)
            } catch (e: Exception) {
                Log.e("MainActivity", "Error al emitir tono de sistema", e)
            }

            // Reproducir archivo de audio
            mediaPlayer?.let { mp ->
                try {
                    if (mp.isPlaying) {
                        mp.seekTo(0)
                    }
                    mp.start()
                } catch (e: Exception) {
                    Log.e("MainActivity", "Error al reproducir MediaPlayer", e)
                }
            }

            // Mostrar mensaje Toast
            Toast.makeText(context, "Alerta", Toast.LENGTH_SHORT).show()

            // Navegar a la segunda ventana
            val intent = Intent(this@MainActivity, Clase2::class.java)
            startActivity(intent)
        }
    }

    private fun startSensor() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BODY_SENSORS), 1001)
            return
        }
        if (sensor != null) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == sensorType) {
            val lectura = event.values[0]
            Log.d("onSensorChanged", "Lectura: $lectura")
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No se requiere implementación específica
    }

    override fun onPause() {
        super.onPause()
        if (::sensorManager.isInitialized) {
            sensorManager.unregisterListener(this)
        }
    }

    override fun onResume() {
        super.onResume()
        sensor?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
