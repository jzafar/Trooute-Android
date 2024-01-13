package com.example.trooute.presentation.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.Patterns
import android.widget.AutoCompleteTextView
import android.widget.CheckBox
import android.widget.Toast
import com.google.android.material.internal.ViewUtils.showKeyboard
import com.google.android.material.textfield.TextInputEditText

fun Context.isImageAdded(image: Boolean, message: String): Boolean {
    return if (!image) {
        Toast(this).showWarningMessage(this, "$message image is required")
        false
    } else {
        true
    }
}

@SuppressLint("RestrictedApi")
fun Context.isFieldValid(field: TextInputEditText, message: String): Boolean {
    val mField = field.text.toString()
    return if (mField.isBlank() || mField.isEmpty() || mField.trim() == "") {
        field.requestFocus()
        showKeyboard(field)
        Toast(this).showWarningMessage(this, "$message field can't be blank")
        false
    } else {
        true
    }
}

fun Context.isTermsCheckBoxClicked(field: CheckBox): Boolean {
    if (field.isChecked) {
        return true
    } else {
        Toast(this).showWarningMessage(this, "Please accept terms and conditions")
        return false
    }
}

fun Context.isDropdownValid(
    field: AutoCompleteTextView,
    array: ArrayList<String>,
    message: String
): Boolean {
    field.validator = object : AutoCompleteTextView.Validator {
        override fun isValid(text: CharSequence?): Boolean {
            return !text.isNullOrEmpty() && text.isNotBlank() && array.contains(text.toString())
        }

        override fun fixText(invalidText: CharSequence?): CharSequence {
            return invalidText ?: ""
        }
    }

    val selectedText = field.text.toString().trim()

    return if (field.validator.isValid(selectedText)) {
        true
    } else {
        Toast(this).showWarningMessage(this, "Please select $message")
        false
    }
}

@SuppressLint("RestrictedApi")
fun Context.isEmailValid(email: TextInputEditText): Boolean {
    val mEmail = email.text.toString()
    return if (mEmail.isBlank() || mEmail.isEmpty() || mEmail.trim() == "") {
        email.requestFocus()
        showKeyboard(email)
        Toast(this).showWarningMessage(this, "Email can't be blank")
        false
    } else if (!Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()) {
        email.requestFocus()
        showKeyboard(email)
        Toast(this).showErrorMessage(this, "Email is not valid")
        false
    } else {
        true
    }
}

@SuppressLint("RestrictedApi")
fun Context.isPhoneNumberValid(phone: TextInputEditText): Boolean {
    val phoneNumber = phone.text.toString()
    return if (phoneNumber.isBlank() || phoneNumber.isEmpty() || phoneNumber.trim() == "") {
        phone.requestFocus()
        showKeyboard(phone)
        Toast(this).showWarningMessage(this, "Phone number can't be blank")
        false
    } else if (!Patterns.PHONE.matcher(phoneNumber).matches()) {
        phone.requestFocus()
        showKeyboard(phone)
        Toast(this).showErrorMessage(this, "Phone number is not valid")
        false
    } else {
        true
    }
}

@SuppressLint("RestrictedApi")
fun Context.isPasswordValid(
    isLogin: Boolean,
    password: TextInputEditText
): Boolean {
    val mPassword = password.text.toString()
    return if (mPassword.isBlank() || mPassword.isEmpty() || mPassword.trim() == "") {
        password.requestFocus()
        showKeyboard(password)
        Toast(this).showWarningMessage(this, "Password can't be blank")
        false
    } else if (!isLogin && mPassword.length < 8) {
        password.requestFocus()
        showKeyboard(password)
        Toast(this).showErrorMessage(
            this,
            "Password needs to consist of at least 8 characters"
        )
        false
    } else {
        true
    }
}

@SuppressLint("RestrictedApi")
fun Context.isConfirmPasswordValid(
    password: TextInputEditText,
    confirmPassword: TextInputEditText
): Boolean {
    val mPassword = password.text.toString()
    val mConfirmPassword = confirmPassword.text.toString()
    return when {
        mConfirmPassword.trim().isEmpty() || mConfirmPassword.isBlank() -> {
            confirmPassword.requestFocus()
            showKeyboard(confirmPassword)
            Toast(this).showWarningMessage(this, "Retype your password can't be blank")
            false
        }

        mConfirmPassword != mPassword -> {
            confirmPassword.requestFocus()
            showKeyboard(confirmPassword)
            Toast(this).showErrorMessage(
                this,
                "Passwords not matched"
            )
            false
        }

        else -> {
            true
        }
    }
}
