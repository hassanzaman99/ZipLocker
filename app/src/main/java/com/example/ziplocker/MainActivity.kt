package com.example.ziplocker

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.ziplocker.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val imageUnzip = intArrayOf(
        R.drawable.s2,
        R.drawable.s3,
        R.drawable.s4,
        R.drawable.s5,
        R.drawable.s6,
        R.drawable.s7,
        R.drawable.s8,
        R.drawable.s9,
        R.drawable.s10
    )
    private var frameNumber = 0
    private var isDownFromStart = false
    private var mScreenHeight = 0
    private var mScreenWidth = 0
    private var mStartWidthRange = 0
    private var mEndWidthRange = 0

    private lateinit var vibrator: Vibrator

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        makeFullScreen()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.settingsButton.setOnClickListener {
            // Create an Intent to navigate to SettingsActivity
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
        // Initialize vibrator service
        vibrator = getSystemService(Vibrator::class.java)!!

        // Apply window insets to adjust for system bars
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.zipImageView.setBackgroundResource(imageUnzip[0])

        binding.zipImageView.setOnTouchListener { v, event ->
            val totalFrames = imageUnzip.size
            val frameHeight = mScreenHeight / totalFrames // Height range for each frame
            var i: Int

            mScreenHeight = binding.zipImageView.height
            mScreenWidth = binding.zipImageView.width

            mStartWidthRange = (2 * (mScreenWidth / 5))
            mEndWidthRange = (3 * (mScreenWidth / 5))

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    isDownFromStart =
                        event.y < mScreenHeight / 4 && event.x > mStartWidthRange && event.x < mEndWidthRange

                }

                MotionEvent.ACTION_MOVE -> {
                    if (isDownFromStart) {
                        if (event.x > mStartWidthRange && event.x < mEndWidthRange) {
                            i = (event.y / frameHeight).toInt()
                            // Only update if the frame changes and within valid range
                            if (i != frameNumber && i in 0 until totalFrames) {
                                frameNumber = i
                                setImage(frameNumber)
                                vibrateSmoothly(event.y / mScreenHeight) // Add haptic feedback based on drag progress
                            }
                        }
                    }
                }

                MotionEvent.ACTION_UP -> {
                    if (frameNumber >= totalFrames - 1) {
                        frameNumber = 0
                        isDownFromStart = true
                        showBiometricPrompt()
                    } else {
                        resetZipper() // Reset zipper if not fully opened
                    }

                    v.isPressed = false
                    v.performClick()  // Notify system of click action
                }
            }

            true
        }
    }
    // Apply the saved gradient when the activity is resumed
    override fun onResume() {
        super.onResume()
        binding.root.applyGradientBackground(this)
    }

    @Suppress("DEPRECATION")
    private fun makeFullScreen() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

    private fun vibrateSmoothly(progress: Float) {
        if (vibrator.hasVibrator()) {
            val vibrationIntensity = (progress * 100).toInt() // Adjust intensity based on progress
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        // Use VibrationEffect for Android O and above
                        vibrator.vibrate(
                            VibrationEffect.createOneShot(
                                vibrationIntensity.toLong(),
                                VibrationEffect.DEFAULT_AMPLITUDE
                            )
                        )
                    } else {
                        // Use the deprecated vibrate method for older Android versions (below O)
                        @Suppress("DEPRECATION")
                        vibrator.vibrate(vibrationIntensity.toLong())
                    }
                }
            }
        }
    }


    private fun setImage(paramInt: Int) {
        when (paramInt) {
            0 -> binding.zipImageView.setBackgroundResource(imageUnzip[0])
            1 -> binding.zipImageView.setBackgroundResource(imageUnzip[1])
            2 -> binding.zipImageView.setBackgroundResource(imageUnzip[2])
            3 -> binding.zipImageView.setBackgroundResource(imageUnzip[3])
            4 -> binding.zipImageView.setBackgroundResource(imageUnzip[4])
            5 -> binding.zipImageView.setBackgroundResource(imageUnzip[5])
            6 -> binding.zipImageView.setBackgroundResource(imageUnzip[6])
            7 -> binding.zipImageView.setBackgroundResource(imageUnzip[7])
            8 -> binding.zipImageView.setBackgroundResource(imageUnzip[8])
            9 -> binding.zipImageView.setBackgroundResource(imageUnzip[9])
        }
    }

    private fun showBiometricPrompt() {
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    unlockDevice()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    resetZipper() // Reset zipper image on failure
                    Toast.makeText(this@MainActivity, "Authentication failed!", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    resetZipper() // Reset zipper image on error
                    Toast.makeText(
                        this@MainActivity,
                        "Authentication error: $errString",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock Device")
            .setSubtitle("Authenticate to proceed")
            .setNegativeButtonText("Cancel")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    private fun unlockDevice() {
        resetZipper() // Reset the zipper image to starting position
        Toast.makeText(this, "Device Unlocked!", Toast.LENGTH_SHORT).show()
    }

    private fun resetZipper() {
        // Animate the zipper back to the starting position (fully closed)
        animateZipperClosing()
    }

    private fun animateZipperOpening() {
        val animator = ValueAnimator.ofInt(0, mScreenHeight)  // Animate from top to bottom
        animator.duration = 500  // Adjust duration for smoothness
        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            updateZipperImage(value)
        }
        animator.start()
    }

    private fun animateZipperClosing() {
        val animator = ValueAnimator.ofInt(mScreenHeight, 0)  // Animate from bottom to top
        animator.duration = 500
        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            updateZipperImage(value)
        }
        animator.start()
    }

    private fun updateZipperImage(value: Int) {
        val totalFrames = imageUnzip.size
        val frameHeight = mScreenHeight / totalFrames
        val frameIndex = (value / frameHeight).coerceIn(0, totalFrames - 1)
        setImage(frameIndex)
    }
}






