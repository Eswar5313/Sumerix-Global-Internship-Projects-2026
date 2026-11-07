package com.eswar.profile.ui

import android.app.Activity
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Renders every screen with Robolectric native graphics and saves PNGs to
 * build/outputs/roborazzi (copied into docs/screenshots for the README).
 */
@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class ScreenshotTest {

    private fun <A : Activity> shot(cls: Class<A>, name: String) {
        ActivityScenario.launch(cls).onActivity { it.window.decorView.captureRoboImage("build/outputs/roborazzi/$name.png") }
    }

    @Test @Config(qualifiers = "w411dp-h891dp-xxhdpi") fun phone_01_splash() = shot(SplashActivity::class.java, "01_splash")
    @Test @Config(qualifiers = "w411dp-h891dp-xxhdpi") fun phone_02_profile() = shot(MainActivity::class.java, "02_profile")
    @Test @Config(qualifiers = "w411dp-h1600dp-xxhdpi") fun phone_03_about() = shot(AboutActivity::class.java, "03_about")
    @Test @Config(qualifiers = "w411dp-h1500dp-xxhdpi") fun phone_04_skills() = shot(SkillsActivity::class.java, "04_skills")
    @Test @Config(qualifiers = "w411dp-h891dp-xxhdpi") fun phone_05_contact() = shot(ContactActivity::class.java, "05_contact")
    @Test @Config(qualifiers = "w411dp-h891dp-night-xxhdpi") fun phone_06_profile_dark() = shot(MainActivity::class.java, "06_profile_dark")
    @Test @Config(qualifiers = "w1280dp-h800dp-xhdpi") fun tablet_07_profile() = shot(MainActivity::class.java, "07_profile_tablet")
}
