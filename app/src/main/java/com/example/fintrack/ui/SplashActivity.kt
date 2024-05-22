package com.example.fintrack.ui

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.fintrack.R

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        // Carrega a animação de fade in
        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in_animation)

        val splashScrenLayout = findViewById<LinearLayout>(R.id.ll_splashscreen)

        splashScrenLayout.startAnimation(fadeInAnimation)

        val splashScreenDuration = 2000L // 2 segundos
        splashScrenLayout.postDelayed({

            startActivity(Intent(this, MainActivity::class.java))

            finish()
        }, splashScreenDuration)
    }
}