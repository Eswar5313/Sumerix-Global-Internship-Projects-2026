package com.eswar.employeemanager.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.eswar.employeemanager.data.AppDatabase
import com.eswar.employeemanager.data.EmployeeRepository
import com.eswar.employeemanager.data.SeedData
import com.eswar.employeemanager.data.SortOrder
import com.eswar.employeemanager.data.await
import com.eswar.employeemanager.domain.EmployeeForm
import com.eswar.employeemanager.domain.Field
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class EmployeeViewModelTest {
    @get:Rule val instant = InstantTaskExecutorRule()
    private lateinit var db: AppDatabase
    private lateinit var vm: EmployeeViewModel

    @Before fun setUp() = runBlocking {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        db = AppDatabase.inMemory(ApplicationProvider.getApplicationContext())
        db.departmentDao().insertAll(SeedData.departments)
        SeedData.employees.forEach { db.employeeDao().insert(it) }
        vm = EmployeeViewModel(EmployeeRepository(db.employeeDao(), db.departmentDao()))
    }

    @After fun tearDown() { db.close(); Dispatchers.resetMain() }

    @Test fun filterChangesRequeryRoom() {
        assertEquals(12, vm.employees.await().size)
        vm.setDepartment("FIN")
        assertEquals(2, vm.employees.await().size)
        vm.setDepartment(""); vm.setQuery("menon")
        assertEquals(listOf("Priya Menon"), vm.employees.await().map { it.name })
        vm.setQuery(""); vm.setSort(SortOrder.SALARY)
        assertEquals("Ananya Iyer", vm.employees.await().first().name)
    }

    @Test fun invalidFormExposesErrorsAndDoesNotSave() {
        assertFalse(vm.save(EmployeeForm(name = "X")))
        assertTrue(Field.EMAIL in vm.formErrors.await())
        assertEquals(12, vm.count.await())
    }

    @Test fun validFormIsInsertedThenDeletedAndRestored() {
        val ok = vm.save(EmployeeForm(name = "Asha Varghese", designation = "FP&A Analyst", departmentCode = "FIN",
            email = "asha@example.com", phone = "9876543210", salary = "1200000", joiningDate = "2025-02-03"))
        assertTrue(ok)
        Thread.sleep(300)  // Room suspend write runs on its own executor
        val list = vm.employees.await()
        assertEquals(13, list.size)
        val asha = list.first { it.name == "Asha Varghese" }
        vm.delete(asha); Thread.sleep(300)
        assertEquals(12, vm.employees.await().size)
        vm.undoDelete(asha); Thread.sleep(300)
        assertEquals(13, vm.employees.await().size)
    }
}
