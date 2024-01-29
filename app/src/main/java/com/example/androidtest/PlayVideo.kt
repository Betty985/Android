package com.example.androidtest

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class PlayVideo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_video)
        val uri = Uri.parse("android.resource://$packageName/${R.raw.movie}")
        val videoView = findViewById<VideoView>(R.id.videoView)
        val play = findViewById<Button>(R.id.play)
        val pause = findViewById<Button>(R.id.pause)
        val replay = findViewById<Button>(R.id.replay)
        videoView.setVideoURI(uri)
        play.setOnClickListener {
            if (!videoView.isPlaying) {
                videoView.start()//开始播放
            }
        }
        pause.setOnClickListener {
            if (videoView.isPlaying) {
                videoView.pause()//暂停播放
            }
        }
        replay.setOnClickListener {
            if (videoView.isPlaying) {
                videoView.resume()//重新播放
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val videoView = findViewById<VideoView>(R.id.videoView)
//        将VideoView所占用的资源释放掉
        videoView.suspend()
    }
}