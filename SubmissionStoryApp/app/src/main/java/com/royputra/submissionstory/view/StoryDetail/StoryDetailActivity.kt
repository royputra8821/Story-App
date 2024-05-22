package com.royputra.submissionstory.view.StoryDetail

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.setPadding
import com.bumptech.glide.Glide
import com.royputra.submissionstory.R
import com.royputra.submissionstory.databinding.ActivityStoryDetailBinding

class StoryDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoryDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityStoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.detailStory)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val photoUrl = intent.getStringExtra(EXTRA_PHOTO_URL)
        val createdAt = intent.getStringExtra(EXTRA_CREATED_AT)
        val name = intent.getStringExtra(EXTRA_NAME)
        val description = intent.getStringExtra(EXTRA_DESCRIPTION)
        val lon = intent.getDoubleExtra(EXTRA_LON, 0.0)
        val id = intent.getStringExtra(EXTRA_ID)
        val lat = intent.getDoubleExtra(EXTRA_LAT, 0.0)
        detailView(photoUrl, createdAt, name, description, lon, id, lat)


    }

    private fun detailView(photoUrl: String?, createdAt: String?, name: String?, description: String?, lon: Double, id: String?, lat: Double) {
        Glide.with(this@StoryDetailActivity)
            .load(photoUrl)
            .into(binding.imgStory)
        binding.tvName.text = name
        binding.tvDate.text = createdAt
        binding.tvDesc.text = description
    }

    companion object {
        const val EXTRA_PHOTO_URL = "EXTRA_PHOTO_URL"
        const val EXTRA_CREATED_AT = "EXTRA_CREATED_AT"
        const val EXTRA_NAME = "EXTRA_NAME"
        const val EXTRA_DESCRIPTION = "EXTRA_DESCRIPTION"
        const val EXTRA_LON = "EXTRA_LON"
        const val EXTRA_ID = "EXTRA_ID"
        const val EXTRA_LAT = "EXTRA_LAT"
    }
}