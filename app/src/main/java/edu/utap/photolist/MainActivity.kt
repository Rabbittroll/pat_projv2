package edu.utap.photolist
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.commit
import edu.utap.photolist.databinding.ActivityMainBinding
import edu.utap.photolist.view.HomeFragment
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
    companion object {
        private val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        fun localPhotoFile(pictureName : String): File {
            // Create the File where the photo should go
            val localPhotoFile = File(storageDir, "${pictureName}.jpg")
            Log.d("MainActivity", "photo path ${localPhotoFile.absolutePath}")
            return localPhotoFile
        }
    }
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.pictureSuccess()
        } else {
            viewModel.pictureFailure()
        }
    }
    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.updateUser()
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
            Log.d("MainActivity", "sign in failed ${result}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set the layout for the layout we created
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.commit {
            replace(R.id.mainFragment, HomeFragment.newInstance())
        }

        viewModel.setPhotoIntent(::takePictureIntent)

        // Initialize firestore assets
        AuthInit(viewModel, signInLauncher)
        viewModel.fetchPhotoMeta()
    }

    //////////////////////////////////////////////////////////////////////
    // Camera stuff
    private fun photoUri(localPhotoFile: File) : Uri {
        var photoUri : Uri? = null
        // Create the File where the photo should go
        try {
            photoUri = FileProvider.getUriForFile(
                this,
                "edu.utap.photolist",
                localPhotoFile)
        } catch (ex: IOException) {
            // Error occurred while creating the File
            Log.d(javaClass.simpleName, "Cannot create file", ex)
        }
        // CRASH.  Production code should do something more graceful
        return photoUri!!
    }
    private fun takePictureIntent(name: String) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePhotoIntent ->
            val localPhotoFile = localPhotoFile(name)
            val photoUri = photoUri(localPhotoFile)
            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            cameraLauncher.launch(takePhotoIntent)
        }
        Log.d(javaClass.simpleName, "takePhotoIntent")
    }
}
