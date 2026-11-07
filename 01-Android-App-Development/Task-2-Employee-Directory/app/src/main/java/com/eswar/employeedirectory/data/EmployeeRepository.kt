package com.eswar.employeedirectory.data

import com.eswar.employeedirectory.model.CompanyStats
import com.eswar.employeedirectory.model.Department
import com.eswar.employeedirectory.model.Department.*
import com.eswar.employeedirectory.model.DirectoryItem
import com.eswar.employeedirectory.model.Employee
import com.eswar.employeedirectory.model.filterBy
import com.eswar.employeedirectory.model.yearsOfService
import java.time.LocalDate

/**
 * In-memory dummy data for a FICTIONAL company (NorthStar Logistics). Business rules live here,
 * not in Fragments, so they can be unit-tested and later swapped for Room (Task 3).
 */
object EmployeeRepository {

    private fun emp(
        id: Int, first: String, last: String, title: String, dept: Department, city: String,
        joined: String, skills: String, manager: Boolean = false,
    ) = Employee(
        employeeId = id, first = first, last = last, designation = title, department = dept,
        email = "${first.lowercase()}.${last.lowercase()}@northstar.example",
        phone = "+91 98400 ${(10000 + id * 137).toString().takeLast(5)}",
        location = city, joinDate = LocalDate.parse(joined),
        skills = skills.split(",").map { it.trim() }, isManager = manager,
    )

    private val seed: List<Employee> = listOf(
        emp(101, "Karthik", "Raman", "Head of Operations", OPERATIONS, "Coimbatore", "2016-04-11", "Hub operations, Lean, P&L", true),
        emp(102, "Sanjay", "Pillai", "Hub Manager", OPERATIONS, "Chennai", "2019-07-01", "Linehaul, Dock scheduling, Safety", true),
        emp(103, "Divya", "Krishnan", "Shift Team Lead", OPERATIONS, "Coimbatore", "2023-02-15", "Sortation, Rostering, Excel"),
        emp(104, "Imran", "Khan", "Inventory Analyst", OPERATIONS, "Delhi", "2024-09-02", "SQL, Cycle counts, Power BI"),
        emp(105, "Lakshmi", "Narayanan", "Process Excellence Lead", OPERATIONS, "Bengaluru", "2021-11-22", "Six Sigma, Kaizen, Minitab"),
        emp(201, "Ananya", "Iyer", "Engineering Manager", TECHNOLOGY, "Bengaluru", "2018-01-08", "Kotlin, Architecture, Hiring", true),
        emp(202, "Rohan", "Das", "Senior Android Developer", TECHNOLOGY, "Bengaluru", "2020-06-15", "Kotlin, Jetpack, Room"),
        emp(203, "Sneha", "Reddy", "Backend Engineer", TECHNOLOGY, "Hyderabad", "2022-03-21", "Java, Spring Boot, PostgreSQL"),
        emp(204, "Arjun", "Mehta", "Data Engineer", TECHNOLOGY, "Pune", "2023-08-01", "Python, Airflow, BigQuery"),
        emp(205, "Kavya", "Joshi", "UI/UX Designer", TECHNOLOGY, "Mumbai", "2025-01-06", "Figma, Research, Design systems"),
        emp(301, "Rahul", "Gupta", "Finance Controller", FINANCE, "Delhi", "2017-05-02", "IFRS, Budgeting, SAP FICO", true),
        emp(302, "Pooja", "Verma", "FP&A Analyst", FINANCE, "Delhi", "2022-10-10", "Forecasting, Excel, Power BI"),
        emp(303, "Joseph", "Fernandes", "Accounts Executive", FINANCE, "Mumbai", "2024-04-15", "Tally, GST, Reconciliation"),
        emp(401, "Meera", "Nair", "HR Business Partner", PEOPLE, "Chennai", "2019-02-18", "Talent, Engagement, POSH", true),
        emp(402, "Gurpreet", "Singh", "Talent Acquisition Lead", PEOPLE, "Delhi", "2021-07-12", "Hiring, Employer brand, ATS"),
        emp(403, "Harini", "Sundaram", "L&D Specialist", PEOPLE, "Coimbatore", "2025-06-02", "Training, LMS, Facilitation"),
        emp(501, "Vikram", "Shetty", "Head of Sales", SALES, "Mumbai", "2016-09-19", "Enterprise sales, Negotiation", true),
        emp(502, "Aditya", "Bose", "Key Account Manager", SALES, "Kolkata", "2020-12-01", "Account planning, CRM"),
        emp(503, "Nisha", "Patel", "Sales Analyst", SALES, "Ahmedabad", "2023-05-08", "Salesforce, Excel, Pipeline"),
        emp(504, "Farhan", "Qureshi", "Business Development Executive", SALES, "Delhi", "2025-03-17", "Prospecting, Demos"),
        emp(601, "Priya", "Menon", "Customer Success Manager", CUSTOMER_SUCCESS, "Bengaluru", "2018-08-27", "CSAT, Escalations, QBRs", true),
        emp(602, "Deepak", "Rao", "Support Team Lead", CUSTOMER_SUCCESS, "Chennai", "2021-04-05", "FCR, Coaching, Zendesk"),
        emp(603, "Riya", "Chatterjee", "Customer Experience Analyst", CUSTOMER_SUCCESS, "Kolkata", "2024-01-22", "Surveys, NPS, SQL"),
        emp(604, "Sai", "Prasad", "Onboarding Specialist", CUSTOMER_SUCCESS, "Hyderabad", "2026-08-03", "Training, Documentation"),
    )

    private val favourites = mutableSetOf(102, 202)

    val employees: List<Employee>
        get() = seed.map { it.copy(isFavorite = it.employeeId in favourites) }

    fun byId(id: Int): Employee? = employees.firstOrNull { it.employeeId == id }

    fun toggleFavourite(id: Int): Boolean {
        val nowFavourite = if (id in favourites) { favourites.remove(id); false } else { favourites.add(id); true }
        return nowFavourite
    }

    fun newJoiners(count: Int = 3): List<Employee> = employees.sortedByDescending { it.joinDate }.take(count)

    fun search(query: String, department: Department?): List<Employee> =
        employees.filterBy { (department == null || it.department == department) && it.matches(query) }
            .sortedWith(compareBy({ it.department.ordinal }, { !it.isManager }, { it.fullName }))

    /** Builds the sectioned list (headers + rows) for the RecyclerView. */
    fun directoryItems(query: String = "", department: Department? = null): List<DirectoryItem> =
        search(query, department).groupBy { it.department }.flatMap { (dept, people) ->
            listOf(DirectoryItem.Header(dept, people.size)) + people.map { DirectoryItem.Row(it) }
        }

    fun stats(today: java.time.LocalDate = java.time.LocalDate.of(2026, 11, 7)): CompanyStats {
        val all = employees
        return CompanyStats(
            totalEmployees = all.size,
            departments = all.map { it.department }.distinct().size,
            managers = all.count { it.isManager },
            locations = all.map { it.location }.toSet().size,
            averageTenureYears = all.map { it.yearsOfService(today) }.average(),
            headcountByDepartment = Department.entries.associateWith { d -> all.count { it.department == d } },
        )
    }
}
