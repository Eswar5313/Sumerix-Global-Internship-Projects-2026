package com.eswar.profile.ui

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

/**
 * Shared behaviour for every screen: edge-to-edge drawing (required when targeting
 * Android 15) with system-bar insets applied as padding so content never hides
 * behind the status or navigation bar.
 */
abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
    }

    protected fun applySystemBarInsets(root: View) {
        val start = root.paddingTop
        val bottom = root.paddingBottom
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = start + bars.top, bottom = bottom + bars.bottom)
            insets
        }
    }

    companion object {
        /** Intent extras used to pass data between Activities. */
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_SOURCE = "extra_source"
    }
}
