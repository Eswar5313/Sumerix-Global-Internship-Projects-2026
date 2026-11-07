package com.eswar.employeedirectory.model

/** Anything that can be matched by the directory search box. */
interface Searchable {
    fun matches(query: String): Boolean
}

/**
 * Abstract base class - encapsulates the name fields shared by every person in the directory.
 * Subclasses must describe their role (abstraction + polymorphism).
 */
abstract class Person(
    val id: Int,
    val firstName: String,
    val lastName: String,
) : Searchable {

    val fullName: String get() = "$firstName $lastName"

    abstract fun roleSummary(): String

    override fun matches(query: String): Boolean =
        query.isBlank() || fullName.contains(query.trim(), ignoreCase = true)
}
