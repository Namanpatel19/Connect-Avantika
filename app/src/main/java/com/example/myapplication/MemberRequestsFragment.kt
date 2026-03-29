package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapters.ClubRequestAdapter
import com.example.myapplication.databinding.FragmentMemberRequestsBinding
import com.example.myapplication.ui.viewmodel.AppViewModel

class MemberRequestsFragment : Fragment() {
    private var _binding: FragmentMemberRequestsBinding? = null
    private val binding get() = _binding!!
    private lateinit var vm: AppViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentMemberRequestsBinding.inflate(inflater, container, false).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(requireActivity())[AppViewModel::class.java]

        binding.rvRequests.layoutManager = LinearLayoutManager(context)

        vm.clubRequests.observe(viewLifecycleOwner) { requests ->
            val pending = requests.filter { it.status == "pending" }
            binding.tvPendingCount.text = "${pending.size} total membership requests"
            binding.tvEmpty.visibility = if (pending.isEmpty()) View.VISIBLE else View.GONE
            binding.rvRequests.adapter = ClubRequestAdapter(pending,
                onAccept    = { req -> updateRequest(req.id!!, "accepted") },
                onReject    = { req -> updateRequest(req.id!!, "rejected") },
                onInterview = { req -> updateRequest(req.id!!, "interview") }
            )
        }

        vm.myClub.observe(viewLifecycleOwner) { club ->
            club?.let { vm.loadClubRequests(it.id!!) }
        }

        if (vm.myClub.value == null) vm.loadMyClub()
    }

    private fun updateRequest(id: String, status: String) {
        vm.updateClubRequest(id, status) { success ->
            Toast.makeText(context, if (success) "Updated to $status" else "Error", Toast.LENGTH_SHORT).show()
            vm.myClub.value?.id?.let { vm.loadClubRequests(it) }
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
