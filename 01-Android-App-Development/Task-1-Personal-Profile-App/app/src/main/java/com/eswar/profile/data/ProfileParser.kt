package com.eswar.profile.data

/**
 * Converts the pipe-separated string-array resources into Kotlin data classes.
 * Keeping the text in res/values/arrays.xml means no UI copy is hard-coded in Kotlin.
 */
object ProfileParser {

    /** "Excel & Power BI|85|ANALYTICS"  ->  Skill */
    fun parseSkills(rows: Array<String>): List<Skill> = rows.mapNotNull { row ->
        val parts = row.split("|").map { it.trim() }
        if (parts.size < 3) return@mapNotNull null          // skip malformed rows safely
        val level = parts[1].toIntOrNull() ?: return@mapNotNull null
        Skill(parts[0], level.coerceIn(0, 100), SkillCategory.fromKey(parts[2]))
    }

    /** "2014 - 2016|MBA|SRM University|First Class"  ->  TimelineItem */
    fun parseTimeline(rows: Array<String>): List<TimelineItem> = rows.mapNotNull { row ->
        val parts = row.split("|").map { it.trim() }
        when (parts.size) {
            3 -> TimelineItem(parts[0], parts[1], parts[2])
            4 -> TimelineItem(parts[0], parts[1], parts[2], parts[3].ifBlank { null })
            else -> null
        }
    }
}
