package com.eswar.employeedirectory.ui.company

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.eswar.employeedirectory.data.EmployeeRepository
import com.eswar.employeedirectory.databinding.FragmentCompanyBinding
import com.eswar.employeedirectory.R

class CompanyFragment : Fragment() {

    private var _binding: FragmentCompanyBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCompanyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val stats = EmployeeRepository.stats()
        binding.companyFacts.text = getString(R.string.company_facts, stats.totalEmployees, stats.departments, stats.locations)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
