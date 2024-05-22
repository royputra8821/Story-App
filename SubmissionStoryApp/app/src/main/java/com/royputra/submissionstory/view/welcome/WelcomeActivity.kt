package com.royputra.submissionstory.view.welcome

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.setPadding
import com.royputra.submissionstory.R
import com.royputra.submissionstory.databinding.ActivityWelcomeBinding
import com.royputra.submissionstory.view.login.LoginActivity
import com.royputra.submissionstory.view.signup.SignupActivity

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { s, insets ->
            val systemBar = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            s.setPadding(systemBar.left, systemBar.top, systemBar.right, systemBar.bottom)
            insets
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))
        }
        binding.signupButton.setOnClickListener{
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode =ObjectAnimator.REVERSE
        }.start()

        val login =ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(250)
        val signup =ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(250)
        val title =ObjectAnimator.ofFloat(binding.tvTitle, View.ALPHA, 1f).setDuration(250)
        val desc =ObjectAnimator.ofFloat(binding.tvDescWelcome, View.ALPHA, 1f).setDuration(250)

        val together = AnimatorSet().apply {
            playTogether(login, signup)
        }
        AnimatorSet().apply {
            playSequentially(title, desc, together)
            start()
        }
    }
}