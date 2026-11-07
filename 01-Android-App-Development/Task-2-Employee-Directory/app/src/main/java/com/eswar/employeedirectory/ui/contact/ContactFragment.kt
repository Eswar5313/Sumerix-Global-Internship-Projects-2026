package com.eswar.employeedirectory.ui.contact

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.eswar.employeedirectory.R
import com.eswar.employeedirectory.databinding.FragmentContactBinding
import com.eswar.employeedirectory.model.Department

class ContactFragment : Fragment() {

    private var _binding: FragmentContactBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentContactBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val names = Department.entries.map { it.displayName }
        binding.inputDepartment.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, names))
        binding.buttonSubmit.setOnClickListener { submit() }
    }

    /** Returns true when the form is valid and the confirmation dialog was shown. */
    fun submit(): Boolean {
        val name = binding.inputName.text?.toString()?.trim().orEmpty()
        val email = binding.inputEmail.text?.toString()?.trim().orEmpty()
        val dept = binding.inputDepartment.text?.toString().orEmpty()
        val message = binding.inputMessage.text?.toString()?.trim().orEmpty()
        binding.layoutName.error = if (name.isEmpty()) getString(R.string.error_required) else null
        binding.layoutEmail.error = if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) getString(R.string.error_email) else null
        binding.layoutDepartment.error = if (Department.fromDisplayName(dept) == null) getString(R.string.error_department) else null
        binding.layoutMessage.error = if (message.length < 10) getString(R.string.error_message) else null
        val ok = listOf(binding.layoutName, binding.layoutEmail, binding.layoutDepartment, binding.layoutMessage).all { it.error == null }
        if (!ok) {
            Snackbar.make(binding.root, R.string.fix_errors, Snackbar.LENGTH_SHORT).show()
            return false
        }
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_sent_title)
            .setMessage(getString(R.string.dialog_sent_message, name, dept))
            .setPositiveButton(R.string.ok, null)
            .show()
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
