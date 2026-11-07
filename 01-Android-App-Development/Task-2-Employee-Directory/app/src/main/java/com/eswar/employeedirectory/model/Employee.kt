package com.eswar.employeedirectory.model

import java.time.LocalDate

/** A permanent employee. Data class => equals/hashCode/copy for free, used by DiffUtil. */
data class Employee(
    val employeeId: Int,
    val first: String,
    val last: String,
    val designation: String,
    val department: Department,
    val email: String,
    val phone: String,
    val location: String,
    val joinDate: LocalDate,
    val skills: List<String>,
    val isManager: Boolean = false,
    val isFavorite: Boolean = false,
) : Person(employeeId, first, last) {

    override fun roleSummary(): String = "$designation · ${department.displayName}"

    /** Polymorphic override: also searches designation, department, city and skills. */
    override fun matches(query: String): Boolean {
        if (super.matches(query)) return true
        val q = query.trim()
        return designation.contains(q, true) || department.displayName.contains(q, true) ||
            location.contains(q, true) || skills.any { it.contains(q, true) }
    }
}

/** Sealed hierarchy -> RecyclerView with multiple view types. */
sealed class DirectoryItem {
    abstract val stableId: Long

    data class Header(val department: Department, val count: Int) : DirectoryItem() {
        override val stableId: Long = -department.ordinal.toLong() - 1
    }

    data class Row(val employee: Employee) : DirectoryItem() {
        override val stableId: Long = employee.employeeId.toLong()
    }
}

data class CompanyStats(
    val totalEmployees: Int,
    val departments: Int,
    val managers: Int,
    val locations: Int,
    val averageTenureYears: Double,
    val headcountByDepartment: Map<Department, Int>,
)
