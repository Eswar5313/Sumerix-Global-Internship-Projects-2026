package com.eswar.employeedirectory.ui.departments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.eswar.employeedirectory.R
import com.eswar.employeedirectory.data.EmployeeRepository
import com.eswar.employeedirectory.databinding.FragmentDepartmentsBinding
import com.eswar.employeedirectory.ui.adapter.DepartmentAdapter

class DepartmentsFragment : Fragment() {

    private var _binding: FragmentDepartmentsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDepartmentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val counts = EmployeeRepository.stats().headcountByDepartment.toList()
        binding.recycler.layoutManager = GridLayoutManager(requireContext(), resources.getInteger(R.integer.department_span))
        binding.recycler.adapter = DepartmentAdapter(counts) { dept ->
            // Passes the department to the list screen through a Safe Args argument
            findNavController().navigate(DepartmentsFragmentDirections.actionDepartmentsToList(dept.displayName))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
