package com.eswar.employeedirectory.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.eswar.employeedirectory.R
import com.eswar.employeedirectory.data.EmployeeRepository
import com.eswar.employeedirectory.databinding.FragmentEmployeeListBinding
import com.eswar.employeedirectory.model.Department
import com.eswar.employeedirectory.model.DirectoryItem
import com.eswar.employeedirectory.model.Employee
import com.eswar.employeedirectory.ui.adapter.DirectoryAdapter

class EmployeeListFragment : Fragment() {

    private var _binding: FragmentEmployeeListBinding? = null
    private val binding get() = _binding!!
    private val args: EmployeeListFragmentArgs by navArgs()     // Safe Args
    private lateinit var adapter: DirectoryAdapter
    private var selectedDepartment: Department? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEmployeeListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        selectedDepartment = Department.fromDisplayName(args.departmentName)
        adapter = DirectoryAdapter(onEmployeeClick = ::openDetail, onFavouriteClick = ::toggleFavourite)

        val span = resources.getInteger(R.integer.list_span)
        binding.recycler.layoutManager = GridLayoutManager(requireContext(), span).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int) =
                    if (adapter.getItemViewType(position) == DirectoryAdapter.TYPE_HEADER) span else 1
            }
        }
        binding.recycler.adapter = adapter

        buildDepartmentChips()
        binding.searchInput.doAfterTextChanged { refresh() }
        binding.fabTop.setOnClickListener { binding.recycler.smoothScrollToPosition(0) }
        refresh()
    }

    private fun buildDepartmentChips() {
        val group = binding.chipGroup
        val all = layoutInflater.inflate(R.layout.view_filter_chip, group, false) as Chip
        all.text = getString(R.string.filter_all)
        all.id = View.generateViewId()
        group.addView(all)
        Department.entries.forEach { dept ->
            val chip = layoutInflater.inflate(R.layout.view_filter_chip, group, false) as Chip
            chip.text = dept.displayName
            chip.id = View.generateViewId()
            chip.tag = dept
            group.addView(chip)
            if (dept == selectedDepartment) chip.isChecked = true
        }
        if (selectedDepartment == null) all.isChecked = true
        group.setOnCheckedStateChangeListener { g, ids ->
            selectedDepartment = ids.firstOrNull()?.let { g.findViewById<Chip>(it).tag as? Department }
            refresh()
        }
    }

    /** Recomputes the list from the current search text + department chip. */
    fun refresh() {
        val query = binding.searchInput.text?.toString().orEmpty()
        val items = EmployeeRepository.directoryItems(query, selectedDepartment)
        adapter.submitList(items)
        val people = items.count { it is DirectoryItem.Row }
        binding.resultCount.text = resources.getQuantityString(R.plurals.results_count, people, people)
        binding.emptyState.visibility = if (people == 0) View.VISIBLE else View.GONE
    }

    private fun openDetail(e: Employee) {
        findNavController().navigate(EmployeeListFragmentDirections.actionListToDetail(e.employeeId))
    }

    private fun toggleFavourite(e: Employee) {
        val added = EmployeeRepository.toggleFavourite(e.employeeId)
        refresh()
        Snackbar.make(binding.root,
            getString(if (added) R.string.favourite_added else R.string.favourite_removed, e.fullName),
            Snackbar.LENGTH_SHORT)
            .setAction(R.string.undo) { EmployeeRepository.toggleFavourite(e.employeeId); refresh() }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
