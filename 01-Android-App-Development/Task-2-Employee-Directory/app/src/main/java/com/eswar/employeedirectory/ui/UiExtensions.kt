package com.eswar.employeedirectory.ui

import android.content.res.ColorStateList
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.eswar.employeedirectory.R
import com.eswar.employeedirectory.model.Department
import com.eswar.employeedirectory.model.initials

@ColorRes
fun Department.colorRes(): Int = when (this) {
    Department.OPERATIONS -> R.color.dept_operations
    Department.TECHNOLOGY -> R.color.dept_technology
    Department.FINANCE -> R.color.dept_finance
    Department.PEOPLE -> R.color.dept_hr
    Department.SALES -> R.color.dept_sales
    Department.CUSTOMER_SUCCESS -> R.color.dept_customer
}

/** Turns a TextView with a circular background into a coloured initials avatar. */
fun TextView.showAvatar(name: String, department: Department) {
    text = name.initials()
    backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, department.colorRes()))
}
