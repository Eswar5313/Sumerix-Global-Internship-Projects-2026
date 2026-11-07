package com.eswar.profile.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.eswar.profile.R
import com.eswar.profile.data.ProfileParser
import com.eswar.profile.data.TimelineItem

class AboutActivity : SectionActivity() {

    override val defaultTitle: String get() = getString(R.string.section_about)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        applySystemBarInsets(findViewById(R.id.about_root))
        setUpToolbar(findViewById(R.id.toolbar))

        val experience = ProfileParser.parseTimeline(resources.getStringArray(R.array.experience_timeline))
        val education = ProfileParser.parseTimeline(resources.getStringArray(R.array.education_timeline))
        renderTimeline(findViewById(R.id.experience_container), experience)
        renderTimeline(findViewById(R.id.education_container), education)
    }

    private fun renderTimeline(container: LinearLayout, items: List<TimelineItem>) {
        val inflater = LayoutInflater.from(this)
        items.forEachIndexed { index, item ->
            val row = inflater.inflate(R.layout.item_timeline, container, false)
            row.findViewById<TextView>(R.id.timeline_period).text = item.period
            row.findViewById<TextView>(R.id.timeline_title).text = item.title
            row.findViewById<TextView>(R.id.timeline_org).text = item.organisation
            row.findViewById<TextView>(R.id.timeline_detail).apply {
                text = item.detail.orEmpty()
                visibility = if (item.detail == null) android.view.View.GONE else android.view.View.VISIBLE
            }
            row.findViewById<android.view.View>(R.id.timeline_line).visibility =
                if (index == items.lastIndex) android.view.View.INVISIBLE else android.view.View.VISIBLE
            container.addView(row)
        }
    }
}
