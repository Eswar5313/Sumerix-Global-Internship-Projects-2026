package com.eswar.employeedirectory.ui

import android.os.Bundle
import android.os.Looper
import androidx.core.os.bundleOf
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.captureRoboImage
import com.eswar.employeedirectory.R
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class ScreenshotTest {

    private fun shot(name: String, destination: Int? = null, args: Bundle? = null) {
        ActivityScenario.launch(MainActivity::class.java).onActivity { activity ->
            destination?.let { activity.navController.navigate(it, args) }
            shadowOf(Looper.getMainLooper()).idle()
            activity.window.decorView.captureRoboImage("build/outputs/roborazzi/$name.png")
        }
    }

    @Test @Config(qualifiers = "w411dp-h1100dp-xxhdpi") fun s01_dashboard() = shot("01_dashboard")
    @Test @Config(qualifiers = "w411dp-h891dp-xxhdpi") fun s02_employees() = shot("02_employee_list", R.id.employeeListFragment)
    @Test @Config(qualifiers = "w411dp-h891dp-xxhdpi") fun s03_filtered() =
        shot("03_filtered_technology", R.id.employeeListFragment, bundleOf("departmentName" to "Technology"))
    @Test @Config(qualifiers = "w411dp-h891dp-xxhdpi") fun s04_detail() =
        shot("04_employee_detail", R.id.employeeDetailFragment, bundleOf("employeeId" to 202))
    @Test @Config(qualifiers = "w411dp-h891dp-xxhdpi") fun s05_departments() = shot("05_departments", R.id.departmentsFragment)
    @Test @Config(qualifiers = "w411dp-h891dp-xxhdpi") fun s06_company() = shot("06_company", R.id.companyFragment)
    @Test @Config(qualifiers = "w411dp-h891dp-xxhdpi") fun s07_contact() = shot("07_contact", R.id.contactFragment)
    @Test @Config(qualifiers = "w411dp-h891dp-night-xxhdpi") fun s08_dark() = shot("08_employee_list_dark", R.id.employeeListFragment)
    @Test @Config(qualifiers = "w1280dp-h800dp-xhdpi") fun s09_tablet() = shot("09_tablet_two_column_list", R.id.employeeListFragment)
}
