package com.eswar.profile.ui

import com.google.android.material.appbar.MaterialToolbar

/** Common toolbar wiring for the three detail screens (inheritance + polymorphism). */
abstract class SectionActivity : BaseActivity() {

    /** Each subclass supplies a fallback title when it is opened without an Intent extra. */
    protected abstract val defaultTitle: String

    protected fun setUpToolbar(toolbar: MaterialToolbar) {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // Data passed from MainActivity through the Intent
        supportActionBar?.title = intent.getStringExtra(EXTRA_TITLE) ?: defaultTitle
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }
}
