package com.eswar.profile.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.eswar.profile.R
import com.eswar.profile.data.ProfileStats

/** Profile (home) screen - entry point to About, Skills and Contact. */
class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        applySystemBarInsets(findViewById(R.id.main_root))

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        findViewById<TextView>(R.id.avatar_initials).text =
            ProfileStats.initials(getString(R.string.profile_name))

        // Each card opens a separate Activity and passes its title through the Intent.
        bindSection(R.id.card_about, AboutActivity::class.java, R.string.section_about)
        bindSection(R.id.card_skills, SkillsActivity::class.java, R.string.section_skills)
        bindSection(R.id.card_contact, ContactActivity::class.java, R.string.section_contact)

        findViewById<MaterialButton>(R.id.button_share).setOnClickListener { shareProfile() }
    }

    private fun bindSection(cardId: Int, target: Class<*>, titleRes: Int) {
        findViewById<MaterialCardView>(cardId).setOnClickListener { openSection(target, titleRes) }
    }

    internal fun openSection(target: Class<*>, titleRes: Int) {
        val intent = Intent(this, target).apply {
            putExtra(EXTRA_TITLE, getString(titleRes))
            putExtra(EXTRA_SOURCE, "profile")
        }
        startActivity(intent)
    }

    private fun shareProfile() {
        val text = getString(R.string.share_text, getString(R.string.profile_name), getString(R.string.link_github))
        val send = Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, text)
        startActivity(Intent.createChooser(send, getString(R.string.share_profile)))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_toggle_theme -> {
            val night = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
            AppCompatDelegate.setDefaultNightMode(
                if (night) AppCompatDelegate.MODE_NIGHT_NO else AppCompatDelegate.MODE_NIGHT_YES
            )
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    @Suppress("unused")
    private fun View.visibleIf(condition: Boolean) { visibility = if (condition) View.VISIBLE else View.GONE }
}
