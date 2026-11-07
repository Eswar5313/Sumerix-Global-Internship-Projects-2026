package com.eswar.employeedirectory.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.eswar.employeedirectory.R
import com.eswar.employeedirectory.databinding.ItemDepartmentHeaderBinding
import com.eswar.employeedirectory.databinding.ItemEmployeeBinding
import com.eswar.employeedirectory.model.DirectoryItem
import com.eswar.employeedirectory.model.Employee
import com.eswar.employeedirectory.ui.colorRes
import com.eswar.employeedirectory.ui.showAvatar

/**
 * One adapter, two view types (department header + employee card), driven by DiffUtil.
 * Click handling is passed in as lambdas so the adapter has no navigation knowledge.
 */
class DirectoryAdapter(
    private val onEmployeeClick: (Employee) -> Unit,
    private val onFavouriteClick: (Employee) -> Unit,
) : ListAdapter<DirectoryItem, RecyclerView.ViewHolder>(Diff) {

    init { setHasStableIds(true) }

    override fun getItemId(position: Int): Long = getItem(position).stableId

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is DirectoryItem.Header -> TYPE_HEADER
        is DirectoryItem.Row -> TYPE_EMPLOYEE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_HEADER) {
            HeaderHolder(ItemDepartmentHeaderBinding.inflate(inflater, parent, false))
        } else {
            EmployeeHolder(ItemEmployeeBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is DirectoryItem.Header -> (holder as HeaderHolder).bind(item)
            is DirectoryItem.Row -> (holder as EmployeeHolder).bind(item.employee)
        }
    }

    inner class HeaderHolder(private val b: ItemDepartmentHeaderBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(item: DirectoryItem.Header) = with(b) {
            headerTitle.text = item.department.displayName
            headerCount.text = root.context.resources.getQuantityString(R.plurals.people_count, item.count, item.count)
            headerBar.setBackgroundColor(ContextCompat.getColor(root.context, item.department.colorRes()))
        }
    }

    inner class EmployeeHolder(private val b: ItemEmployeeBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(e: Employee) = with(b) {
            avatar.showAvatar(e.fullName, e.department)
            name.text = e.fullName
            designation.text = e.designation
            meta.text = root.context.getString(R.string.employee_meta, e.department.displayName, e.location)
            managerBadge.visibility = if (e.isManager) android.view.View.VISIBLE else android.view.View.GONE
            favourite.setImageResource(if (e.isFavorite) R.drawable.ic_star else R.drawable.ic_star_border)
            favourite.contentDescription = root.context.getString(
                if (e.isFavorite) R.string.remove_favourite else R.string.add_favourite)
            root.setOnClickListener { onEmployeeClick(e) }
            favourite.setOnClickListener { onFavouriteClick(e) }
        }
    }

    private object Diff : DiffUtil.ItemCallback<DirectoryItem>() {
        override fun areItemsTheSame(old: DirectoryItem, new: DirectoryItem) = old.stableId == new.stableId
        override fun areContentsTheSame(old: DirectoryItem, new: DirectoryItem) = old == new
    }

    companion object {
        const val TYPE_HEADER = 0
        const val TYPE_EMPLOYEE = 1
    }
}
