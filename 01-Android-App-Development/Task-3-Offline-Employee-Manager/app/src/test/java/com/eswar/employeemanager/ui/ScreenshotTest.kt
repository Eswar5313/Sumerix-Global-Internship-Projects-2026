package com.eswar.employeemanager.ui

import android.os.Bundle
import android.os.Looper
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.captureRoboImage
import com.eswar.employeemanager.R
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Uses the real seeded database file (Room callback) so screens show the first-launch state. */
@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class ScreenshotTest {

    @Before fun freshDatabase() {
        com.eswar.employeemanager.data.AppDatabase.resetInstance()
        ApplicationProvider.getApplicationContext<android.content.Context>().deleteDatabase("employee_manager.db")
    }

    private fun settle(activity: MainActivity, until: () -> Boolean = { true }) {
        repeat(60) {
            shadowOf(Looper.getMainLooper()).idle()
            if (until()) return
            Thread.sleep(50)
        }
    }

    private fun shot(name: String, destination: Int? = null, args: Bundle? = null, action: (MainActivity) -> Unit = {}) {
        ActivityScenario.launch(MainActivity::class.java).onActivity { activity ->
            settle(activity) { (activity.findViewById<RecyclerView>(R.id.recycler)?.adapter?.itemCount ?: 0) > 0 }
            destination?.let { activity.navController.navigate(it, args); settle(activity) }
            Thread.sleep(200); settle(activity)
            action(activity); settle(activity)
            activity.window.decorView.captureRoboImage("build/outputs/roborazzi/$name.png")
        }
    }

    @Test @Config(qualifiers = "w411dp-h1000dp-xxhdpi") fun s01_list() = shot("01_employee_list")
    @Test @Config(qualifiers = "w411dp-h891dp-xxhdpi") fun s02_detail() =
        shot("02_employee_detail", R.id.employeeDetailFragment, bundleOf("employeeId" to 5L))
    @Test @Config(qualifiers = "w411dp-h891dp-xxhdpi") fun s03_add_validation() =
        shot("03_add_employee_validation", R.id.addEditEmployeeFragment, bundleOf("employeeId" to 0L)) { activity ->
            val host = activity.supportFragmentManager.findFragmentById(R.id.nav_host_fragment)!!
            (host.childFragmentManager.primaryNavigationFragment as com.eswar.employeemanager.ui.edit.AddEditEmployeeFragment).save()
        }
    @Test @Config(qualifiers = "w411dp-h891dp-xxhdpi") fun s04_edit() =
        shot("04_edit_employee", R.id.addEditEmployeeFragment, bundleOf("employeeId" to 2L))
    @Test @Config(qualifiers = "w411dp-h891dp-xxhdpi") fun s05_stats() = shot("05_statistics", R.id.statsFragment)
    @Test @Config(qualifiers = "w411dp-h891dp-night-xxhdpi") fun s06_dark() = shot("06_employee_list_dark")
    @Test @Config(qualifiers = "w1280dp-h800dp-xhdpi") fun s07_tablet() = shot("07_tablet_two_column")
}
