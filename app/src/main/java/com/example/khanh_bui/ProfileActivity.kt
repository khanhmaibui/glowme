package com.example.khanh_bui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import java.io.File
import java.io.FileOutputStream

class ProfileActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var nameView: TextView
    private lateinit var emailView: TextView
    private lateinit var phoneView: TextView
    private lateinit var genderView: RadioGroup
    private lateinit var classView: TextView
    private lateinit var majorView: TextView
    private lateinit var sharedPreference: SharedPreferences
    private lateinit var profilePictureUri: Uri
    private lateinit var tempProfilePictureUri: Uri
    private lateinit var pickedProfilePictureUri: Uri
    private lateinit var profilePicture: File
    private lateinit var tempProfilePicture: File
    private lateinit var pickedProfilePicture: File

    private lateinit var cameraResult: ActivityResultLauncher<Intent>
    private lateinit var galleryResult: ActivityResultLauncher<Intent>
    private lateinit var profilePictureViewModel: ProfilePictureViewModel

    private val profilePictureName = "profile_picture.jpg"
    private val tempProfilePictureName = "temp_picture.jpg"
    private val pickedProfilePictureName = "picked_picture.jpg"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setSupportActionBar(findViewById(R.id.tool_bar))

        //Find views
        imageView = findViewById(R.id.profile_photo)
        nameView = findViewById(R.id.your_name)
        emailView = findViewById(R.id.your_email)
        phoneView = findViewById(R.id.your_number)
        genderView = findViewById(R.id.gender)
        classView = findViewById(R.id.class_year)
        majorView = findViewById(R.id.major)

        //Check permission for camera
        Util.checkPermissions(this)

        //Get uri for profile picture
        profilePicture = File(getExternalFilesDir(null), profilePictureName)
        profilePictureUri = FileProvider.getUriForFile(this, "com.example.khanh_bui", profilePicture)
        //Get uri for temp profile picture
        tempProfilePicture = File(getExternalFilesDir(null), tempProfilePictureName)
        tempProfilePictureUri = FileProvider.getUriForFile(this, "com.example.khanh_bui", tempProfilePicture)

        pickedProfilePicture = File(getExternalFilesDir(null), pickedProfilePictureName)
        pickedProfilePictureUri = FileProvider.getUriForFile(this, "com.example.khanh_bui", tempProfilePicture)

        //Set cameraResult to temporary profile picture
        cameraResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result: ActivityResult ->
            if(result.resultCode == Activity.RESULT_OK){
                val bitmap = Util.getBitmap(this, tempProfilePictureUri)
                profilePictureViewModel.userImage.value = bitmap
                MediaStore.Images.Media.insertImage(this.contentResolver, bitmap, tempProfilePictureName, null)
                if (pickedProfilePicture.exists()) {
                    pickedProfilePicture.delete()
                }
            }
        }

        galleryResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result: ActivityResult ->
            if(result.resultCode == Activity.RESULT_OK){
                //save Uri picked to tempProfilePicture
                pickedProfilePictureUri = result.data?.data!!
                val bitmap = Util.getBitmap(this, pickedProfilePictureUri)
                val fOut = FileOutputStream(pickedProfilePicture)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut)
                fOut.flush()
                fOut.close()
                //set imageview
                profilePictureViewModel.userImage.value = Util.getBitmap(this, pickedProfilePictureUri)
                if (tempProfilePicture.exists()) {
                    tempProfilePicture.delete()
                }
            }
        }

        //View
        profilePictureViewModel = ViewModelProvider(this)[ProfilePictureViewModel::class.java]
        profilePictureViewModel.userImage.observe(this) { it ->
            imageView.setImageBitmap(it)
        }

        //Load profile picture
        if(profilePicture.exists()) {
            imageView.setImageBitmap(Util.getBitmap(this, profilePictureUri))
        }
        //Load saved data
        loadProfile()

        if (savedInstanceState != null) {
            nameView.text = savedInstanceState.getString("NAMEVIEW_KEY")
            emailView.text = savedInstanceState.getString("EMAILVIEW_KEY")
            phoneView.text = savedInstanceState.getString("PHONEVIEW_KEY")
            genderView.check(savedInstanceState.getInt("GENDERVIEW_KEY"))
            classView.text = savedInstanceState.getString("CLASSVIEW_KEY")
            majorView.text = savedInstanceState.getString("MAJORVIEW_KEY")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val checkedGender = genderView.checkedRadioButtonId
        outState.putInt("GENDERVIEW_KEY", checkedGender)
        outState.putString("NAMEVIEW_KEY", nameView.text.toString())
        outState.putString("EMAILVIEW_KEY", emailView.text.toString())
        outState.putString("PHONEVIEW_KEY", phoneView.text.toString())
        outState.putString("CLASSVIEW_KEY", classView.text.toString())
        outState.putString("MAJORVIEW_KEY", majorView.text.toString())
    }

    fun onChangeClicked(view: View) {
        val items = arrayOf("Take from camera", "Select from gallery")
        val alertDialogBuilder = AlertDialog.Builder(this)
        var intent: Intent
        alertDialogBuilder.setTitle("Select profile image")
            .setItems(items) { _, index -> when(items[index])
                {
                    "Take from camera" -> {
                        intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempProfilePictureUri)
                        cameraResult.launch(intent)
                    }
                    "Select from gallery" -> {
                        intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                        galleryResult.launch(intent)
                    }
                }
            }
        alertDialogBuilder.show()
    }

    fun onSaveClicked(view: View) {
        if (tempProfilePicture.exists()) {
            tempProfilePicture.renameTo(profilePicture)
        }
        if (pickedProfilePicture.exists()) {
            pickedProfilePicture.renameTo(profilePicture)
        }
        saveProfile()
        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
        finish()
    }

    fun onCancelClicked(view: View)
    {
        finish()
    }

    //Inflate action bar activities
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    private fun loadProfile() {
        sharedPreference = getSharedPreferences("SAVE_PROFILE", Context.MODE_PRIVATE)
        nameView.text = sharedPreference.getString("NAMEVIEW_KEY", "")
        emailView.text = sharedPreference.getString("EMAILVIEW_KEY", "")
        phoneView.text = sharedPreference.getString("PHONEVIEW_KEY", "")
        genderView.check(sharedPreference.getInt("GENDERVIEW_KEY", -1))
        classView.text = sharedPreference.getString("CLASSVIEW_KEY", "")
        majorView.text = sharedPreference.getString("MAJORVIEW_KEY", "")
    }

    private fun saveProfile() {
        sharedPreference = getSharedPreferences("SAVE_PROFILE", Context.MODE_PRIVATE)
        val checkedGender: Int = genderView.checkedRadioButtonId
        sharedPreference.edit()
            .putString("NAMEVIEW_KEY", nameView.text.toString())
            .putString("EMAILVIEW_KEY", emailView.text.toString())
            .putString("PHONEVIEW_KEY", phoneView.text.toString())
            .putInt("GENDERVIEW_KEY", checkedGender)
            .putString("CLASSVIEW_KEY", classView.text.toString())
            .putString("MAJORVIEW_KEY", majorView.text.toString())
            .apply()
    }
}