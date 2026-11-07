package com.eswar.employeemanager.data

import java.time.LocalDate

/** Sample records for a FICTIONAL company so the app is useful on first launch. */
object SeedData {
    val departments = listOf(
        DepartmentEntity("OPS", "Operations", "CC-100"),
        DepartmentEntity("TECH", "Technology", "CC-200"),
        DepartmentEntity("FIN", "Finance", "CC-300"),
        DepartmentEntity("HR", "People & HR", "CC-400"),
        DepartmentEntity("SALES", "Sales", "CC-500"),
        DepartmentEntity("CS", "Customer Success", "CC-600"),
    )

    private fun e(name: String, title: String, dept: String, salary: Double, joined: String, fav: Boolean = false) = EmployeeEntity(
        name = name, designation = title, departmentCode = dept,
        email = name.lowercase().replace(" ", ".") + "@northstar.example",
        phone = "98400" + (10000 + name.length * 977).toString().takeLast(5),
        salary = salary, joiningDate = LocalDate.parse(joined), isFavorite = fav,
    )

    val employees = listOf(
        e("Karthik Raman", "Head of Operations", "OPS", 3_600_000.0, "2016-04-11", true),
        e("Divya Krishnan", "Shift Team Lead", "OPS", 950_000.0, "2023-02-15"),
        e("Imran Khan", "Inventory Analyst", "OPS", 780_000.0, "2024-09-02"),
        e("Ananya Iyer", "Engineering Manager", "TECH", 4_200_000.0, "2018-01-08"),
        e("Rohan Das", "Senior Android Developer", "TECH", 2_600_000.0, "2020-06-15", true),
        e("Kavya Joshi", "UI/UX Designer", "TECH", 1_450_000.0, "2025-01-06"),
        e("Rahul Gupta", "Finance Controller", "FIN", 3_100_000.0, "2017-05-02"),
        e("Pooja Verma", "FP&A Analyst", "FIN", 1_150_000.0, "2022-10-10"),
        e("Meera Nair", "HR Business Partner", "HR", 1_900_000.0, "2019-02-18"),
        e("Vikram Shetty", "Head of Sales", "SALES", 3_400_000.0, "2016-09-19"),
        e("Nisha Patel", "Sales Analyst", "SALES", 860_000.0, "2023-05-08"),
        e("Priya Menon", "Customer Success Manager", "CS", 2_050_000.0, "2018-08-27"),
    )
}
