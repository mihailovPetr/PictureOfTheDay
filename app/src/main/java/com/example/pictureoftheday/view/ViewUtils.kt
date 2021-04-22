package com.example.pictureoftheday.view

import android.view.View
import com.google.android.material.snackbar.Snackbar

fun View.showSnackBar(
    text: String,
    actionText: String,
    length: Int = Snackbar.LENGTH_INDEFINITE,
    action: ((View) -> Unit)? = null
) {
    Snackbar.make(this, text, length).setAction(actionText, action).show()
}
