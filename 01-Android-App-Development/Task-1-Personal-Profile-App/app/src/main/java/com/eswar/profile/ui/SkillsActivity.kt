package com.eswar.profile.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.eswar.profile.R
import com.eswar.profile.data.ProfileParser
import com.eswar.profile.data.ProfileStats

class SkillsActivity : SectionActivity() {

    override val defaultTitle: String get() = getString(R.string.section_skills)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_skills)
        applySystemBarInsets(findViewById(R.id.skills_root))
        setUpToolbar(findViewById(R.id.toolbar))

        val skills = ProfileParser.parseSkills(resources.getStringArray(R.array.skills))
        val average = ProfileStats.averageLevel(skills)
        findViewById<TextView>(R.id.skills_summary).text =
            getString(R.string.skills_summary, skills.size, average, ProfileStats.levelLabel(average))

        val container = findViewById<LinearLayout>(R.id.skills_container)
        val inflater = LayoutInflater.from(this)
        ProfileStats.groupByCategory(skills).forEach { (category, list) ->
            val header = inflater.inflate(R.layout.item_skill_header, container, false) as TextView
            header.text = category.label
            container.addView(header)
            for (skill in list) {
                val row = inflater.inflate(R.layout.item_skill, container, false)
                row.findViewById<TextView>(R.id.skill_name).text = skill.name
                row.findViewById<TextView>(R.id.skill_level).text =
                    getString(R.string.skill_level_format, skill.level, ProfileStats.levelLabel(skill.level))
                row.findViewById<LinearProgressIndicator>(R.id.skill_progress).progress = skill.level
                container.addView(row)
            }
        }
    }
}
