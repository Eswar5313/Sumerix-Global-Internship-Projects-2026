package com.eswar.employeemanager

import android.app.Application
import com.eswar.employeemanager.data.AppDatabase
import com.eswar.employeemanager.data.EmployeeRepository

/** Simple manual dependency container (Hilt arrives in Task 4). */
class EmployeeManagerApp : Application() {
    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }
    val repository: EmployeeRepository by lazy { EmployeeRepository(database.employeeDao(), database.departmentDao()) }
}
