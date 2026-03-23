package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.data.FirebaseManager
import com.example.myapplication.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        loadProfile()
        setupButtons()
    }

    private fun loadProfile() {
        viewLifecycleOwner.lifecycleScope.launch {
            val student = FirebaseManager.getStudentProfile()
            if (student != null) {
                binding.userName.text = student.name
                binding.userDeptYear.text = "${student.department} | ${student.year}"
                binding.userId.text = "ID: ${student.studentId}"
            }
        }
    }

    private fun setupButtons() {
        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        
        binding.btnEditProfile.setOnClickListener {
            // Handle edit profile
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}