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
import com.example.myapplication.adapters.StudentAdapter
import com.example.myapplication.data.ClubMember
import com.example.myapplication.data.Student
import com.example.myapplication.databinding.FragmentMemberRequestsBinding
import com.example.myapplication.ui.viewmodel.AppViewModel
import com.google.android.material.tabs.TabLayout
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

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) { refreshList() }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        vm.students.observe(viewLifecycleOwner) { students ->
            studentMap = students.associateBy { it.userId }
            refreshList()
        }

        vm.clubRequests.observe(viewLifecycleOwner) { refreshList() }
        vm.clubMembers.observe(viewLifecycleOwner) { refreshList() }

        vm.myClub.observe(viewLifecycleOwner) { club ->
            club?.let { 
                vm.loadClubRequests(it.id!!)
            }
        }

        if (vm.myClub.value == null) vm.loadMyClub()
        vm.loadAllStudents()
    }

    private fun refreshList() {
        if (binding.tabLayout.selectedTabPosition == 0) {
            showRequests()
        } else {
            showMembers()
        }
    }

    private fun showRequests() {
        val requests = vm.clubRequests.value?.filter { it.status == "pending" || it.status == "interview" } ?: emptyList()
        binding.tvPendingCount.text = "${requests.size} active request(s)"
        binding.tvEmpty.visibility = if (requests.isEmpty()) View.VISIBLE else View.GONE
        
        binding.rvRequests.adapter = ClubRequestAdapter(
            requests    = requests,
            studentMap  = studentMap,
            onAccept    = { req -> 
                AlertDialog.Builder(requireContext())
                    .setTitle("Accept Member")
                    .setMessage("Add this student to the club?")
                    .setPositiveButton("Accept") { _, _ ->
                        vm.acceptClubRequest(req) { success ->
                            Toast.makeText(context, if (success) "Accepted!" else "Error", Toast.LENGTH_SHORT).show()
                        }
                    }.setNegativeButton("Cancel", null).show()
            },
            onReject    = { req -> 
                AlertDialog.Builder(requireContext())
                    .setTitle("Reject Request")
                    .setMessage("Reject this join request?")
                    .setPositiveButton("Reject") { _, _ ->
                        vm.rejectClubRequest(req) { success ->
                            Toast.makeText(context, if (success) "Rejected" else "Error", Toast.LENGTH_SHORT).show()
                        }
                    }.setNegativeButton("Cancel", null).show()
            },
            onInterview = { req -> showInterviewDialog(req) }
        )
    }

    private fun showMembers() {
        val members = vm.clubMembers.value ?: emptyList()
        binding.tvPendingCount.text = "${members.size} active member(s)"
        binding.tvEmpty.visibility = if (members.isEmpty()) View.VISIBLE else View.GONE

        val memberStudents = members.mapNotNull { studentMap[it.studentId] }
        binding.rvRequests.adapter = StudentAdapter(memberStudents) { student ->
            AlertDialog.Builder(requireContext())
                .setTitle("Kick Member")
                .setMessage("Remove ${student.name} from the club?")
                .setPositiveButton("Kick") { _, _ ->
                    val clubId = vm.myClub.value?.id ?: return@setPositiveButton
                    vm.kickClubMember(clubId, student.userId) { success ->
                        Toast.makeText(context, if (success) "Member removed" else "Error", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancel", null).show()
        }
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
                            Toast.makeText(context, if (success) "Scheduled!" else "Error", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()

            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
