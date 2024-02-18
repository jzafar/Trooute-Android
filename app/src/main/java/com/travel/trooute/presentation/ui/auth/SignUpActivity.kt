package com.travel.trooute.presentation.ui.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.travel.trooute.R
import com.travel.trooute.core.util.Constants
import com.travel.trooute.core.util.Constants.EMAIL
import com.travel.trooute.core.util.Resource
import com.travel.trooute.core.util.UploadMultipart.imgRequestBody
import com.travel.trooute.databinding.ActivitySignUpBinding
import com.travel.trooute.presentation.utils.ImagePicker
import com.travel.trooute.presentation.utils.Loader
import com.travel.trooute.presentation.utils.WindowsManager.statusBarColor
import com.travel.trooute.presentation.utils.isConfirmPasswordValid
import com.travel.trooute.presentation.utils.isEmailValid
import com.travel.trooute.presentation.utils.isFieldValid
import com.travel.trooute.presentation.utils.isGenderSelected
import com.travel.trooute.presentation.utils.isPasswordValid
import com.travel.trooute.presentation.utils.isPhoneNumberValid
import com.travel.trooute.presentation.utils.isTermsCheckBoxClicked
import com.travel.trooute.presentation.utils.showErrorMessage
import com.travel.trooute.presentation.utils.showSuccessMessage
import com.travel.trooute.presentation.utils.trimAndRemoveDashes
import com.travel.trooute.presentation.viewmodel.authviewmodel.SignUpViewModel
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
class SignUpActivity : AppCompatActivity(), PickiTCallbacks {

    private val TAG = "SignUpActivity"

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var imagePicker: ImagePicker

    private var profileImageFile: File? = null
    private var pickiT: PickiT? = null
    private var isImageAdded = false
    private var gender: String = ""
    private val signUpViewModel: SignUpViewModel by viewModels()

    @Inject
    lateinit var loader: Loader

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
        statusBarColor(R.color.white)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        imagePicker = ImagePicker(this, imagePickerLauncher)
        pickiT = PickiT(this, this, this)

        binding.apply {
            imgUserProfile.setOnClickListener {
                imagePicker.openDialog()
            }

            termsAndCondition.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(Constants.TERMS_CONDITIONS))
                startActivity(browserIntent)
            }


            btnSignup.setOnClickListener {

                if (
                    isFieldValid(teFullName, "Full name")
                    && isEmailValid(teEmailAddress)
                    && isGenderSelected(gender, "Please select gender")
                    && isPhoneNumberValid(tePhoneNumber)
                    && isPasswordValid(true, tePassword)
                    && isConfirmPasswordValid(tePassword, teRetypePassword)
                    && isTermsCheckBoxClicked(checkBox)
                ) {
                    if (isImageAdded) {
                        signUpViewModel.signUp(
                            MultipartBody.Builder().setType(MultipartBody.FORM)
                                .addFormDataPart("name", teFullName.text.toString())
                                .addFormDataPart("email", teEmailAddress.text.toString())
                                .addFormDataPart("password", tePassword.toString())
                                .addFormDataPart("phoneNumber", trimAndRemoveDashes(tePhoneNumber))
                                .addFormDataPart("gender", gender)
                                .addFormDataPart(
                                    "photo",
                                    profileImageFile?.name,
                                    imgRequestBody(profileImageFile)
                                )
                                .build()
                        )
                    }else {
                        signUpViewModel.signUp(
                            MultipartBody.Builder().setType(MultipartBody.FORM)
                                .addFormDataPart("name", teFullName.text.toString())
                                .addFormDataPart("email", teEmailAddress.text.toString())
                                .addFormDataPart("password", tePassword.toString())
                                .addFormDataPart("phoneNumber", trimAndRemoveDashes(tePhoneNumber))
                                .addFormDataPart("gender", gender)
                                .build()
                        )
                    }

                    bindSignUpObserver()
                }
            }

            tvSingin.setOnClickListener {
                startActivity(Intent(this@SignUpActivity, SignInActivity::class.java))
                finish()
            }
        }
    }

    fun onClickRadioButton(view: View){
        if (view is RadioButton) {
            when (view.id) {
                R.id.male ->
                    if (view.isChecked) {
                        gender = "male"
                    }
                R.id.female ->
                    if (view.isChecked) {
                        gender = "female"
                    }
            }
        }
    }
    private fun bindSignUpObserver() {
        lifecycleScope.launch {
            signUpViewModel.signUpState.collect {
                loader.cancel()
                when (it) {
                    is Resource.ERROR -> {
                        Toast(this@SignUpActivity).showErrorMessage(
                            this@SignUpActivity,
                            it.message.toString()
                        )
                        Log.e(TAG, "bindAuthObserver: Error: " + it.message.toString())
                    }

                    Resource.LOADING -> {
                        loader.show()
                    }

                    is Resource.SUCCESS -> {
                        Toast(this@SignUpActivity).showSuccessMessage(
                            this@SignUpActivity,
                            it.data.message.toString()
                        )
                        startActivity(
                            Intent(
                                this@SignUpActivity,
                                AuthVerificationActivity::class.java
                            ).putExtra(EMAIL, binding.teEmailAddress.text.toString())
                        )
                        clearAllForm()
                        Log.e(TAG, "bindAuthObserver: success : " + it.data)
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

    fun clearAllForm() {
        binding.teFullName.getEditableText().clear()
        binding.teEmailAddress.getEditableText().clear()
        binding.tePhoneNumber.getEditableText().clear()
        binding.tePassword.getEditableText().clear()
        binding.teRetypePassword.getEditableText().clear()
        binding.imgUserProfile.setImageURI(null)
        binding.imgUserProfile.setImageResource(R.drawable.ic_camera)
        val contentPadding = (resources.getDimension(R.dimen.content_padding)).toInt()
        binding.imgUserProfile.setContentPadding(contentPadding, contentPadding, contentPadding, contentPadding)
        isImageAdded = false
        profileImageFile = null
    }
}