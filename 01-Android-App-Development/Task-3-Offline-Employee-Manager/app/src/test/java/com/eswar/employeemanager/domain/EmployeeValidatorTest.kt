package com.eswar.employeemanager.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class EmployeeValidatorTest {
    private val today = LocalDate.of(2026, 11, 7)
    private val good = EmployeeForm(name = "  Asha   Varghese ", designation = "Analyst", departmentCode = "FIN",
        email = "Asha.V@Example.com", phone = "+91 98765 43210", salary = "12,00,000", joiningDate = "2024-06-03")

    @Test fun validFormIsNormalised() {
        val r = EmployeeValidator.validate(good, today) as ValidationResult.Valid
        assertEquals("Asha Varghese", r.entity.name)
        assertEquals("asha.v@example.com", r.entity.email)
        assertEquals("9876543210", r.entity.phone)
        assertEquals(1_200_000.0, r.entity.salary, 0.0)
    }

    @Test fun everyFieldIsChecked() {
        val r = EmployeeValidator.validate(EmployeeForm(name = "A1", salary = "50", joiningDate = "2027-01-01"), today)
                as ValidationResult.Invalid
        assertEquals(Field.entries.toSet(), r.errors.keys)
    }

    @Test fun futureJoiningDateRejected() {
        val r = EmployeeValidator.validate(good.copy(joiningDate = "2026-12-01"), today)
        assertTrue(r is ValidationResult.Invalid && Field.JOINING_DATE in r.errors)
    }
}
