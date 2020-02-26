package com.zabster.openweatherapp.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.isVisible

fun Context.toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Context.toast(msg: Int) {
    Toast.makeText(this, getString(msg), Toast.LENGTH_SHORT).show()
}

fun View?.visible() {
    if (this == null) return
    isEnabled = true
    visibility = View.VISIBLE
}

fun View?.invisible() {
    if (this == null) return
    isEnabled = false
    visibility = View.INVISIBLE
}

fun View?.gone() {
    if (this == null) return
    visibility = View.GONE
}

fun View?.setInvisible(isVisible: Boolean) {
    if (isVisible) visible()
    else invisible()
}

fun Activity.hideKeyBoard() {
    val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputManager.hideSoftInputFromWindow(window.currentFocus?.windowToken, 0)
}

fun View?.showKeyBoard() {
    if (this == null) return
    val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    postDelayed({
        inputManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }, 200L)
}