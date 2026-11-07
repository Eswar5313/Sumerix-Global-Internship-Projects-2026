package com.eswar.profile

import com.eswar.profile.data.ProfileParser
import com.eswar.profile.data.ProfileStats
import com.eswar.profile.data.Skill
import com.eswar.profile.data.SkillCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Plain JVM tests for the Kotlin fundamentals used by the app. */
class ProfileLogicTest {

    @Test fun parsesSkillsAndSkipsMalformedRows() {
        val skills = ProfileParser.parseSkills(arrayOf("SQL|75|ANALYTICS", "broken row", "Kotlin|x|TECH", "Lead|120|PEOPLE"))
        assertEquals(2, skills.size)
        assertEquals(Skill("SQL", 75, SkillCategory.ANALYTICS), skills[0])
        assertEquals(100, skills[1].level)               // clamped into 0..100
    }

    @Test fun parsesTimelineWithOptionalDetail() {
        val rows = ProfileParser.parseTimeline(arrayOf("2016|MBA|SRM University|First Class", "2013|B.Com|Madras"))
        assertEquals("First Class", rows[0].detail)
        assertNull(rows[1].detail)
    }

    @Test fun averageAndLabels() {
        val skills = listOf(Skill("A", 90, SkillCategory.TECH), Skill("B", 60, SkillCategory.TECH))
        assertEquals(75, ProfileStats.averageLevel(skills))
        assertEquals(0, ProfileStats.averageLevel(emptyList()))
        assertEquals("Expert", ProfileStats.levelLabel(90))
        assertEquals("Advanced", ProfileStats.levelLabel(75))
        assertEquals("Intermediate", ProfileStats.levelLabel(55))
        assertEquals("Learning", ProfileStats.levelLabel(40))
    }

    @Test fun groupsByCategoryInEnumOrder() {
        val grouped = ProfileStats.groupByCategory(listOf(
            Skill("Lead", 85, SkillCategory.PEOPLE), Skill("Ops", 80, SkillCategory.OPERATIONS)))
        assertEquals(listOf(SkillCategory.OPERATIONS, SkillCategory.PEOPLE), grouped.keys.toList())
    }

    @Test fun initialsAndEmailValidation() {
        assertEquals("EM", ProfileStats.initials("  Eswar   Mahalingam "))
        assertTrue(ProfileStats.isValidEmail("someone@example.com"))
        assertFalse(ProfileStats.isValidEmail("not-an-email"))
        assertFalse(ProfileStats.isValidEmail(null))
        assertEquals(SkillCategory.TECH, SkillCategory.fromKey(null))
    }
}
