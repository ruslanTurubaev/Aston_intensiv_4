package com.example.aston_intensiv_4_2.clock_widget.extentions

import android.app.Activity
import android.view.View

fun <T> unsafeLazy(initializer: () -> T) = lazy(LazyThreadSafetyMode.NONE, initializer)

fun <T : View> Activity.find(idRes : Int) = unsafeLazy<T> { findViewById(idRes) }