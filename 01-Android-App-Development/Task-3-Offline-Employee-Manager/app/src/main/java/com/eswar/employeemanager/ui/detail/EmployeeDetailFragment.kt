package com.eswar.employeemanager.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.eswar.employeemanager.R
import com.eswar.employeemanager.databinding.FragmentDetailBinding
import com.eswar.employeemanager.databinding.ItemInfoRowBinding
import com.eswar.employeemanager.ui.EmployeeViewModel
import com.eswar.employeemanager.ui.asRupees
import com.eswar.employeemanager.ui.pretty
import com.eswar.employeemanager.ui.showAvatar
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class EmployeeDetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EmployeeViewModel by activityViewModels { EmployeeViewModel.Factory }
    private val args: EmployeeDetailFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        var names = emptyMap<String, String>()
        viewModel.departments.observe(viewLifecycleOwner) { list -> names = list.associate { it.code to it.name } }
        viewModel.observe(args.employeeId).observe(viewLifecycleOwner) { e ->
            if (e == null) return@observe   // deleted
            binding.avatar.showAvatar(e.name, e.departmentCode)
            binding.name.text = e.name
            binding.designation.text = e.designation
            binding.departmentChip.text = names[e.departmentCode] ?: e.departmentCode
            binding.infoContainer.removeAllViews()
            val years = ChronoUnit.DAYS.between(e.joiningDate, LocalDate.now()) / 365.25
            listOf(
                Triple(R.drawable.ic_badge, R.string.label_id, "EMP-%04d".format(e.id)),
                Triple(R.drawable.ic_mail, R.string.label_email, e.email),
                Triple(R.drawable.ic_phone, R.string.label_phone, e.phone),
                Triple(R.drawable.ic_payments, R.string.label_salary, getString(R.string.salary_format, e.salary.asRupees(), (e.salary / 12).asRupees())),
                Triple(R.drawable.ic_calendar, R.string.label_joined, getString(R.string.tenure_format, e.joiningDate.pretty(), years)),
            ).forEach { (icon, label, value) ->
                ItemInfoRowBinding.inflate(layoutInflater, binding.infoContainer, true).apply {
                    infoIcon.setImageResource(icon); infoLabel.setText(label); infoValue.text = value
                }
            }
            binding.buttonEdit.setOnClickListener {
                findNavController().navigate(EmployeeDetailFragmentDirections.actionDetailToEdit(e.id))
            }
            binding.buttonDelete.setOnClickListener {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.confirm_delete_title, e.name))
                    .setMessage(R.string.confirm_delete_message)
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.delete) { _, _ ->
                        viewModel.delete(e)
                        findNavController().popBackStack()
                    }.show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
