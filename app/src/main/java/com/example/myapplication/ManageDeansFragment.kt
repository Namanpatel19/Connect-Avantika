package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapplication.databinding.FragmentManageStudentsDeanBinding

class ManageDeansFragment : Fragment() {
    private var _binding: FragmentManageStudentsDeanBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentManageStudentsDeanBinding.inflate(inflater, container, false).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.tvCount.text = "Management for Deans (Coming Soon)"
        binding.btnAddStudent.visibility = View.GONE
        binding.etSearch.hint = "Search deans..."
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
