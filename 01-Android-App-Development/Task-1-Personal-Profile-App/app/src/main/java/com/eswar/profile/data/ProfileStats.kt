package com.eswar.profile.data

/** Small pure-Kotlin helpers (functions, loops, when, collections) - unit tested on the JVM. */
object ProfileStats {

    fun averageLevel(skills: List<Skill>): Int {
        if (skills.isEmpty()) return 0
        var total = 0
        for (skill in skills) total += skill.level
        return total / skills.size
    }

    fun levelLabel(level: Int): String = when {
        level >= 85 -> "Expert"
        level >= 70 -> "Advanced"
        level >= 50 -> "Intermediate"
        else -> "Learning"
    }

    fun groupByCategory(skills: List<Skill>): Map<SkillCategory, List<Skill>> =
        SkillCategory.entries.associateWith { category ->
            skills.filter { it.category == category }.sortedByDescending { it.level }
        }.filterValues { it.isNotEmpty() }

    fun initials(fullName: String): String =
        fullName.trim().split(Regex("\\s+"))
            .filter { it.isNotEmpty() }
            .take(2)
            .joinToString("") { it.first().uppercase() }

    fun isValidEmail(email: String?): Boolean =
        !email.isNullOrBlank() && Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$").matches(email.trim())
}
