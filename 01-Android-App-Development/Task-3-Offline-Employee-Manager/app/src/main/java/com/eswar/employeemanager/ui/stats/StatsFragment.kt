package com.eswar.employeemanager.ui.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.eswar.employeemanager.R
import com.eswar.employeemanager.databinding.FragmentStatsBinding
import com.eswar.employeemanager.databinding.ItemStatRowBinding
import com.eswar.employeemanager.ui.EmployeeViewModel
import com.eswar.employeemanager.ui.asRupees

/** Statistics computed by SQL (GROUP BY + LEFT JOIN) and observed as LiveData. */
class StatsFragment : Fragment() {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EmployeeViewModel by activityViewModels { EmployeeViewModel.Factory }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.stats.observe(viewLifecycleOwner) { stats ->
            val total = stats.sumOf { it.headcount }
            val payroll = stats.sumOf { it.totalSalary }
            binding.totalEmployees.text = total.toString()
            binding.totalPayroll.text = payroll.asRupees()
            binding.avgSalary.text = (if (total == 0) 0.0 else payroll / total).asRupees()
            binding.rows.removeAllViews()
            val max = (stats.maxOfOrNull { it.headcount } ?: 1).coerceAtLeast(1)
            stats.forEach { s ->
                ItemStatRowBinding.inflate(layoutInflater, binding.rows, true).apply {
                    deptName.text = s.name
                    deptValue.text = getString(R.string.stat_row, s.headcount, s.avgSalary.asRupees())
                    deptBar.max = max
                    deptBar.progress = s.headcount
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
