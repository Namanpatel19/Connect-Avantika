package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapters.AnnouncementAdapter
import com.example.myapplication.databinding.FragmentNoticesBinding
import com.example.myapplication.ui.viewmodel.AppViewModel

class NoticesFragment : Fragment() {

    private var _binding: FragmentNoticesBinding? = null
    private val binding get() = _binding!!
    private lateinit var vm: AppViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoticesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(requireActivity())[AppViewModel::class.java]

        binding.rvNotices.layoutManager = LinearLayoutManager(requireContext())

        vm.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        vm.announcements.observe(viewLifecycleOwner) { list ->
            binding.rvNotices.adapter = AnnouncementAdapter(list)
        }

        vm.loadAllAnnouncements()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
