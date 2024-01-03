package com.example.trooute.presentation.ui.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.trooute.R
import com.example.trooute.core.util.Resource
import com.example.trooute.core.util.SharedPreferenceManager
import com.example.trooute.core.util.UploadMultipart
import com.example.trooute.databinding.ActivityYourProfileBinding
import com.example.trooute.presentation.utils.ImagePicker
import com.example.trooute.presentation.utils.Loader
import com.example.trooute.presentation.utils.isFieldValid
import com.example.trooute.presentation.utils.isPhoneNumberValid
import com.example.trooute.presentation.utils.loadProfileImage
import com.example.trooute.presentation.utils.showErrorMessage
import com.example.trooute.presentation.utils.showSuccessMessage
import com.example.trooute.presentation.viewmodel.authviewmodel.UpdateMyProfileVM
import com.google.android.material.internal.ViewUtils
import com.hbisoft.pickit.PickiT
import com.hbisoft.pickit.PickiTCallbacks
import com.nguyenhoanglam.imagepicker.ui.imagepicker.registerImagePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class YourProfileActivity : AppCompatActivity(), PickiTCallbacks {

    private lateinit var binding: ActivityYourProfileBinding
    private lateinit var imagePicker: ImagePicker

    private var profileImageFile: File? = null
    private var pickiT: PickiT? = null
    private var isImageAdded = false

    private val updateMyProfileVM: UpdateMyProfileVM by viewModels()

    @Inject
    lateinit var loader: Loader

    @Inject
    lateinit var sharedPreferenceManager: SharedPreferenceManager

    private var imageUri: Uri? = null
    private val imagePickerLauncher = registerImagePicker { images ->
        if (images.isNotEmpty()) {
            val sampleImage = images[0]
            imageUri = sampleImage.uri
            imageUri?.let {
                isImageAdded = true
                binding.imgUserProfile.setImageURI(imageUri)
                binding.imgUserProfile.setContentPadding(0, 0, 0, 0)
                pickiT?.getPath(imageUri, Build.VERSION.SDK_INT)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_your_profile)
        imagePicker = ImagePicker(this, imagePickerLauncher)
        pickiT = PickiT(this, this, this)

        binding.apply {
            imgUserProfile.setOnClickListener {
                imagePicker.openDialog()
            }

            sharedPreferenceManager.getAuthModelFromPref()?.let {user ->
                loadProfileImage(imgUserProfile, user.photo.toString())
                teFullName.setText(user.name.toString())
                tePhoneNumber.setText(user.phoneNumber.toString())
            }

            btnUpdate.setOnClickListener {
                if (
                    isFieldValid(teFullName, "Full name")
                    && isPhoneNumberValid(tePhoneNumber)
                ) {
                    if (isImageAdded) {
                        updateMyProfileVM.updateMyProfile(
                            MultipartBody.Builder().setType(MultipartBody.FORM)
                                .addFormDataPart("name", teFullName.text.toString())
                                .addFormDataPart("phoneNumber", tePhoneNumber.text.toString())
                                .addFormDataPart(
                                    "photo",
                                    profileImageFile?.name,
                                    UploadMultipart.imgRequestBody(profileImageFile)
                                )
                                .build()
                        )
                    }else {
                        updateMyProfileVM.updateMyProfile(
                            MultipartBody.Builder().setType(MultipartBody.FORM)
                                .addFormDataPart("name", teFullName.text.toString())
                                .addFormDataPart("phoneNumber", tePhoneNumber.text.toString())
                                .build()
                        )
                    }

                    bindMyProfileObserver()
                }
            }

            btnUpdatePassword.setOnClickListener {
                startActivity(Intent(this@YourProfileActivity, UpdatePasswordActivity::class.java))
            }
        }
    }

    private fun bindMyProfileObserver() {
        lifecycleScope.launch {
            updateMyProfileVM.updateMyProfileState.collect {
                loader.cancel()
                when (it) {
                    is Resource.ERROR -> {
                        Toast(this@YourProfileActivity).showErrorMessage(
                            this@YourProfileActivity,
                            it.message.toString()
                        )
                    }

                    Resource.LOADING -> {
                        loader.show()
                    }

                    is Resource.SUCCESS -> {
                        Toast(this@YourProfileActivity).showSuccessMessage(
                            this@YourProfileActivity,
                            it.data.message.toString()
                        )

                        sharedPreferenceManager.saveAuthModelInPref(it.data.data)
                        finish()
                    }
                }
            }
        }
    }

    override fun PickiTonUriReturned() {

    }

    override fun PickiTonStartListener() {

    }

    override fun PickiTonProgressUpdate(progress: Int) {

    }

    override fun PickiTonCompleteListener(
        path: String?,
        wasDriveFile: Boolean,
        wasUnknownProvider: Boolean,
        wasSuccessful: Boolean,
        Reason: String?
    ) {
        // Check if the conversion was successful
        if (wasSuccessful) {
            profileImageFile = File(path.toString())
        } else {
            // Handle the conversion failure
        }
    }

    override fun PickiTonMultipleCompleteListener(
        paths: ArrayList<String>?, wasSuccessful: Boolean, Reason: String?
    ) {

    }

    @SuppressLint("RestrictedApi")
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        ViewUtils.hideKeyboard(binding.ltRoot)
        return super.dispatchTouchEvent(ev)
    }
}