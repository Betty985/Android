package com.example.androidtest

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class playAudio : AppCompatActivity() {
    private val mediaPlayer = MediaPlayer()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_audio)
        initMediaPlayer()
        val play=findViewById<Button>(R.id.play)
        val pause=findViewById<Button>(R.id.pause)
        val stop=findViewById<Button>(R.id.stop)
        play.setOnClickListener {
            if (!mediaPlayer.isPlaying) {
//                开始播放
                mediaPlayer.start()
            }
        }
        pause.setOnClickListener {
            if (mediaPlayer.isPlaying) {
//                暂停播放
                mediaPlayer.pause()
            }
        }
        stop.setOnClickListener{
            if(mediaPlayer.isPlaying){
//                停止播放
                mediaPlayer.reset()
                initMediaPlayer()
            }
        }
    }

    private fun initMediaPlayer() {
        val assetManager = assets
        val fd = assetManager.openFd("the-last-piano-112677.mp3")
        mediaPlayer.setDataSource(fd.fileDescriptor,fd.startOffset,fd.length)
        mediaPlayer.prepare()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        mediaPlayer.release()
    }
}