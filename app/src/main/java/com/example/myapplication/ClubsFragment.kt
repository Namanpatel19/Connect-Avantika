package com.example.myapplication

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapters.ClubAdapter
import com.example.myapplication.databinding.FragmentClubsBinding
import com.example.myapplication.ui.viewmodel.AppViewModel
import com.example.myapplication.data.Club
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout

class ClubsFragment : Fragment() {
    private var _binding: FragmentClubsBinding? = null
    private val binding get() = _binding!!
    private lateinit var vm: AppViewModel
    private var allClubs: List<Club> = emptyList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentClubsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(requireActivity())[AppViewModel::class.java]

        binding.rvClubs.layoutManager = LinearLayoutManager(context)

        vm.clubs.observe(viewLifecycleOwner) { clubs ->
            allClubs = clubs
            filterClubs(binding.tabLayout.getTabAt(binding.tabLayout.selectedTabPosition)?.text.toString())
        }

        vm.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) { filterClubs(tab.text.toString()) }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterClubs(binding.tabLayout.getTabAt(binding.tabLayout.selectedTabPosition)?.text.toString(), s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        vm.loadClubs()
    }

    private fun filterClubs(category: String, query: String = "") {
        var filtered = if (category == "All") allClubs
        else allClubs.filter { (it.category ?: "").equals(category, ignoreCase = true) }

        if (query.isNotEmpty()) {
            filtered = filtered.filter { it.name.contains(query, ignoreCase = true) }
        }

        val isStudent = vm.userRole == "student"
        val isAdmin = vm.userRole == "super_admin"

        binding.rvClubs.adapter = ClubAdapter(
            clubs = filtered,
            isStudentRole = isStudent,
            isAdminRole = isAdmin,
            onJoinClick = { club ->
                if (!isStudent) {
                    Toast.makeText(context, "Only students can join clubs", Toast.LENGTH_SHORT).show()
                    return@ClubAdapter
                }
                vm.joinClub(club.id ?: "") { success ->
                    val msg = if (success) "Join request sent!" else "Already applied or error"
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            },
            onViewInfoClick = { club ->
                val bundle = Bundle().apply { putString("club_id", club.id) }
                findNavController().navigate(R.id.navigation_club_details, bundle)
            },
            onDeleteClick = { club ->
                if (isAdmin) {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Delete Club")
                        .setMessage("Delete '${club.name}'?  This cannot be undone.")
                        .setPositiveButton("Delete") { _, _ ->
                            vm.deleteClub(club.id ?: "") { success ->
                                Toast.makeText(context, if (success) "Club deleted" else "Error deleting club", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
                }
            }
        )
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
