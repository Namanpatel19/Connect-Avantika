package com.example.myapplication

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapters.ClubRequestAdapter
import com.example.myapplication.data.Student
import com.example.myapplication.databinding.FragmentMemberRequestsBinding
import com.example.myapplication.ui.viewmodel.AppViewModel
import kotlinx.coroutines.launch
import java.util.*

class MemberRequestsFragment : Fragment() {
    private var _binding: FragmentMemberRequestsBinding? = null
    private val binding get() = _binding!!
    private lateinit var vm: AppViewModel
    private var studentMap: Map<String, Student> = emptyMap()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentMemberRequestsBinding.inflate(inflater, container, false).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(requireActivity())[AppViewModel::class.java]

        binding.rvRequests.layoutManager = LinearLayoutManager(context)

        // Load student list once to build the name lookup map
        vm.students.observe(viewLifecycleOwner) { students ->
            studentMap = students.associateBy { it.userId }
            rebuildAdapter()
        }

        vm.clubRequests.observe(viewLifecycleOwner) { requests ->
            val activeRequests = requests.filter { it.status == "pending" || it.status == "interview" }
            binding.tvPendingCount.text = "${activeRequests.size} active request(s)"
            binding.tvEmpty.visibility = if (activeRequests.isEmpty()) View.VISIBLE else View.GONE
            binding.rvRequests.adapter = ClubRequestAdapter(
                requests    = activeRequests,
                studentMap  = studentMap,
                onAccept    = { req -> updateRequest(req.id!!, "accepted") },
                onReject    = { req -> updateRequest(req.id!!, "rejected") },
                onInterview = { req -> showInterviewDialog(req.id!!) }
            )
        }

        vm.myClub.observe(viewLifecycleOwner) { club ->
            club?.let {
                vm.loadClubRequests(it.id!!)
            }
        }

        if (vm.myClub.value == null) vm.loadMyClub()
        vm.loadAllStudents()          // ensures studentMap is populated
    }

    private fun rebuildAdapter() {
        val requests = vm.clubRequests.value
            ?.filter { it.status == "pending" || it.status == "interview" }
            ?: return
        binding.rvRequests.adapter = ClubRequestAdapter(
            requests    = requests,
            studentMap  = studentMap,
            onAccept    = { req -> updateRequest(req.id!!, "accepted") },
            onReject    = { req -> updateRequest(req.id!!, "rejected") },
            onInterview = { req -> showInterviewDialog(req.id!!) }
        )
    }

    private fun showInterviewDialog(requestId: String) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(requireContext(), { _, year, month, day ->
            TimePickerDialog(requireContext(), { _, hour, minute ->
                val date = String.format("%d-%02d-%02d", year, month + 1, day)
                val time = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
                updateRequestWithInterview(requestId, date, time)
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun updateRequest(id: String, status: String) {
        vm.updateClubRequest(id, status) { success ->
            Toast.makeText(context,
                if (success) "Request $status successfully" else "Failed to update request",
                Toast.LENGTH_SHORT).show()
            vm.myClub.value?.id?.let { vm.loadClubRequests(it) }
        }
    }

    private fun updateRequestWithInterview(id: String, date: String, time: String) {
        vm.updateClubRequest(id, "interview", date, time) { success ->
            if (success) {
                Toast.makeText(context, "Interview scheduled for $date at $time", Toast.LENGTH_LONG).show()
                vm.myClub.value?.id?.let { vm.loadClubRequests(it) }
            } else {
                Toast.makeText(context, "Failed to schedule interview", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
