package com.royputra.submissionstory.view.signup

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.royputra.submissionstory.R
import com.royputra.submissionstory.data.ResultState
import com.royputra.submissionstory.databinding.ActivitySignupBinding
import com.royputra.submissionstory.view.login.LoginActivity
import com.royputra.submissionstory.view.model.SignupViewModel
import com.royputra.submissionstory.view.model.ViewModelFactory

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private val viewModel by viewModels<SignupViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSignup.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        setupView()
        setupAction()
    }

    private fun setupAction() {
        binding.btnSignup.setOnClickListener{
            val name = binding.nameEditText.text?.toString() ?: ""
            val email = binding.emailEditText.text?.toString() ?: ""
            val password = binding.passwordEditText.text?.toString() ?: ""

            viewModel.signup(name, email, password)
            viewModel.registrationResult.observe(this){ result ->
                showLoading(result is ResultState.Loading)

                when (result){
                    is ResultState.Success -> {
                        showLoading(false)
                        result.data.let { data ->
                            data.message?.let { message -> AlertDialog.Builder(this).apply {
                                setTitle("Yosh!")
                                setMessage("Akun dengan $email sudah jadi nih. Yuk, login dan share ceritamu.")
                                setPositiveButton("Next"){_,_ ->
                                    finish()
                                    startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
                                }
                                create()
                                show()
                            } }
                        }
                    }
                    is ResultState.Error -> {
                        showLoading(false)
                        AlertDialog.Builder(this).apply {
                            setTitle("Sorry..")
                            setMessage(result.error)
                            setPositiveButton("Try Again"){_,_ ->
                            }
                            create()
                            show()
                        }
                    }
                    is ResultState.Loading -> showLoading(true)
                }
            }
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun showLoading(isLoading: Boolean){
        if (isLoading){
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}