package com.eswar.employeemanager.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.eswar.employeemanager.R
import com.eswar.employeemanager.data.EmployeeEntity
import com.eswar.employeemanager.databinding.ItemEmployeeBinding
import com.eswar.employeemanager.ui.asRupees
import com.eswar.employeemanager.ui.pretty
import com.eswar.employeemanager.ui.showAvatar

class EmployeeAdapter(
    private val departmentName: (String) -> String,
    private val onClick: (EmployeeEntity) -> Unit,
    private val onFavourite: (EmployeeEntity) -> Unit,
) : ListAdapter<EmployeeEntity, EmployeeAdapter.Holder>(Diff) {

    inner class Holder(val b: ItemEmployeeBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Holder(ItemEmployeeBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val e = getItem(position)
        with(holder.b) {
            avatar.showAvatar(e.name, e.departmentCode)
            name.text = e.name
            designation.text = root.context.getString(R.string.designation_department, e.designation, departmentName(e.departmentCode))
            salary.text = e.salary.asRupees()
            joined.text = root.context.getString(R.string.joined_on, e.joiningDate.pretty())
            favourite.setImageResource(if (e.isFavorite) R.drawable.ic_star else R.drawable.ic_star_border)
            root.setOnClickListener { onClick(e) }
            favourite.setOnClickListener { onFavourite(e) }
        }
    }

    fun itemAt(position: Int): EmployeeEntity = getItem(position)

    private object Diff : DiffUtil.ItemCallback<EmployeeEntity>() {
        override fun areItemsTheSame(a: EmployeeEntity, b: EmployeeEntity) = a.id == b.id
        override fun areContentsTheSame(a: EmployeeEntity, b: EmployeeEntity) = a == b
    }
}
