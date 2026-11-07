package com.eswar.employeemanager.ui.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.eswar.employeemanager.R
import com.eswar.employeemanager.data.DepartmentEntity
import com.eswar.employeemanager.databinding.FragmentAddEditBinding
import com.eswar.employeemanager.domain.EmployeeForm
import com.eswar.employeemanager.domain.Field
import com.eswar.employeemanager.ui.EmployeeViewModel
import com.eswar.employeemanager.ui.UiEvent
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

class AddEditEmployeeFragment : Fragment() {

    private var _binding: FragmentAddEditBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EmployeeViewModel by activityViewModels { EmployeeViewModel.Factory }
    private val args: AddEditEmployeeFragmentArgs by navArgs()
    private var departments: List<DepartmentEntity> = emptyList()
    private var favourite = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.formTitle.setText(if (args.employeeId == 0L) R.string.title_add else R.string.title_edit)
        viewModel.departments.observe(viewLifecycleOwner) { list ->
            departments = list
            binding.inputDepartment.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, list.map { it.name }))
        }
        if (args.employeeId != 0L && savedInstanceState == null) {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.load(args.employeeId)?.let { e ->
                    favourite = e.isFavorite
                    binding.inputName.setText(e.name)
                    binding.inputDesignation.setText(e.designation)
                    binding.inputDepartment.setText(departments.firstOrNull { it.code == e.departmentCode }?.name ?: e.departmentCode, false)
                    binding.inputEmail.setText(e.email)
                    binding.inputPhone.setText(e.phone)
                    binding.inputSalary.setText(e.salary.toLong().toString())
                    binding.inputJoining.setText(e.joiningDate.toString())
                }
            }
        } else if (args.employeeId == 0L && savedInstanceState == null) {
            binding.inputJoining.setText(LocalDate.now().toString())
        }
        binding.layoutJoining.setEndIconOnClickListener { pickDate() }
        binding.buttonSave.setOnClickListener { save() }

        viewModel.formErrors.observe(viewLifecycleOwner) { errors ->
            show(binding.layoutName, errors[Field.NAME]); show(binding.layoutDesignation, errors[Field.DESIGNATION])
            show(binding.layoutDepartment, errors[Field.DEPARTMENT]); show(binding.layoutEmail, errors[Field.EMAIL])
            show(binding.layoutPhone, errors[Field.PHONE]); show(binding.layoutSalary, errors[Field.SALARY])
            show(binding.layoutJoining, errors[Field.JOINING_DATE])
        }
        viewModel.events.observe(viewLifecycleOwner) { ev ->
            when (val e = ev.peek()) {
                is UiEvent.Saved -> ev.getContentIfNotHandled()?.let {
                    Snackbar.make(requireActivity().findViewById(android.R.id.content),
                        if (e.isNew) R.string.saved_new else R.string.saved_update, Snackbar.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
                is UiEvent.Error -> ev.getContentIfNotHandled()?.let { binding.layoutEmail.error = e.message }
                else -> Unit
            }
        }
    }

    private fun show(layout: TextInputLayout, error: String?) { layout.error = error }

    fun currentForm() = EmployeeForm(
        id = args.employeeId,
        name = binding.inputName.text?.toString().orEmpty(),
        designation = binding.inputDesignation.text?.toString().orEmpty(),
        departmentCode = departments.firstOrNull { it.name == binding.inputDepartment.text?.toString() }?.code.orEmpty(),
        email = binding.inputEmail.text?.toString().orEmpty(),
        phone = binding.inputPhone.text?.toString().orEmpty(),
        salary = binding.inputSalary.text?.toString().orEmpty(),
        joiningDate = binding.inputJoining.text?.toString().orEmpty(),
        isFavorite = favourite,
    )

    fun save(): Boolean = viewModel.save(currentForm())

    private fun pickDate() {
        val picker = MaterialDatePicker.Builder.datePicker().setTitleText(R.string.hint_joining).build()
        picker.addOnPositiveButtonClickListener { millis ->
            binding.inputJoining.setText(Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate().toString())
        }
        picker.show(childFragmentManager, "date")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
