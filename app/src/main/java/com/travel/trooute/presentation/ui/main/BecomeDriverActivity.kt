package com.travel.trooute.presentation.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.travel.trooute.R
import com.travel.trooute.core.util.Resource
import com.travel.trooute.core.util.SharedPreferenceManager
import com.travel.trooute.data.model.driver.request.UploadDriverDetailsRequest
import com.travel.trooute.databinding.ActivityBecomeDriverBinding
import com.travel.trooute.presentation.utils.ImagePicker
import com.travel.trooute.presentation.utils.Loader
import com.travel.trooute.presentation.utils.WindowsManager.statusBarColor
import com.travel.trooute.presentation.utils.isDropdownValid
import com.travel.trooute.presentation.utils.isFieldValid
import com.travel.trooute.presentation.utils.isImageAdded
import com.travel.trooute.presentation.utils.loadImage
import com.travel.trooute.presentation.utils.showErrorMessage
import com.travel.trooute.presentation.utils.showSuccessMessage
import com.travel.trooute.presentation.viewmodel.driverviewmodel.UpdateCarDetailsViewModel
import com.travel.trooute.presentation.viewmodel.driverviewmodel.UploadDriverDetailsViewModel
import com.google.android.material.internal.ViewUtils
import com.hbisoft.pickit.PickiT
import com.hbisoft.pickit.PickiTCallbacks
import com.nguyenhoanglam.imagepicker.ui.imagepicker.registerImagePicker
import com.travel.trooute.presentation.ui.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.util.ArrayList
import javax.inject.Inject

@AndroidEntryPoint
class BecomeDriverActivity : BaseActivity(), PickiTCallbacks {

    private val TAG = "BecomeDriverActivity"

    private lateinit var binding: ActivityBecomeDriverBinding
    private lateinit var vehicleImagePicker: ImagePicker
    private lateinit var licenseImagePicker: ImagePicker
    private lateinit var approved: String

    private var yearArrayList = ArrayList<String>()
    private var colorArrayList = ArrayList<String>()
    private var requestForVehicleImg = false
    private var isVehicleImageUriAvailable = false
    private var isLicenseImageUriAvailable = false
    private var vehicleImgFile: File? = null
    private var licenseImgFile: File? = null
    private var pickiT: PickiT? = null

    private var vehicleUri: Uri? = null
    private val vehicleImagePickerLauncher = registerImagePicker { images ->
        if (images.isNotEmpty()) {
            val sampleImage = images[0]
            vehicleUri = sampleImage.uri
            if (vehicleUri?.toString() == null) {
                requestForVehicleImg = false
                isVehicleImageUriAvailable = false
            } else {
                requestForVehicleImg = true
                isVehicleImageUriAvailable = true
                binding.imgVehicle.isVisible = true
                binding.imgVehicle.setImageURI(vehicleUri)
                pickiT?.getPath(vehicleUri, Build.VERSION.SDK_INT)
            }
        }
    }

    private var licenseUri: Uri? = null
    private val licenseImagePickerLauncher = registerImagePicker { images ->
        if (images.isNotEmpty()) {
            val sampleImage = images[0]
            licenseUri = sampleImage.uri
            if (licenseUri?.toString() == null) {
                requestForVehicleImg = false
                isLicenseImageUriAvailable = false
            } else {
                requestForVehicleImg = false
                isLicenseImageUriAvailable = true
                binding.imgDrivingLicense.isVisible = true
                binding.imgDrivingLicense.setImageURI(licenseUri)
                pickiT?.getPath(licenseUri, Build.VERSION.SDK_INT)
            }
        }
    }

    private val uploadDriverDetailsViewModel: UploadDriverDetailsViewModel by viewModels()

    private val updateCarInfoDetailsViewModel: UpdateCarDetailsViewModel by viewModels()

    @Inject
    lateinit var loader: Loader

    @Inject
    lateinit var sharedPreferenceManager: SharedPreferenceManager

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        statusBarColor(R.color.white)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_become_driver)
        vehicleImagePicker = ImagePicker(this, vehicleImagePickerLauncher)
        licenseImagePicker = ImagePicker(this, licenseImagePickerLauncher)
        pickiT = PickiT(this, this, this)

        if (!::approved.isInitialized) {
            approved = ContextCompat.getString(this, R.string.approved).lowercase()
        }

        binding.apply {
            includeAppBar.apply {
                if (sharedPreferenceManager.getDriverStatus()?.lowercase() == approved) {
                    this.toolbarTitle.text = getString(R.string.update_car_info)
                } else {
                    this.toolbarTitle.text = getString(R.string.become_driver)
                }

                this.filter.isVisible = false

                this.arrowBackPress.setOnClickListener {
                    finish()
                }
            }

            ltClickToUploadPhoto.setOnClickListener {
                vehicleImagePicker.openDialog()
            }

            setUpYearDropDown(actYear)
            setUpColorDropDown(actColor)

            ltClickToUploadDrivingLicense.setOnClickListener {
                licenseImagePicker.openDialog()
            }
            if (sharedPreferenceManager.getDriverStatus()?.lowercase() == approved) {
                btnSubmitRequest.text = getString(R.string.update)
            } else {
                btnSubmitRequest.text = getString(R.string.submit_request)
            }

            if (sharedPreferenceManager.getDriverStatus()?.lowercase() == approved) {
                btnSubmitRequest.setOnClickListener {
                    if (
                        isFieldValid(etMake, getString(R.string.make))
                        && isFieldValid(etModel, getString(R.string.model))
                        && isDropdownValid(actYear, yearArrayList, getString(R.string.year))
                        && isDropdownValid(actColor, colorArrayList, getString(R.string.color))
                        && isFieldValid(etVehicleLicensePlate, getString(R.string.vehicle_license_plate))

                    ) {
                        updateCarInfoDetailsViewModel.updateCarDetails(
                            UploadDriverDetailsRequest(
                                make = etMake.text.toString(),
                                model = etModel.text.toString(),
                                registrationNumber = etVehicleLicensePlate.text.toString(),
                                year = actYear.text.toString(),
                                color = actColor.text.toString(),
                                carPhoto = vehicleImgFile,
                                driverLicense = null,
                            )
                        )
                        bindUpdateCarDetailsObserver()
                    }
                }
            } else {
                btnSubmitRequest.setOnClickListener {
                    if (
                        isImageAdded(isVehicleImageUriAvailable, getString(R.string.vehicle))
                        && isFieldValid(etMake, getString(R.string.make))
                        && isFieldValid(etModel, getString(R.string.model))
                        && isDropdownValid(actYear, yearArrayList, getString(R.string.year))
                        && isDropdownValid(actColor, colorArrayList, getString(R.string.color))
                        && isFieldValid(etVehicleLicensePlate, getString(R.string.vehicle_license_plate))
                        && isImageAdded(isLicenseImageUriAvailable, getString(R.string.license))
                    ) {
                        uploadDriverDetailsViewModel.uploadDriverDetails(
                            UploadDriverDetailsRequest(
                                make = etMake.text.toString(),
                                model = etModel.text.toString(),
                                registrationNumber = etVehicleLicensePlate.text.toString(),
                                year = actYear.text.toString(),
                                color = actColor.text.toString(),
                                carPhoto = vehicleImgFile,
                                driverLicense = licenseImgFile,
                            )
                        )
                        bindUploadDriverDetailsObserver()
                    }
                }
            }

        }

        addVehicleData()
    }


    private fun setUpYearDropDown(actYear: AutoCompleteTextView) {
        yearArrayList.add("2024")
        yearArrayList.add("2023")
        yearArrayList.add("2022")
        yearArrayList.add("2021")
        yearArrayList.add("2020")
        yearArrayList.add("2019")
        yearArrayList.add("2018")
        yearArrayList.add("2017")
        yearArrayList.add("2016")
        yearArrayList.add("2015")
        yearArrayList.add("2014")
        yearArrayList.add("2013")
        yearArrayList.add("2012")
        yearArrayList.add("2011")
        yearArrayList.add("2010")
        yearArrayList.add("2009")
        yearArrayList.add("2008")
        yearArrayList.add("2007")
        yearArrayList.add("2006")
        yearArrayList.add("2005")
        yearArrayList.add("2004")
        yearArrayList.add("2003")
        yearArrayList.add("2002")
        yearArrayList.add("2001")
        yearArrayList.add("2000")


        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, yearArrayList)

        actYear.apply {
            setAdapter(adapter)
            setOnItemClickListener { parent, view, position, id ->
                parent.getItemAtPosition(position).toString()
            }
        }
    }

    private fun setUpColorDropDown(actColor: AutoCompleteTextView) {
        colorArrayList.add(getString(R.string.white))
        colorArrayList.add(getString(R.string.black))
        colorArrayList.add(getString(R.string.gray))
        colorArrayList.add(getString(R.string.silver))
        colorArrayList.add(getString(R.string.blue))
        colorArrayList.add(getString(R.string.red))
        colorArrayList.add(getString(R.string.brown))
        colorArrayList.add(getString(R.string.green))
        colorArrayList.add(getString(R.string.orange))
        colorArrayList.add(getString(R.string.beige))
        colorArrayList.add(getString(R.string.purple))
        colorArrayList.add(getString(R.string.gold))
        colorArrayList.add(getString(R.string.yellow))
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, colorArrayList)

        actColor.apply {
            setAdapter(adapter)
            setOnItemClickListener { parent, view, position, id ->
                parent.getItemAtPosition(position).toString()
            }
        }
    }

    private fun bindUploadDriverDetailsObserver() {
        lifecycleScope.launch {
            uploadDriverDetailsViewModel.uploadDriverDetailsState.collect {
                loader.cancel()
                when (it) {
                    is Resource.ERROR -> {
                        Log.e(
                            TAG,
                            "bindUploadDriverDetailsObserver: Error -> " + it.message.toString()
                        )
                        Toast(this@BecomeDriverActivity).showErrorMessage(
                            this@BecomeDriverActivity, it.message.toString()
                        )
                    }

                    Resource.LOADING -> {
                        loader.show()
                    }

                    is Resource.SUCCESS -> {
                        Toast(this@BecomeDriverActivity).showSuccessMessage(
                            this@BecomeDriverActivity, it.data.message.toString()
                        )
                        startActivity(
                            Intent(
                                this@BecomeDriverActivity, SuccessActivity::class.java
                            )
                        )
                    }
                }
            }
        }
    }

    private fun bindUpdateCarDetailsObserver() {
        lifecycleScope.launch {
            updateCarInfoDetailsViewModel.updateCarInfoDetailsState.collect {
                loader.cancel()
                when (it) {
                    is Resource.ERROR -> {
                        Log.e(
                            TAG,
                            "bindUpdateCarDetailsObserver: Error -> " + it.message.toString()
                        )
                        Toast(this@BecomeDriverActivity).showErrorMessage(
                            this@BecomeDriverActivity, it.message.toString()
                        )
                    }

                    Resource.LOADING -> {
                        loader.show()
                    }

                    is Resource.SUCCESS -> {
                        Toast(this@BecomeDriverActivity).showSuccessMessage(
                            this@BecomeDriverActivity, it.data.message.toString()
                        )
                        sharedPreferenceManager.getAuthModelFromPref().let { user ->
                            user?.carDetails = it.data.data
                            if (user != null) {
                                sharedPreferenceManager.updateUserInPref(user)
                            }
                        }
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
            if (requestForVehicleImg) {
                vehicleImgFile = File(path.toString())
            } else {
                licenseImgFile = File(path.toString())
            }
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

    private fun addVehicleData() {
        sharedPreferenceManager.getAuthModelFromPref().let { user ->
            var carDetails = user?.carDetails
            if (!carDetails?.make.isNullOrEmpty()) {
                binding.etMake.setText(carDetails?.make)
            }
            if (!carDetails?.model.isNullOrEmpty()) {
                binding.etModel.setText(carDetails?.model)
            }
            if (!carDetails?.registrationNumber.isNullOrEmpty()) {
                binding.etVehicleLicensePlate.setText(carDetails?.registrationNumber)
            }
            if (carDetails?.year.toString() != "null") {
                binding.actYear.setText(carDetails?.year.toString())
            }
            if (!carDetails?.color.isNullOrEmpty()) {
                binding.actColor.setText(carDetails?.color)
            }
            if (!carDetails?.driverLicense.isNullOrEmpty()) {
                binding.imgDrivingLicense.isVisible = true
                binding.ltClickToUploadDrivingLicense.isVisible  = true
                loadImage(binding.imgDrivingLicense, carDetails?.driverLicense.toString())
            }



            if (vehicleUri != null) {
                binding.imgVehicle.setImageURI(vehicleUri)
            } else {
                loadImage(binding.imgVehicle, carDetails?.photo.toString())
            }

            binding.imgVehicle.isVisible = true
            binding.ltClickToUploadPhoto.isVisible  = true
            setUpYearDropDown(binding.actYear)
            setUpColorDropDown(binding.actColor)
            if (sharedPreferenceManager.getDriverStatus()?.lowercase() == approved) {
                binding.ltDrivingLicenseSection.isVisible = false
            } else {
                binding.ltDrivingLicenseSection.isVisible = true
            }
        }

    }

}