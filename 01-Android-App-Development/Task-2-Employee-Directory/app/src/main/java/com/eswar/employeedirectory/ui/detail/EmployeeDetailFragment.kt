package com.eswar.employeedirectory.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.eswar.employeedirectory.R
import com.eswar.employeedirectory.data.EmployeeRepository
import com.eswar.employeedirectory.databinding.FragmentEmployeeDetailBinding
import com.eswar.employeedirectory.databinding.ItemInfoRowBinding
import com.eswar.employeedirectory.model.yearsOfService
import com.eswar.employeedirectory.ui.showAvatar
import java.time.format.DateTimeFormatter
import java.util.Locale

class EmployeeDetailFragment : Fragment() {

    private var _binding: FragmentEmployeeDetailBinding? = null
    private val binding get() = _binding!!
    private val args: EmployeeDetailFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEmployeeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val e = EmployeeRepository.byId(args.employeeId) ?: run {
            binding.name.setText(R.string.employee_not_found); return
        }
        binding.avatar.showAvatar(e.fullName, e.department)
        binding.name.text = e.fullName
        binding.designation.text = e.designation
        binding.departmentChip.text = e.department.displayName

        val joined = e.joinDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.US))
        val rows = listOf(
            Triple(R.drawable.ic_badge, getString(R.string.label_employee_id), "NS-${e.employeeId}"),
            Triple(R.drawable.ic_mail, getString(R.string.label_email), e.email),
            Triple(R.drawable.ic_phone, getString(R.string.label_phone), e.phone),
            Triple(R.drawable.ic_location, getString(R.string.label_location), e.location),
            Triple(R.drawable.ic_calendar, getString(R.string.label_joined),
                getString(R.string.joined_format, joined, e.yearsOfService())),
        )
        rows.forEach { (icon, label, value) ->
            ItemInfoRowBinding.inflate(layoutInflater, binding.infoContainer, true).apply {
                infoIcon.setImageResource(icon)
                infoLabel.text = label
                infoValue.text = value
            }
        }
        e.skills.forEach { skill ->
            val chip = layoutInflater.inflate(R.layout.view_skill_chip, binding.skillGroup, false) as Chip
            chip.text = skill
            binding.skillGroup.addView(chip)
        }
        binding.buttonCall.setOnClickListener { startSafely(Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + e.phone))) }
        binding.buttonEmail.setOnClickListener { startSafely(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + e.email))) }
        binding.buttonMessage.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.dialog_contact_title, e.first))
                .setMessage(getString(R.string.dialog_contact_message, e.fullName, e.department.displayName))
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.action_email) { _, _ -> binding.buttonEmail.performClick() }
                .show()
        }
    }

    private fun startSafely(intent: Intent) {
        if (intent.resolveActivity(requireContext().packageManager) != null) startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
