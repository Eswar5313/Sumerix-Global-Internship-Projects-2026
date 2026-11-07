package com.eswar.employeedirectory.model

enum class Department(val displayName: String, val head: String) {
    OPERATIONS("Operations", "Karthik Raman"),
    TECHNOLOGY("Technology", "Ananya Iyer"),
    FINANCE("Finance", "Rahul Gupta"),
    PEOPLE("People & HR", "Meera Nair"),
    SALES("Sales", "Vikram Shetty"),
    CUSTOMER_SUCCESS("Customer Success", "Priya Menon");

    companion object {
        fun fromDisplayName(name: String?): Department? =
            entries.firstOrNull { it.displayName.equals(name, ignoreCase = true) || it.name == name }
    }
}
