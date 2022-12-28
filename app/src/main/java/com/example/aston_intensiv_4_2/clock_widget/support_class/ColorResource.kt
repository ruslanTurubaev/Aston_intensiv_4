package com.example.aston_intensiv_4_2.clock_widget.support_class

import android.content.Context
import androidx.core.content.ContextCompat

sealed class ColorResource {

    @androidx.annotation.ColorInt abstract fun getColor(context: Context): Int

    data class ColorInt(@androidx.annotation.ColorInt val color: Int) : ColorResource() {
        override fun getColor(context: Context) = color
    }

    data class ColorRes(@androidx.annotation.ColorRes val color: Int) : ColorResource() {

        override fun getColor(context: Context) = ContextCompat.getColor(context, color)
    }

}