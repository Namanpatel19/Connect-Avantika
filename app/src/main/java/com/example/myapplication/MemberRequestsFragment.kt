package com.example.myapplication

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapters.ClubRequestAdapter
import com.example.myapplication.data.Student
import com.example.myapplication.databinding.FragmentMemberRequestsBinding
import com.example.myapplication.ui.viewmodel.AppViewModel
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

        // Observe students to build the studentMap
        vm.students.observe(viewLifecycleOwner) { students ->
            studentMap = students.associateBy { it.userId }
            refreshAdapter()
        }

        // Observe club requests to refresh the list
        vm.clubRequests.observe(viewLifecycleOwner) { 
            refreshAdapter()
        }

        vm.myClub.observe(viewLifecycleOwner) { club ->
            club?.let { vm.loadClubRequests(it.id!!) }
        }

        if (vm.myClub.value == null) vm.loadMyClub()
        vm.loadAllStudents()
    }

    private fun refreshAdapter() {
        val requests = vm.clubRequests.value?.filter { it.status == "pending" || it.status == "interview" } ?: emptyList()
        
        binding.tvPendingCount.text = "${requests.size} active request(s)"
        binding.tvEmpty.visibility = if (requests.isEmpty()) View.VISIBLE else View.GONE
        
        binding.rvRequests.adapter = ClubRequestAdapter(
            requests    = requests,
            studentMap  = studentMap,
            onAccept    = { req -> 
                vm.acceptClubRequest(req) { success ->
                    if (success) {
                        Toast.makeText(context, "Student accepted!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Failed to accept student.", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            onReject    = { req -> 
                vm.rejectClubRequest(req) { success ->
                    if (success) {
                        Toast.makeText(context, "Request rejected", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Failed to reject.", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            onInterview = { req -> showInterviewDialog(req) }
        )
    }

    private fun showInterviewDialog(request: com.example.myapplication.data.ClubRequest) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(requireContext(), { _, year, month, day ->
            TimePickerDialog(requireContext(), { _, hour, minute ->
                val date = String.format("%d-%02d-%02d", year, month + 1, day)
                val time = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
                
                val input = EditText(requireContext())
                input.hint = "Venue"
                AlertDialog.Builder(requireContext())
                    .setTitle("Interview Venue")
                    .setView(input)
                    .setPositiveButton("Schedule") { _, _ ->
                        val venue = input.text.toString().ifEmpty { "Main Hall" }
                        vm.callForInterview(request, date, time, venue) { success ->
                            if (success) {
                                Toast.makeText(context, "Interview scheduled!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Failed to schedule interview.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()

            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
