package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.data.FirebaseManager
import com.example.myapplication.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadDashboard()
    }

    private fun loadDashboard() {
        viewLifecycleOwner.lifecycleScope.launch {
            val student = FirebaseManager.getStudentProfile()
            if (student != null) {
                binding.welcomeText.text = "Welcome, ${student.name}!"
            }
            
            // For a real dashboard, we could fetch a few items for each section
            // Here we just use the static layout but could populate it dynamically
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}