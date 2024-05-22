package com.royputra.submissionstory.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.royputra.submissionstory.R
import com.royputra.submissionstory.databinding.ActivityLoginBinding
import com.royputra.submissionstory.view.main.MainActivity
import com.royputra.submissionstory.view.model.LoginViewModel
import com.royputra.submissionstory.view.model.ViewModelFactory

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBar = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBar.left, systemBar.top, systemBar.right, systemBar.bottom)
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

    private fun playAnimation(){
        ObjectAnimator.ofFloat(binding.imgView, View.TRANSLATION_X,-30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(250)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(250)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(250)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(250)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(250)
        val login = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(250)

        AnimatorSet().apply {
            playSequentially(
                title,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                login
            )
            startDelay = 250
        }.start()
    }

    private fun setupAction() {
        binding.btnLogin.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            when {
                email.isEmpty() -> {
                    binding.emailEditText.error = getString(R.string.emailValid)
                }

                password.isEmpty() -> {
                    binding.passwordEditText.error = getString(R.string.passwordValid)
                }
                else -> {
                    loginViewModel.login(email, password)
                    showLoading(true)
                }
            }
        }
        observeLoginResponse()
        observeLoadingState()
        observeErrorState()
    }

    private fun observeErrorState() {
        loginViewModel.isError.observe(this) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                showError(errorMessage)
                showLoading(false)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading){
            binding.progressBar.visibility = View.VISIBLE
        }else{
            binding.progressBar.visibility = View.INVISIBLE
        }
    }

    private fun showError(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    private fun observeLoadingState() {
        loginViewModel.isLoading.observe(this){ isLoading ->
            showLoading(isLoading)
        }
    }

    private fun observeLoginResponse() {
        loginViewModel.loginResponse.observe(this){ response ->
            if (response.error){
                Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
            } else {
                AlertDialog.Builder(this).apply {
                    setTitle("Yosh!")
                    setMessage(getString(R.string.loginSuccess))
                    setPositiveButton(getString(R.string.next)) { _, _ ->
                        val intent = Intent(context, MainActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                        showLoading(false)
                    }
                    create()
                    show()
                }
            }
        }
    }
}