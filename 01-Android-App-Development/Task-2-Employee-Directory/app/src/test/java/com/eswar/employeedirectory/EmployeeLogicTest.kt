package com.eswar.employeedirectory

import com.eswar.employeedirectory.data.EmployeeRepository
import com.eswar.employeedirectory.model.Department
import com.eswar.employeedirectory.model.DirectoryItem
import com.eswar.employeedirectory.model.initials
import com.eswar.employeedirectory.model.yearsOfService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class EmployeeLogicTest {

    @Test fun repositoryHasTwentyFourEmployeesInSixDepartments() {
        val stats = EmployeeRepository.stats()
        assertEquals(24, stats.totalEmployees)
        assertEquals(6, stats.departments)
        assertEquals(7, stats.managers)
        assertEquals(24, stats.headcountByDepartment.values.sum())
    }

    @Test fun searchMatchesNameRoleCityAndSkill() {
        assertEquals(listOf("Rohan Das"), EmployeeRepository.search("rohan", null).map { it.fullName })
        assertTrue(EmployeeRepository.search("android", null).any { it.fullName == "Rohan Das" })
        assertTrue(EmployeeRepository.search("coimbatore", null).size >= 3)
        assertTrue(EmployeeRepository.search("power bi", null).isNotEmpty())
    }

    @Test fun departmentFilterAndSectionHeaders() {
        val items = EmployeeRepository.directoryItems("", Department.FINANCE)
        assertEquals(1, items.count { it is DirectoryItem.Header })
        assertEquals(3, items.count { it is DirectoryItem.Row })
        val all = EmployeeRepository.directoryItems()
        assertEquals(6, all.count { it is DirectoryItem.Header })
        // managers are listed first inside each department
        val firstRow = all[1] as DirectoryItem.Row
        assertTrue(firstRow.employee.isManager)
    }

    @Test fun extensionsAndTenure() {
        assertEquals("AI", "Ananya Iyer".initials())
        val e = EmployeeRepository.byId(101)!!
        assertEquals(10.6, e.yearsOfService(LocalDate.of(2026, 11, 7)), 0.1)
        assertEquals(Department.PEOPLE, Department.fromDisplayName("people & hr"))
    }

    @Test fun favouriteToggleIsReversible() {
        val before = EmployeeRepository.byId(301)!!.isFavorite
        EmployeeRepository.toggleFavourite(301)
        assertEquals(!before, EmployeeRepository.byId(301)!!.isFavorite)
        EmployeeRepository.toggleFavourite(301)
        assertEquals(before, EmployeeRepository.byId(301)!!.isFavorite)
    }
}
