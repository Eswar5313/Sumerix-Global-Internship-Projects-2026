package com.eswar.employeedirectory.model

import java.time.LocalDate
import java.time.temporal.ChronoUnit

/** Extension functions keep helpers next to the type they describe. */
fun String.initials(): String = trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
    .take(2).joinToString("") { it.first().uppercase() }

fun Employee.yearsOfService(today: LocalDate = LocalDate.of(2026, 11, 7)): Double =
    ChronoUnit.DAYS.between(joinDate, today).coerceAtLeast(0) / 365.25

/** Higher-order function: filter with any predicate passed as a lambda. */
inline fun List<Employee>.filterBy(predicate: (Employee) -> Boolean): List<Employee> = filter(predicate)
