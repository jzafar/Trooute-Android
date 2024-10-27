package com.travel.trooute.presentation.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.app.ShareCompat

fun Context.inviteFriend() {
    ShareCompat
        .IntentBuilder(this)
        .setType("text/plain")
        .setChooserTitle("Invite a friend")
//        .setChooserTitle("Share text with: ")
        .setText("Hey! I want to invite you to try Trooute App. Get where you’re going with affordable, convenient rides. You can download App from this link: https://play.google.com/store/apps/details?id=com.travel.trooute")
        .startChooser()
}

fun Context.composeEmail() {
    val recipientEmail = "support@trooute.com"
    val subject = "Subject of the email"
    val message = "Desired text to share"

    val intent = Intent(Intent.ACTION_SENDTO)
    intent.data = Uri.parse("mailto:$recipientEmail")
    intent.putExtra(Intent.EXTRA_SUBJECT, subject)
    intent.putExtra(Intent.EXTRA_TEXT, message)

    try {
        startActivity(intent)
    } catch (e: Exception) {
        // Handle the case where no email app is available
        Toast(this).showErrorMessage(this, e.message)
    }
}