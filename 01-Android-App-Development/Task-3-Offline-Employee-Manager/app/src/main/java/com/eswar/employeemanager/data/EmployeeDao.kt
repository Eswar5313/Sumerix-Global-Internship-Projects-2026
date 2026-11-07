package com.eswar.employeemanager.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface EmployeeDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(employee: EmployeeEntity): Long

    @Update
    suspend fun update(employee: EmployeeEntity): Int

    @Delete
    suspend fun delete(employee: EmployeeEntity): Int

    @Query("SELECT * FROM employees WHERE id = :id")
    suspend fun getById(id: Long): EmployeeEntity?

    @Query("SELECT * FROM employees WHERE id = :id")
    fun observeById(id: Long): LiveData<EmployeeEntity?>

    /** Search + filter + sort in one query; the ViewModel re-runs it whenever the filter changes. */
    @Query(
        """
        SELECT * FROM employees
        WHERE (:query = '' OR name LIKE '%' || :query || '%' OR designation LIKE '%' || :query || '%'
               OR email LIKE '%' || :query || '%')
          AND (:departmentCode = '' OR department_code = :departmentCode)
        ORDER BY
          CASE WHEN :sort = 'NAME' THEN name END COLLATE NOCASE ASC,
          CASE WHEN :sort = 'SALARY' THEN salary END DESC,
          CASE WHEN :sort = 'JOINED' THEN joining_date END DESC
        """
    )
    fun search(query: String, departmentCode: String, sort: String): LiveData<List<EmployeeEntity>>

    @Query("SELECT * FROM employees ORDER BY name COLLATE NOCASE")
    suspend fun getAll(): List<EmployeeEntity>

    @Query("SELECT COUNT(*) FROM employees")
    fun observeCount(): LiveData<Int>

    @Query(
        """
        SELECT d.code AS code, d.name AS name, COUNT(e.id) AS headcount,
               COALESCE(AVG(e.salary), 0) AS avgSalary, COALESCE(SUM(e.salary), 0) AS totalSalary
        FROM departments d LEFT JOIN employees e ON e.department_code = d.code
        GROUP BY d.code ORDER BY headcount DESC, d.name
        """
    )
    fun observeDepartmentStats(): LiveData<List<DepartmentStat>>

    @Transaction
    @Query("SELECT * FROM departments ORDER BY name")
    suspend fun getDepartmentsWithEmployees(): List<DepartmentWithEmployees>
}

@Dao
interface DepartmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(departments: List<DepartmentEntity>)

    @Query("SELECT * FROM departments ORDER BY name")
    fun observeAll(): LiveData<List<DepartmentEntity>>

    @Query("SELECT * FROM departments ORDER BY name")
    suspend fun getAll(): List<DepartmentEntity>
}
