package com.royputra.submissionstory.view.StoryAdd

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.royputra.submissionstory.R
import com.royputra.submissionstory.data.pref.UserPreference
import com.royputra.submissionstory.data.pref.dataStore
import com.royputra.submissionstory.data.retrofit.api.ApiConfig
import com.royputra.submissionstory.data.retrofit.response.FileUploadResponse
import com.royputra.submissionstory.databinding.ActivityStoryAddBinding
import com.royputra.submissionstory.view.getImageUri
import com.royputra.submissionstory.view.main.MainActivity
import com.royputra.submissionstory.view.reduceFileImage
import com.royputra.submissionstory.view.uriToFile
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException

class StoryAddActivity : AppCompatActivity() {
    private lateinit var userPref: UserPreference
    private lateinit var binding: ActivityStoryAddBinding
    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityStoryAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPref = UserPreference.getInstance(dataStore)
        binding.btnCamera.setOnClickListener{ startCamera() }
        binding.btnGallery.setOnClickListener{ startGallery() }
        binding.btnUpload.setOnClickListener{ uploadImage() }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBar = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBar.left, systemBar.top, systemBar.right, systemBar.bottom)
            insets
        }
    }

    private fun startCamera(){
        currentImageUri = getImageUri(this)
        launchIntentCamera.launch(currentImageUri!!)
    }
    private val launchIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ){isSuccess ->
        if (isSuccess){
            showImage()
        }
    }

    private fun startGallery(){
        launchGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
    private val launchGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ){uri: Uri? ->
        if (uri != null){
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No Media Selected")
        }
    }

    private fun uploadImage(){
        val description = binding.descText.text.toString()
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            Log.d("Image File", "showImage: ${imageFile.path}")
            showLoading(true)

            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )
            lifecycleScope.launch {
                userPref.getSession().collect{user ->
                    val token = user.token
                    if (token.isNotEmpty()){
                        try {
                            val apiService = ApiConfig.getApiService(token)
                            val successResponse = apiService.uploadStory("Bearer $token", multipartBody, requestBody)
                            showToast(successResponse.message)

                            AlertDialog.Builder(this@StoryAddActivity).apply {
                                setTitle(getString(R.string.uploadSuccess))
                                setMessage(getString(R.string.storyUploadSuccess))
                                setPositiveButton(getString(R.string.next)) {_,_ ->
                                    val intent = Intent(this@StoryAddActivity, MainActivity::class.java).apply {
                                        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                    }
                                    startActivity(intent)
                                    finish()
                                }
                                create()
                                show()
                            }
                        } catch (e: HttpException){
                            val errorBody = e.response()?.errorBody()?.string()
                            val errorResponse = Gson().fromJson(errorBody, FileUploadResponse::class.java)
                            showToast(errorResponse.message)
                        } finally {
                            showLoading(false)
                        }
                    } else {
                        showToast(getString(R.string.tokenInfo))
                        showLoading(false)
                    }
                }
            }
        }?: showToast(getString(R.string.empty_image))
    }

    private fun showLoading(isLoading: Boolean){
        if (isLoading){
            binding.progressBar.visibility = View.VISIBLE
        }else{
            binding.progressBar.visibility = View.INVISIBLE
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.pvImgView.setImageURI(it)
        }
    }
}