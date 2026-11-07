package com.eswar.employeemanager.domain

import com.eswar.employeemanager.data.EmployeeEntity
import java.time.LocalDate
import java.time.format.DateTimeParseException

/** Raw text exactly as typed in the form. */
data class EmployeeForm(
    val id: Long = 0,
    val name: String = "",
    val designation: String = "",
    val departmentCode: String = "",
    val email: String = "",
    val phone: String = "",
    val salary: String = "",
    val joiningDate: String = "",
    val isFavorite: Boolean = false,
)

enum class Field { NAME, DESIGNATION, DEPARTMENT, EMAIL, PHONE, SALARY, JOINING_DATE }

sealed class ValidationResult {
    data class Valid(val entity: EmployeeEntity) : ValidationResult()
    data class Invalid(val errors: Map<Field, String>) : ValidationResult()
}

object EmployeeValidator {
    private val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    private val phoneRegex = Regex("^(\\+91[- ]?)?[6-9]\\d{9}$")

    fun validate(form: EmployeeForm, today: LocalDate = LocalDate.now()): ValidationResult {
        val errors = mutableMapOf<Field, String>()
        val name = form.name.trim().replace(Regex("\\s+"), " ")
        if (name.length < 3 || !name.all { it.isLetter() || it == ' ' || it == '.' }) errors[Field.NAME] = "Enter the full name (letters only)"
        if (form.designation.isBlank()) errors[Field.DESIGNATION] = "Designation is required"
        if (form.departmentCode.isBlank()) errors[Field.DEPARTMENT] = "Choose a department"
        if (!emailRegex.matches(form.email.trim())) errors[Field.EMAIL] = "Enter a valid email"
        val phone = form.phone.trim().replace(" ", "")
        if (!phoneRegex.matches(phone)) errors[Field.PHONE] = "Enter a 10-digit Indian mobile number"
        val salary = form.salary.replace(",", "").trim().toDoubleOrNull()
        if (salary == null || salary < 100_000 || salary > 50_000_000) errors[Field.SALARY] = "Annual salary between ₹1,00,000 and ₹5,00,00,000"
        val date = try { LocalDate.parse(form.joiningDate.trim()) } catch (e: DateTimeParseException) { null }
        if (date == null || date.isAfter(today) || date.isBefore(LocalDate.of(1990, 1, 1))) errors[Field.JOINING_DATE] = "Use yyyy-MM-dd, not in the future"

        if (errors.isNotEmpty()) return ValidationResult.Invalid(errors)
        return ValidationResult.Valid(
            EmployeeEntity(
                id = form.id, name = name, designation = form.designation.trim(), departmentCode = form.departmentCode,
                email = form.email.trim().lowercase(), phone = phone.removePrefix("+91").trim('-', ' '), salary = salary!!,
                joiningDate = date!!, isFavorite = form.isFavorite,
            )
        )
    }
}
