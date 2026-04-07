package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.myapplication.databinding.FragmentManageAllBinding
import com.google.android.material.tabs.TabLayoutMediator

class ManageAllFragment : Fragment() {
    private var _binding: FragmentManageAllBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentManageAllBinding.inflate(inflater, container, false).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ManagePagerAdapter(this)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Students"
                1 -> "Faculty"
                2 -> "Deans"
                3 -> "Clubs"
                4 -> "Events"
                5 -> "Notices"
                6 -> "Users (Auth)"
                else -> null
            }
        }.attach()
    }

    private inner class ManagePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = 7
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> ManageStudentsDeanFragment()
                1 -> ManageFacultyFragment()
                2 -> ManageDeansFragment()
                3 -> ManageClubsFragment()
                4 -> ManageEventsFragment()
                5 -> ManageAnnouncementsFragment()
                6 -> ManageUsersFragment()
                else -> Fragment()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
