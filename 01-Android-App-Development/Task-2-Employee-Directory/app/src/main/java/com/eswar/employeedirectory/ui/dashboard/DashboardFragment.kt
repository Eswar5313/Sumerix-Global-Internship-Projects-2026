package com.eswar.employeedirectory.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.eswar.employeedirectory.R
import com.eswar.employeedirectory.data.EmployeeRepository
import com.eswar.employeedirectory.databinding.FragmentDashboardBinding
import com.eswar.employeedirectory.databinding.ItemTeamStatBinding
import com.eswar.employeedirectory.model.DirectoryItem
import com.eswar.employeedirectory.ui.adapter.DirectoryAdapter
import java.util.Locale

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!   // valid only between onCreateView and onDestroyView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val stats = EmployeeRepository.stats()
        binding.cardEmployees.statValue.text = stats.totalEmployees.toString()
        binding.cardEmployees.statLabel.setText(R.string.stat_employees)
        binding.cardDepartments.statValue.text = stats.departments.toString()
        binding.cardDepartments.statLabel.setText(R.string.stat_departments)
        binding.cardTenure.statValue.text = String.format(Locale.US, "%.1f", stats.averageTenureYears)
        binding.cardTenure.statLabel.setText(R.string.stat_tenure)
        binding.cardLocations.statValue.text = stats.locations.toString()
        binding.cardLocations.statLabel.setText(R.string.stat_locations)

        val max = stats.headcountByDepartment.values.maxOrNull() ?: 1
        stats.headcountByDepartment.forEach { (dept, count) ->
            ItemTeamStatBinding.inflate(layoutInflater, binding.teamStatsContainer, true).apply {
                teamName.text = dept.displayName
                teamCount.text = count.toString()
                teamProgress.max = max
                teamProgress.progress = count
            }
        }

        val adapter = DirectoryAdapter(
            onEmployeeClick = { e ->
                findNavController().navigate(DashboardFragmentDirections.actionDashboardToDetail(e.employeeId))
            },
            onFavouriteClick = {},
        )
        binding.newJoiners.layoutManager = LinearLayoutManager(requireContext())
        binding.newJoiners.adapter = adapter
        binding.newJoiners.isNestedScrollingEnabled = false
        adapter.submitList(EmployeeRepository.newJoiners().map { DirectoryItem.Row(it) })

        binding.buttonViewAll.setOnClickListener {
            findNavController().navigate(R.id.employeeListFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
