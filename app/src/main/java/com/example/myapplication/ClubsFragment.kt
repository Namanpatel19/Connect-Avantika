package com.example.myapplication

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapters.ClubAdapter
import com.example.myapplication.databinding.FragmentClubsBinding
import com.example.myapplication.ui.viewmodel.AppViewModel
import com.example.myapplication.data.Club
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
            filterClubs("All")
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
                val query = s.toString().lowercase()
                val filtered = allClubs.filter { it.name.lowercase().contains(query) }
                binding.rvClubs.adapter = ClubAdapter(filtered) { club ->
                    vm.joinClub(club.id ?: "") { success ->
                        android.widget.Toast.makeText(context, if (success) "Join request sent!" else "Error", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        vm.loadClubs()
    }

    private fun filterClubs(category: String) {
        val filtered = if (category == "All") allClubs
        else allClubs.filter { it.name.lowercase().contains(category.lowercase()) }
        binding.rvClubs.adapter = ClubAdapter(filtered) { club ->
            vm.joinClub(club.id ?: "") { success ->
                android.widget.Toast.makeText(context, if (success) "Join request sent!" else "Error", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
