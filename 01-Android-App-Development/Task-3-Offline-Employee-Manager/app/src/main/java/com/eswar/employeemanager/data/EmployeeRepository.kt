package com.eswar.employeemanager.data

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.LiveData

/**
 * Single source of truth for the UI. Fragments/ViewModels never touch the DAO directly,
 * so a remote API can be added later (Task 4) without changing the screens.
 */
class EmployeeRepository(
    private val employeeDao: EmployeeDao,
    private val departmentDao: DepartmentDao,
) {
    val departments: LiveData<List<DepartmentEntity>> = departmentDao.observeAll()
    val count: LiveData<Int> = employeeDao.observeCount()
    val departmentStats: LiveData<List<DepartmentStat>> = employeeDao.observeDepartmentStats()

    fun search(query: String, departmentCode: String, sort: SortOrder): LiveData<List<EmployeeEntity>> =
        employeeDao.search(query.trim(), departmentCode, sort.name)

    fun observe(id: Long): LiveData<EmployeeEntity?> = employeeDao.observeById(id)

    suspend fun get(id: Long): EmployeeEntity? = employeeDao.getById(id)

    /** Insert or update. Returns a failure (instead of crashing) when the email already exists. */
    suspend fun save(employee: EmployeeEntity): Result<Long> = try {
        if (employee.id == 0L) Result.success(employeeDao.insert(employee))
        else { employeeDao.update(employee); Result.success(employee.id) }
    } catch (e: SQLiteConstraintException) {
        Result.failure(DuplicateEmailException(employee.email))
    }

    suspend fun delete(employee: EmployeeEntity) = employeeDao.delete(employee)

    suspend fun restore(employee: EmployeeEntity) = employeeDao.insert(employee)

    suspend fun toggleFavourite(employee: EmployeeEntity) = employeeDao.update(employee.copy(isFavorite = !employee.isFavorite))

    suspend fun exportCsv(): String {
        val names = departmentDao.getAll().associate { it.code to it.name }
        val header = "id,name,designation,department,email,phone,salary,joining_date"
        return (listOf(header) + employeeDao.getAll().map {
            listOf(it.id, it.name, it.designation, names[it.departmentCode] ?: it.departmentCode, it.email, it.phone,
                it.salary.toLong(), it.joiningDate).joinToString(",") { v -> "\"$v\"" }
        }).joinToString("\n")
    }
}

class DuplicateEmailException(email: String) : Exception("An employee with $email already exists")
