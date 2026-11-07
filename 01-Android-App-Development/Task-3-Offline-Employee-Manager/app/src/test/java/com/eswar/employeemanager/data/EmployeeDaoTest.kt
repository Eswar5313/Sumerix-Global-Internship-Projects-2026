package com.eswar.employeemanager.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

fun <T> LiveData<T>.await(): T {
    var value: T? = null
    val observer = androidx.lifecycle.Observer<T> { value = it }
    observeForever(observer)
    removeObserver(observer)
    @Suppress("UNCHECKED_CAST")
    return value as T
}

/** CRUD, search, sort, constraints and relations against a real (in-memory) SQLite database. */
@RunWith(AndroidJUnit4::class)
class EmployeeDaoTest {
    @get:Rule val instant = InstantTaskExecutorRule()
    private lateinit var db: AppDatabase
    private lateinit var repo: EmployeeRepository

    @Before fun setUp() = runBlocking {
        db = AppDatabase.inMemory(ApplicationProvider.getApplicationContext())
        db.departmentDao().insertAll(SeedData.departments)
        SeedData.employees.forEach { db.employeeDao().insert(it) }
        repo = EmployeeRepository(db.employeeDao(), db.departmentDao())
    }

    @After fun tearDown() = db.close()

    @Test fun insertReadUpdateDelete() = runBlocking {
        val id = repo.save(EmployeeEntity(name = "Test Person", designation = "Intern", departmentCode = "TECH",
            email = "test.person@example.com", phone = "9000000000", salary = 300000.0,
            joiningDate = LocalDate.of(2026, 10, 15))).getOrThrow()
        assertEquals("Intern", repo.get(id)!!.designation)
        repo.save(repo.get(id)!!.copy(designation = "Android Intern"))
        assertEquals("Android Intern", repo.get(id)!!.designation)
        repo.delete(repo.get(id)!!)
        assertNull(repo.get(id))
        assertEquals(12, repo.count.await())
    }

    @Test fun searchFiltersAndSorts() {
        assertEquals(listOf("Rohan Das"), repo.search("android", "", SortOrder.NAME).await().map { it.name })
        assertEquals(3, repo.search("", "OPS", SortOrder.NAME).await().size)
        val bySalary = repo.search("", "", SortOrder.SALARY).await()
        assertEquals("Ananya Iyer", bySalary.first().name)
        val byJoined = repo.search("", "", SortOrder.JOINED).await()
        assertEquals("Kavya Joshi", byJoined.first().name)
        val byName = repo.search("", "", SortOrder.NAME).await().map { it.name }
        assertEquals(byName.sortedBy { it.lowercase() }, byName)
    }

    @Test fun duplicateEmailIsRejectedNotCrashing() = runBlocking {
        val dup = SeedData.employees.first().copy(id = 0, name = "Someone Else")
        val result = repo.save(dup)
        assertTrue(result.exceptionOrNull() is DuplicateEmailException)
    }

    @Test fun relationAndAggregates() = runBlocking {
        val withEmployees = db.employeeDao().getDepartmentsWithEmployees()
        assertEquals(6, withEmployees.size)
        assertEquals(3, withEmployees.first { it.department.code == "OPS" }.employees.size)
        val stats = repo.departmentStats.await()
        assertEquals(12, stats.sumOf { it.headcount })
        val tech = stats.first { it.code == "TECH" }
        assertEquals((4_200_000.0 + 2_600_000.0 + 1_450_000.0) / 3, tech.avgSalary, 1.0)
        assertTrue(repo.exportCsv().lines().size == 13)
    }
}
