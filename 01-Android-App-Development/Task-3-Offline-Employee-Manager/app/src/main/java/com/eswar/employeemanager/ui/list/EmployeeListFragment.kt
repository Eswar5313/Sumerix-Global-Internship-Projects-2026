package com.eswar.employeemanager.ui.list

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.eswar.employeemanager.R
import com.eswar.employeemanager.data.SortOrder
import com.eswar.employeemanager.databinding.FragmentEmployeeListBinding
import com.eswar.employeemanager.ui.EmployeeViewModel
import com.eswar.employeemanager.ui.UiEvent
import com.eswar.employeemanager.ui.asRupees
import kotlinx.coroutines.launch

class EmployeeListFragment : Fragment() {

    private var _binding: FragmentEmployeeListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EmployeeViewModel by activityViewModels { EmployeeViewModel.Factory }
    private var departmentNames: Map<String, String> = emptyMap()
    private lateinit var adapter: EmployeeAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEmployeeListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = EmployeeAdapter(
            departmentName = { code -> departmentNames[code] ?: code },
            onClick = { e -> findNavController().navigate(EmployeeListFragmentDirections.actionListToDetail(e.id)) },
            onFavourite = { e -> viewModel.toggleFavourite(e) },
        )
        binding.recycler.layoutManager = GridLayoutManager(requireContext(), resources.getInteger(R.integer.list_span))
        binding.recycler.adapter = adapter
        attachSwipeToDelete()

        binding.searchInput.setText(viewModel.filter.value?.query.orEmpty())
        binding.searchInput.doAfterTextChanged { viewModel.setQuery(it?.toString().orEmpty()) }
        binding.fabAdd.setOnClickListener {
            findNavController().navigate(EmployeeListFragmentDirections.actionListToEdit(0L))
        }

        viewModel.departments.observe(viewLifecycleOwner) { list ->
            departmentNames = list.associate { it.code to it.name }
            buildChips()
            adapter.notifyDataSetChanged()
        }
        viewModel.employees.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            binding.emptyState.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
            binding.summary.text = getString(R.string.list_summary, list.size,
                if (list.isEmpty()) 0.0.asRupees() else list.map { it.salary }.average().asRupees())
        }
        viewModel.events.observe(viewLifecycleOwner) { ev ->
            val e = ev.peek()
            if (e is UiEvent.Deleted) ev.getContentIfNotHandled()?.let {
                Snackbar.make(binding.root, getString(R.string.deleted, e.employee.name), Snackbar.LENGTH_LONG)
                    .setAction(R.string.undo) { viewModel.undoDelete(e.employee) }
                    .setAnchorView(binding.fabAdd)
                    .show()
            }
        }

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, inflater: MenuInflater) = inflater.inflate(R.menu.menu_list, menu)
            override fun onMenuItemSelected(item: MenuItem): Boolean = when (item.itemId) {
                R.id.sort_name -> { viewModel.setSort(SortOrder.NAME); true }
                R.id.sort_salary -> { viewModel.setSort(SortOrder.SALARY); true }
                R.id.sort_joined -> { viewModel.setSort(SortOrder.JOINED); true }
                R.id.action_stats -> { findNavController().navigate(R.id.statsFragment); true }
                R.id.action_export -> { exportCsv(); true }
                else -> false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun buildChips() {
        val group = binding.chipGroup
        group.setOnCheckedStateChangeListener(null)
        group.removeAllViews()
        val selected = viewModel.filter.value?.departmentCode.orEmpty()
        (listOf("" to getString(R.string.filter_all)) + departmentNames.toList()).forEach { (code, name) ->
            val chip = layoutInflater.inflate(R.layout.view_filter_chip, group, false) as Chip
            chip.id = View.generateViewId(); chip.text = name; chip.tag = code
            group.addView(chip)
            if (code == selected) chip.isChecked = true
        }
        group.setOnCheckedStateChangeListener { g, ids ->
            val code = ids.firstOrNull()?.let { g.findViewById<Chip>(it).tag as String } ?: ""
            viewModel.setDepartment(code)
        }
    }

    private fun attachSwipeToDelete() {
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder) = false
            override fun onSwiped(vh: RecyclerView.ViewHolder, direction: Int) {
                viewModel.delete(adapter.itemAt(vh.bindingAdapterPosition))
            }
        }).attachToRecyclerView(binding.recycler)
    }

    private fun exportCsv() = viewLifecycleOwner.lifecycleScope.launch {
        val csv = viewModel.exportCsv()
        val send = Intent(Intent.ACTION_SEND).setType("text/csv")
            .putExtra(Intent.EXTRA_SUBJECT, getString(R.string.export_subject))
            .putExtra(Intent.EXTRA_TEXT, csv)
        startActivity(Intent.createChooser(send, getString(R.string.export_csv)))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
