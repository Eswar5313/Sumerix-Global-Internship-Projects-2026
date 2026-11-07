package com.eswar.profile.ui

import android.content.Intent
import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.eswar.profile.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowLooper
import java.time.Duration

@RunWith(AndroidJUnit4::class)
class NavigationTest {

    @Test fun splashOpensProfileAfterDelay() {
        ActivityScenario.launch(SplashActivity::class.java).onActivity { splash ->
            ShadowLooper.idleMainLooper(SplashActivity.SPLASH_DELAY_MS + 50, java.util.concurrent.TimeUnit.MILLISECONDS)
            val next = shadowOf(splash).nextStartedActivity
            assertEquals(MainActivity::class.java.name, next.component?.className)
            assertTrue(splash.isFinishing)
        }
    }

    @Test fun profileCardsOpenSectionsWithTitleExtra() {
        ActivityScenario.launch(MainActivity::class.java).onActivity { main ->
            assertEquals("EM", main.findViewById<TextView>(R.id.avatar_initials).text.toString())
            val expected = mapOf(
                R.id.card_about to AboutActivity::class.java,
                R.id.card_skills to SkillsActivity::class.java,
                R.id.card_contact to ContactActivity::class.java,
            )
            expected.forEach { (cardId, target) ->
                main.findViewById<android.view.View>(cardId).performClick()
                val started: Intent = shadowOf(main).nextStartedActivity
                assertEquals(target.name, started.component?.className)
                assertNotNull(started.getStringExtra(BaseActivity.EXTRA_TITLE))
            }
        }
    }

    @Test fun sectionShowsTitlePassedThroughIntent() {
        val intent = Intent(ApplicationProvider.getApplicationContext(), SkillsActivity::class.java)
            .putExtra(BaseActivity.EXTRA_TITLE, "My Skills")
        ActivityScenario.launch<SkillsActivity>(intent).onActivity { activity ->
            assertEquals("My Skills", activity.supportActionBar?.title)
        }
    }

    @Test fun contactFormValidatesBeforeSending() {
        ActivityScenario.launch(ContactActivity::class.java).onActivity { activity ->
            assertFalse(activity.submitForm())
            assertNotNull(activity.findViewById<TextInputLayout>(R.id.layout_email).error)

            activity.findViewById<TextInputEditText>(R.id.input_name).setText("Recruiter")
            activity.findViewById<TextInputEditText>(R.id.input_email).setText("hr@example.com")
            activity.findViewById<TextInputEditText>(R.id.input_message).setText("Let's schedule a call this week.")
            assertTrue(activity.submitForm())
            val sent = shadowOf(activity).nextStartedActivity
            assertEquals(Intent.ACTION_SENDTO, sent.action)
        }
    }
}
