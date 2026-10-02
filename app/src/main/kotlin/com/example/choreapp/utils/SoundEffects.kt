package com.example.choreapp.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

enum class Sound { POP, PUT_BACK, DONE, APPROVE, REJECT, ADD }

// Cute chimes synthesized in code, so the app needs no audio files.
object SoundEffects {
    private const val SAMPLE_RATE = 22050
    private const val PREFS = "sound_prefs"
    private const val KEY_ENABLED = "enabled"

    private val tracks = mutableMapOf<Sound, AudioTrack>()

    @Volatile
    var enabled: Boolean = true
        private set

    fun init(context: Context) {
        enabled = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_ENABLED, true)
    }

    fun setEnabled(context: Context, value: Boolean) {
        enabled = value
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean(KEY_ENABLED, value).apply()
        if (value) play(Sound.POP)
    }

    @Synchronized
    fun play(sound: Sound) {
        if (!enabled) return
        runCatching {
            val track = tracks.getOrPut(sound) { buildTrack(sound) }
            track.stop()
            track.reloadStaticData()
            track.play()
        }
    }

    private fun buildTrack(sound: Sound): AudioTrack {
        val data = render(sound)
        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setSampleRate(SAMPLE_RATE)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(data.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()
        track.write(data, 0, data.size)
        return track
    }

    private class Note(val start: Double, val duration: Double, val from: Double, val to: Double = from, val volume: Double = 0.5)

    private fun render(sound: Sound): ShortArray {
        val notes = when (sound) {
            Sound.POP -> listOf(Note(0.0, 0.12, 420.0, 900.0))
            Sound.PUT_BACK -> listOf(Note(0.0, 0.16, 700.0, 330.0))
            Sound.ADD -> listOf(Note(0.0, 0.08, 700.0, 1000.0), Note(0.08, 0.14, 1000.0, 1400.0))
            Sound.DONE -> listOf(
                Note(0.00, 0.22, 523.25), Note(0.09, 0.22, 659.25), Note(0.18, 0.22, 783.99),
                Note(0.27, 0.45, 1046.5), Note(0.36, 0.5, 1318.5, volume = 0.3), Note(0.46, 0.5, 1568.0, volume = 0.25)
            )
            Sound.APPROVE -> listOf(Note(0.0, 0.1, 987.8), Note(0.09, 0.6, 1318.5), Note(0.09, 0.6, 1975.5, volume = 0.2))
            Sound.REJECT -> listOf(Note(0.0, 0.18, 392.0, 370.0), Note(0.16, 0.26, 330.0, 300.0))
        }
        val total = notes.maxOf { it.start + it.duration + 0.05 }
        val mix = DoubleArray((total * SAMPLE_RATE).toInt())
        for (note in notes) {
            val begin = (note.start * SAMPLE_RATE).toInt()
            val length = (note.duration * SAMPLE_RATE).toInt()
            var phase = 0.0
            for (i in 0 until length) {
                if (begin + i >= mix.size) break
                val t = i.toDouble() / SAMPLE_RATE
                val progress = i.toDouble() / length
                val freq = note.from + (note.to - note.from) * progress
                phase += 2 * PI * freq / SAMPLE_RATE
                val envelope = minOf(1.0, t / 0.006) * exp(-t * (4.0 / note.duration))
                val tone = sin(phase) + 0.3 * sin(2 * phase) + 0.1 * sin(3 * phase)
                mix[begin + i] += tone * envelope * note.volume
            }
        }
        return ShortArray(mix.size) { (mix[it].coerceIn(-1.0, 1.0) * Short.MAX_VALUE * 0.8).toInt().toShort() }
    }
}