package com.eswar.profile.data

/** A single skill with a self-assessed proficiency (0-100). */
data class Skill(val name: String, val level: Int, val category: SkillCategory) {
    init {
        require(level in 0..100) { "Skill level must be between 0 and 100, was $level" }
    }
}

enum class SkillCategory(val label: String) {
    OPERATIONS("Operations & Supply Chain"),
    ANALYTICS("Data & Business Analytics"),
    TECH("Tech & Design"),
    PEOPLE("People & Leadership");

    companion object {
        /** Null-safe lookup: unknown keys fall back to TECH instead of crashing. */
        fun fromKey(key: String?): SkillCategory =
            entries.firstOrNull { it.name.equals(key?.trim(), ignoreCase = true) } ?: TECH
    }
}

/** One row of the education / experience timeline. [detail] is optional. */
data class TimelineItem(
    val period: String,
    val title: String,
    val organisation: String,
    val detail: String? = null,
)
