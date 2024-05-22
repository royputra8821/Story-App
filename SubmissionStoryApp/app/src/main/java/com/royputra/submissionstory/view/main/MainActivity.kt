package com.royputra.submissionstory.view.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.royputra.submissionstory.R
import com.royputra.submissionstory.data.adapter.StoryAdapter
import com.royputra.submissionstory.data.retrofit.response.ListStoryItem
import com.royputra.submissionstory.databinding.ActivityMainBinding
import com.royputra.submissionstory.view.StoryAdd.StoryAddActivity
import com.royputra.submissionstory.view.StoryDetail.StoryDetailActivity
import com.royputra.submissionstory.view.login.LoginActivity
import com.royputra.submissionstory.view.model.MainViewModel
import com.royputra.submissionstory.view.model.ViewModelFactory
import com.royputra.submissionstory.view.welcome.WelcomeActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding
    private lateinit var storyAdapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        observeSession()
        observeLogout()
        CoroutineScope(Dispatchers.Main).launch {
            try {
                getAllStorie()
            } catch (e: Exception){ Log.e("MainActivity", "Error: ${e.message}")}
        }
        observeListStory()
        binding.fabAdd.setOnClickListener{
            startActivity(Intent(this@MainActivity, StoryAddActivity::class.java))
        }

    }

    private fun observeSession(){
        viewModel.getSession().observe(this){user ->
            if (!user.isLogin){
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }
    }

    private fun observeLogout(){
        binding.barApp.setOnMenuItemClickListener{menuItem ->
            when (menuItem.itemId){
                R.id.itemMenu -> {
                    binding.barApp.setOnMenuItemClickListener(null)

                    AlertDialog.Builder(this).apply {
                        setTitle(getString(R.string.confirmExit))
                        setMessage(getString(R.string.exit))
                        setPositiveButton(getString(R.string.yes)) {dialog, _ ->
                            dialog.dismiss()
                            viewModel.logout()
                            binding.barApp.setOnMenuItemClickListener{innerMenu ->
                                observeLogout()
                                true
                            }
                            val intent = Intent(this@MainActivity, LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        }
                        setNegativeButton(getString(R.string.no)) {dialog, _ ->
                            dialog.dismiss()
                            binding.barApp.setOnMenuItemClickListener{innerMenu ->
                                observeLogout()
                                true
                            }
                        }
                        create()
                        show()
                    }
                    true
                }
                R.id.languageMenu -> {
                    startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                    true
                }
                else -> false
            }
        }
    }

    private suspend fun getAllStorie(){ viewModel.getAllStory() }

    private fun observeListStory(){
        viewModel.storyList.observe(this){ storyList ->
            if (storyList.isNotEmpty()){
                this.storyAdapter = StoryAdapter(storyList, object : StoryAdapter.OnAdapterListener {
                    override fun onClick(story: ListStoryItem){
                        navigateToDetailStory(story)
                    }
                })
                binding.rvStory.adapter = storyAdapter
                binding.rvStory.layoutManager = LinearLayoutManager(this)
            } else {
                Toast.makeText(this, getString(R.string.storyEmpty), Toast.LENGTH_SHORT).show()
                this.storyAdapter = StoryAdapter(mutableListOf(), object : StoryAdapter.OnAdapterListener {
                    override fun onClick(story: ListStoryItem) {
                        (TODO("Not yet implemented"))
                    }
                })
                binding.rvStory.adapter = storyAdapter
                binding.rvStory.layoutManager = LinearLayoutManager(this)
            }
        }
    }

    private fun setupView(){
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

    private fun navigateToDetailStory(story: ListStoryItem){
        val intent = Intent(this@MainActivity, StoryDetailActivity::class.java)
        intent.putExtra(StoryDetailActivity.EXTRA_PHOTO_URL, story.photoUrl)
        intent.putExtra(StoryDetailActivity.EXTRA_CREATED_AT, story.createdAt)
        intent.putExtra(StoryDetailActivity.EXTRA_NAME, story.name)
        intent.putExtra(StoryDetailActivity.EXTRA_DESCRIPTION, story.description)
        intent.putExtra(StoryDetailActivity.EXTRA_LON, story.lon)
        intent.putExtra(StoryDetailActivity.EXTRA_ID, story.id)
        intent.putExtra(StoryDetailActivity.EXTRA_LAT, story.lat)
        startActivity(intent)
    }
}