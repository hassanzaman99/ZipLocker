package com.example.ziplocker

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.ziplocker.databinding.ActivitySettingsBinding
import com.github.dhaval2404.colorpicker.ColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private var selectedColor1: Int = R.color.d_color1 // Default color 1
    private var selectedColor2: Int = R.color.d_color2 // Default color 2
    private var selectedColor3: Int = R.color.d_color3 // Default color 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set click listener for the color picker buttons
        binding.color1Label.setOnClickListener {
            openColorPickerDialog { color ->
                selectedColor1 = color
                // Update the background color of color1View with the selected color
                binding.color1View.setBackgroundColor(color)
            }
        }


        binding.color2Label.setOnClickListener {
            openColorPickerDialog { color ->
                selectedColor2 = color
                binding.color2View.setBackgroundColor(color)

            }
        }

        binding.color3Label.setOnClickListener {
            openColorPickerDialog { color ->
                selectedColor3 = color
                binding.color3View.setBackgroundColor(color)

            }
        }

        binding.saveButton.setOnClickListener {
            saveColors()
            applyGradientColor(selectedColor1, selectedColor2, selectedColor3)
            Toast.makeText(this, "Gradient applied!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        applySavedGradient()
    }

    private fun openColorPickerDialog(onColorSelected: (Int) -> Unit) {
        ColorPickerDialog
            .Builder(this)
            .setTitle("Pick a Color")
            .setColorShape(ColorShape.CIRCLE)
            .setDefaultColor(R.color.black)
            .setColorListener { color, colorHex ->

                onColorSelected(color)
            }
            .show()
    }

    private fun applyGradientColor(color1: Int, color2: Int, color3: Int) {
        // Create gradient drawable with the three selected colors
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(color1, color2, color3) // Colors for gradient
        )

        // Apply the gradient to the background of the root view
        binding.main.background = gradientDrawable
    }

    private fun saveColors() {
        val sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("selectedColor1", selectedColor1)
        editor.putInt("selectedColor2", selectedColor2)
        editor.putInt("selectedColor3", selectedColor3)
        editor.apply()
    }

    private fun applySavedGradient() {
        val sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)

        // Retrieve all three saved colors from SharedPreferences
        val savedColor1 = sharedPreferences.getInt("selectedColor1", R.color.d_color1)
        val savedColor2 = sharedPreferences.getInt("selectedColor2", R.color.d_color2)
        val savedColor3 = sharedPreferences.getInt("selectedColor3", R.color.d_color3)

        binding.color1View.setBackgroundColor(savedColor1)
        binding.color2View.setBackgroundColor(savedColor2)
        binding.color3View.setBackgroundColor(savedColor3)

        // Apply the gradient background using saved colors
        applyGradientColor(savedColor1, savedColor2, savedColor3)
    }
}
