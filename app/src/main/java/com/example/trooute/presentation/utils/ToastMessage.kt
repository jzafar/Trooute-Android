package com.example.trooute.presentation.utils

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import com.example.trooute.databinding.ErrorToastMessageDesignBinding
import com.example.trooute.databinding.SuccessToastMessageDesignBinding
import com.example.trooute.databinding.WarningToastMessageDesignBinding

fun Toast.showSuccessMessage(
    context: Context, message: String?
) {
    // Inflate the custom toast layout using viewBinding
    val toastBinding = SuccessToastMessageDesignBinding.inflate(LayoutInflater.from(context))

    toastBinding.apply {
        toastMessage.text = message
    }

    // Use the application extension function
    this.apply {
        // Set the gravity of the toast
        setGravity(Gravity.TOP or Gravity.FILL_HORIZONTAL, 0, 0)
        // Set the duration of the toast
        duration = Toast.LENGTH_SHORT
        // Set the view of the toast to the custom layout
        view = toastBinding.root
        // Show the toast
        show()
    }
}

fun Toast.showWarningMessage(
    context: Context, message: String?
) {
    // Inflate the custom toast layout using viewBinding
    val toastBinding = WarningToastMessageDesignBinding.inflate(LayoutInflater.from(context))

    toastBinding.apply {
        toastMessage.text = message
    }

    // Use the application extension function
    this.apply {
        // Set the gravity of the toast
        setGravity(Gravity.TOP or Gravity.FILL_HORIZONTAL, 0, 0)
        // Set the duration of the toast
        duration = Toast.LENGTH_SHORT
        // Set the view of the toast to the custom layout
        view = toastBinding.root
        // Show the toast
        show()
    }
}

fun Toast.showErrorMessage(
    context: Context, message: String?
) {
    // Inflate the custom toast layout using viewBinding
    val toastBinding = ErrorToastMessageDesignBinding.inflate(LayoutInflater.from(context))

    toastBinding.apply {
        toastMessage.text = message
    }

    // Use the application extension function
    this.apply {
        // Set the gravity of the toast
        setGravity(Gravity.TOP or Gravity.FILL_HORIZONTAL, 0, 0)
        // Set the duration of the toast
        duration = Toast.LENGTH_SHORT
        // Set the view of the toast to the custom layout
        view = toastBinding.root
        // Show the toast
        show()
    }
}