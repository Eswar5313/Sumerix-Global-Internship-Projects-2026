package com.eswar.profile.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.eswar.profile.R
import com.eswar.profile.data.ProfileStats

class ContactActivity : SectionActivity() {

    override val defaultTitle: String get() = getString(R.string.section_contact)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)
        applySystemBarInsets(findViewById(R.id.contact_root))
        setUpToolbar(findViewById(R.id.toolbar))

        findViewById<MaterialButton>(R.id.button_email).setOnClickListener { composeEmail(null, null) }
        findViewById<MaterialButton>(R.id.button_linkedin).setOnClickListener { openUrl(getString(R.string.link_linkedin)) }
        findViewById<MaterialButton>(R.id.button_github).setOnClickListener { openUrl(getString(R.string.link_github)) }
        findViewById<MaterialButton>(R.id.button_send).setOnClickListener { submitForm() }
    }

    /** Validates the contact form; returns true when an e-mail intent was launched. */
    internal fun submitForm(): Boolean {
        val nameLayout = findViewById<TextInputLayout>(R.id.layout_name)
        val emailLayout = findViewById<TextInputLayout>(R.id.layout_email)
        val messageLayout = findViewById<TextInputLayout>(R.id.layout_message)
        val name = findViewById<TextInputEditText>(R.id.input_name).text?.toString()?.trim().orEmpty()
        val email = findViewById<TextInputEditText>(R.id.input_email).text?.toString()?.trim().orEmpty()
        val message = findViewById<TextInputEditText>(R.id.input_message).text?.toString()?.trim().orEmpty()

        nameLayout.error = if (name.isEmpty()) getString(R.string.error_required) else null
        emailLayout.error = if (!ProfileStats.isValidEmail(email)) getString(R.string.error_email) else null
        messageLayout.error = if (message.length < 10) getString(R.string.error_message) else null
        if (nameLayout.error != null || emailLayout.error != null || messageLayout.error != null) return false

        composeEmail(getString(R.string.contact_subject, name), "$message\n\n- $name ($email)")
        return true
    }

    private fun composeEmail(subject: String?, body: String?) {
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + getString(R.string.contact_email))).apply {
            subject?.let { putExtra(Intent.EXTRA_SUBJECT, it) }
            body?.let { putExtra(Intent.EXTRA_TEXT, it) }
        }
        launchSafely(intent)
    }

    private fun openUrl(url: String) = launchSafely(Intent(Intent.ACTION_VIEW, Uri.parse(url)))

    private fun launchSafely(intent: Intent) {
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, R.string.error_no_app, Toast.LENGTH_SHORT).show()
        }
    }
}
