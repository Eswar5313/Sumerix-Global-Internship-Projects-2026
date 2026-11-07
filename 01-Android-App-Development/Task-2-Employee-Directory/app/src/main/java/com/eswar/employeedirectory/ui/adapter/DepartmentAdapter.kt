package com.eswar.employeedirectory.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.eswar.employeedirectory.R
import com.eswar.employeedirectory.databinding.ItemDepartmentBinding
import com.eswar.employeedirectory.model.Department
import com.eswar.employeedirectory.ui.colorRes

class DepartmentAdapter(
    private val items: List<Pair<Department, Int>>,
    private val onClick: (Department) -> Unit,
) : RecyclerView.Adapter<DepartmentAdapter.Holder>() {

    inner class Holder(val b: ItemDepartmentBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Holder(ItemDepartmentBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val (dept, count) = items[position]
        holder.b.apply {
            deptStrip.setBackgroundColor(ContextCompat.getColor(root.context, dept.colorRes()))
            deptName.text = dept.displayName
            deptCount.text = root.context.resources.getQuantityString(R.plurals.people_count, count, count)
            deptHead.text = root.context.getString(R.string.department_head, dept.head)
            root.setOnClickListener { onClick(dept) }
        }
    }
}
