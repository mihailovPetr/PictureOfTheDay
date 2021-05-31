package com.example.pictureoftheday.view

import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

fun View.showSnackBar(
    text: String,
    actionText: String,
    length: Int = Snackbar.LENGTH_INDEFINITE,
    action: ((View) -> Unit)? = null
) {
    Snackbar.make(this, text, length).setAction(actionText, action).show()
}

fun Fragment.toast(string: String?) {
    Toast.makeText(context, string, Toast.LENGTH_LONG).apply {
        setGravity(Gravity.BOTTOM, 0, 250)
        show()
    }
}
