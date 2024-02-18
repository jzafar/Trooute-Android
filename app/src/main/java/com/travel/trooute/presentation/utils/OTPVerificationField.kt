package com.travel.trooute.presentation.utils

import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import com.travel.trooute.R
import com.travel.trooute.presentation.utils.WindowsManager.hideSoftKeyboard

// Help link -> https://stackoverflow.com/questions/38872546/edit-text-for-otp-with-each-letter-in-separate-positions
class GenericKeyEvent internal constructor(
    private val currentView: EditText,
    private val previousView: EditText?
) : View.OnKeyListener {
    override fun onKey(p0: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if (event!!.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL && currentView.id != R.id.teCode_1 && currentView.text.isEmpty()) {
            //If current is empty then previous EditText's number will also be deleted
            previousView!!.text = null
            previousView.requestFocus()
            return true
        }
        return false
    }
}

class GenericTextWatcher internal constructor(
    private val currentView: View,
    private val nextView: View?
) :
    TextWatcher {
    override fun afterTextChanged(editable: Editable) { // TODO Auto-generated method stub
        val text = editable.toString()
        when (currentView.id) {
            R.id.teCode_1 -> if (text.length == 1) nextView?.requestFocus()
            R.id.teCode_2 -> if (text.length == 1) nextView?.requestFocus()
            R.id.teCode_3 -> if (text.length == 1) nextView?.requestFocus()
            //You can use EditText4 same as above to hide the keyboard
            R.id.teCode_4 -> if (text.length == 1) hideSoftKeyboard(currentView)
        }
    }

    override fun beforeTextChanged(
        arg0: CharSequence,
        arg1: Int,
        arg2: Int,
        arg3: Int
    ) { // TODO Auto-generated method stub
    }

    override fun onTextChanged(
        arg0: CharSequence,
        arg1: Int,
        arg2: Int,
        arg3: Int
    ) { // TODO Auto-generated method stub
    }
}