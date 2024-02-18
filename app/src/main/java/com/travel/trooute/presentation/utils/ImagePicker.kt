package com.travel.trooute.presentation.utils

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import com.travel.trooute.R
import com.travel.trooute.databinding.ImagePickerBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.nguyenhoanglam.imagepicker.model.CustomColor
import com.nguyenhoanglam.imagepicker.model.GridCount
import com.nguyenhoanglam.imagepicker.model.ImagePickerConfig
import com.nguyenhoanglam.imagepicker.model.IndicatorType
import com.nguyenhoanglam.imagepicker.model.RootDirectory
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePickerLauncher
import com.permissionx.guolindev.PermissionX


class ImagePicker(
    private val activityContext: FragmentActivity,
    private val launcher: ImagePickerLauncher
) {

    private val TAG = "ImagePicker"

    @SuppressLint("NewApi")
    fun openDialog() {
        // Inflate the layout using DataBinding
        val binding: ImagePickerBinding = DataBindingUtil.inflate(
            LayoutInflater.from(activityContext),
            R.layout.image_picker,
            null,
            false
        )

        // Create the BottomSheetDialog
        val bottomSheetDialog = BottomSheetDialog(activityContext, R.style.CustomBottomSheetDialog)
        bottomSheetDialog.setContentView(binding.root)
        bottomSheetDialog.show()

        binding.apply {
            openCamera.setOnClickListener {
                if (cameraPermission()) {
                    openCamera()
                    bottomSheetDialog.dismiss()
                }
            }

            openGallery.setOnClickListener {
                if (galleryPermission()) {
                    openGallery()
                    bottomSheetDialog.dismiss()
                }
            }
        }
    }

    private fun openCamera() {
        val config = ImagePickerConfig(
            isCameraMode = true,
            isShowCamera = true,
            limitSize = 10,
            selectedIndicatorType = IndicatorType.NUMBER,
            rootDirectory = RootDirectory.DCIM,
            subDirectory = "Image Picker",
            folderGridCount = GridCount(2, 4),
            imageGridCount = GridCount(3, 5),
            customColor = CustomColor(
                background = "#000000",
                statusBar = "#000000",
                toolbar = "#212121",
                toolbarTitle = "#FFFFFF",
                toolbarIcon = "#FFFFFF",
            )
        )

        launcher.launch(config)
    }

    private fun openGallery() {
        val config = ImagePickerConfig(
            isSingleSelectMode = true,
            isFolderMode = true,
            limitSize = 10,
            selectedIndicatorType = IndicatorType.NUMBER,
            rootDirectory = RootDirectory.DCIM,
            subDirectory = "Image Picker",
            folderGridCount = GridCount(2, 4),
            imageGridCount = GridCount(3, 5),
            customColor = CustomColor(
                background = "#000000",
                statusBar = "#000000",
                toolbar = "#212121",
                toolbarTitle = "#FFFFFF",
                toolbarIcon = "#FFFFFF"
            )
        )

        launcher.launch(config)
    }


    @RequiresApi(Build.VERSION_CODES.R)
    fun galleryPermission(): Boolean {
        var isGranted = false
        PermissionX.init(activityContext)
            .permissions(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(
                    deniedList,
                    "Core fundamental are based on these permissions",
                    "OK",
                    "Cancel"
                )
            }
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(
                    deniedList,
                    "You need to allow necessary permissions in Settings manually",
                    "OK",
                    "Cancel"
                )
            }
            .request { allGranted, grantedList, deniedList ->
                isGranted = allGranted
            }

        return isGranted
    }

    private fun cameraPermission(): Boolean {
        var isGranted = false
        PermissionX.init(activityContext)
            .permissions(
                Manifest.permission.CAMERA
            )
            .onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(
                    deniedList,
                    "Core fundamental are based on these permissions",
                    "OK",
                    "Cancel"
                )
            }
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(
                    deniedList,
                    "You need to allow necessary permissions in Settings manually",
                    "OK",
                    "Cancel"
                )
            }
            .request { allGranted, grantedList, deniedList ->
                isGranted = allGranted
            }

        return isGranted
    }

}