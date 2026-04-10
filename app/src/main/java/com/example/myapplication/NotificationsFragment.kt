package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapters.NotificationAdapter
import com.example.myapplication.databinding.FragmentNotificationsBinding
import com.example.myapplication.ui.viewmodel.AppViewModel

class NotificationsFragment : Fragment() {
    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!
    private lateinit var vm: AppViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentNotificationsBinding.inflate(inflater, container, false).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(requireActivity())[AppViewModel::class.java]

        binding.rvNotifications.layoutManager = LinearLayoutManager(context)
        vm.notifications.observe(viewLifecycleOwner) { list ->
            binding.rvNotifications.adapter = NotificationAdapter(list)
        }

        binding.btnBack.setOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        vm.loadNotifications()
        // Mark all as read when opening the screen
        vm.markNotificationsAsRead()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
