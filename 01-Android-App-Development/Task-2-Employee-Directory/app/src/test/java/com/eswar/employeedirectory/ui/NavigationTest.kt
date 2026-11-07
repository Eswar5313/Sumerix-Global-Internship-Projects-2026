package com.eswar.employeedirectory.ui

import android.os.Looper
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.eswar.employeedirectory.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows.shadowOf

@RunWith(AndroidJUnit4::class)
class NavigationTest {

    private fun idle() = shadowOf(Looper.getMainLooper()).idle()

    @Test fun startsOnDashboardAndBottomNavSwitchesFragments() {
        ActivityScenario.launch(MainActivity::class.java).onActivity { activity ->
            assertEquals(R.id.dashboardFragment, activity.navController.currentDestination?.id)
            val nav = activity.findViewById<BottomNavigationView>(R.id.bottom_nav)
            nav.selectedItemId = R.id.employeeListFragment
            idle()
            assertEquals(R.id.employeeListFragment, activity.navController.currentDestination?.id)
            nav.selectedItemId = R.id.departmentsFragment
            idle()
            assertEquals(R.id.departmentsFragment, activity.navController.currentDestination?.id)
        }
    }

    @Test fun tappingAnEmployeeOpensDetailWithSafeArgs() {
        ActivityScenario.launch(MainActivity::class.java).onActivity { activity ->
            activity.navController.navigate(R.id.employeeListFragment)
            idle()
            val recycler = activity.findViewById<RecyclerView>(R.id.recycler)
            recycler.measure(0, 0); recycler.layout(0, 0, 1080, 2000)
            assertTrue(recycler.adapter!!.itemCount > 20)
            recycler.findViewHolderForAdapterPosition(1)!!.itemView.performClick()   // position 0 = header
            idle()
            assertEquals(R.id.employeeDetailFragment, activity.navController.currentDestination?.id)
            assertEquals(101, activity.navController.currentBackStackEntry?.arguments?.getInt("employeeId"))
        }
    }

    @Test fun departmentArgumentFiltersTheList() {
        ActivityScenario.launch(MainActivity::class.java).onActivity { activity ->
            activity.navController.navigate(R.id.employeeListFragment, bundleOf("departmentName" to "Finance"))
            idle()
            val recycler = activity.findViewById<RecyclerView>(R.id.recycler)
            assertEquals(4, recycler.adapter!!.itemCount)   // 1 header + 3 finance employees
        }
    }
}
