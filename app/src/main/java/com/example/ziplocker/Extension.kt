package com.example.ziplocker

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout


fun View.applyGradientBackground(context: Context) {

    val sharedPreferences = context.getSharedPreferences("AppPreferences", MODE_PRIVATE)

    val savedColor1 = sharedPreferences.getInt("selectedColor1", Color.parseColor("#FF0000")) // Default color 1
    val savedColor2 = sharedPreferences.getInt("selectedColor2", Color.parseColor("#00FF00")) // Default color 2
    val savedColor3 = sharedPreferences.getInt("selectedColor3", Color.parseColor("#0000FF")) // Default color 3

    val gradientDrawable = GradientDrawable(
        GradientDrawable.Orientation.TOP_BOTTOM,
        intArrayOf(savedColor1, savedColor2, savedColor3)
    )
    this.background = gradientDrawable
}

