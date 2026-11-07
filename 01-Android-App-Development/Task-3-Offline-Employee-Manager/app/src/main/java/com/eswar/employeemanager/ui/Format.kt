package com.eswar.employeemanager.ui

import android.content.res.ColorStateList
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.eswar.employeemanager.R
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val inr: NumberFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN")).apply { maximumFractionDigits = 0 }

fun Double.asRupees(): String = inr.format(this)

fun LocalDate.pretty(): String = format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.US))

fun String.initials(): String = trim().split(Regex("\\s+")).filter { it.isNotEmpty() }.take(2)
    .joinToString("") { it.first().uppercase() }

@ColorRes
fun departmentColor(code: String): Int = when (code) {
    "OPS" -> R.color.dept_operations
    "TECH" -> R.color.dept_technology
    "FIN" -> R.color.dept_finance
    "HR" -> R.color.dept_hr
    "SALES" -> R.color.dept_sales
    else -> R.color.dept_customer
}

fun TextView.showAvatar(name: String, departmentCode: String) {
    text = name.initials()
    backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, departmentColor(departmentCode)))
}
