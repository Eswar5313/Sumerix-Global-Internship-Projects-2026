package com.eswar.employeemanager.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.time.LocalDate

/** Parent table. */
@Entity(tableName = "departments")
data class DepartmentEntity(
    @PrimaryKey val code: String,
    val name: String,
    @ColumnInfo(name = "cost_centre") val costCentre: String,
)

/**
 * Child table. departmentCode is a FOREIGN KEY to departments.code (RESTRICT delete),
 * email is UNIQUE so the same person cannot be added twice.
 */
@Entity(
    tableName = "employees",
    foreignKeys = [ForeignKey(
        entity = DepartmentEntity::class,
        parentColumns = ["code"],
        childColumns = ["department_code"],
        onDelete = ForeignKey.RESTRICT,
    )],
    indices = [Index(value = ["email"], unique = true), Index(value = ["department_code"])],
)
data class EmployeeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val designation: String,
    @ColumnInfo(name = "department_code") val departmentCode: String,
    val email: String,
    val phone: String,
    val salary: Double,
    @ColumnInfo(name = "joining_date") val joiningDate: LocalDate,
    @ColumnInfo(name = "is_favorite") val isFavorite: Boolean = false,
)

/** One-to-many relationship read with @Relation (Room relationships introduction). */
data class DepartmentWithEmployees(
    @Embedded val department: DepartmentEntity,
    @Relation(parentColumn = "code", entityColumn = "department_code")
    val employees: List<EmployeeEntity>,
)

/** Aggregate row returned by a GROUP BY query. */
data class DepartmentStat(
    val code: String,
    val name: String,
    val headcount: Int,
    val avgSalary: Double,
    val totalSalary: Double,
)

enum class SortOrder { NAME, SALARY, JOINED }
