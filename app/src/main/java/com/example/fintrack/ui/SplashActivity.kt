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

        // Obtém o layout da tela de splash
        val splashScrenLayout = findViewById<LinearLayout>(R.id.ll_splashscreen)

        // Inicia a animação de fade in no layout da tela de splash
        splashScrenLayout.startAnimation(fadeInAnimation)

        val splashScreenDuration = 2000L // 2 segundos

        // Define uma tarefa a ser executada após um atraso
        splashScrenLayout.postDelayed({
            // Inicia a MainActivity após o tempo de duração da tela de splash
            startActivity(Intent(this, MainActivity::class.java))

            // Finaliza a activity para evitar que o usuário possa voltar a ela
            finish()

        }, splashScreenDuration)
    }
}