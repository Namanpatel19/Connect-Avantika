package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.myapplication.databinding.DialogAcademicsBinding

class AcademicsDialog : DialogFragment() {

    private var _binding: DialogAcademicsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ) = DialogAcademicsBinding.inflate(inflater, container, false)
        .also { _binding = it }.root

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnOpenMoodle.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://moodle.avantika.edu.in/login/index.php"))
            startActivity(intent)
            dismiss()
        }

        binding.btnStudyMaterial.setOnClickListener {
            (activity as? MainActivity)?.navigateTo(R.id.navigation_student_materials)
            dismiss()
        }

        binding.btnClose.setOnClickListener { dismiss() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
