package com.eswar.profile.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import com.eswar.profile.R
import com.eswar.profile.data.ProfileStats

/** Branded launch screen. Opens the profile after [SPLASH_DELAY_MS]. */
class SplashActivity : BaseActivity() {

    private val handler = Handler(Looper.getMainLooper())
    private val openProfile = Runnable {
        startActivity(Intent(this, MainActivity::class.java).putExtra(EXTRA_SOURCE, "splash"))
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        findViewById<TextView>(R.id.splash_initials).text =
            ProfileStats.initials(getString(R.string.profile_name))
        handler.postDelayed(openProfile, SPLASH_DELAY_MS)
    }

    override fun onDestroy() {
        handler.removeCallbacks(openProfile)   // avoid leaking the Activity on rotation/back
        super.onDestroy()
    }

    companion object {
        const val SPLASH_DELAY_MS = 1200L
    }
}
