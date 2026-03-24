package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.databinding.FragmentHomeBinding
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
        
        setupDate()
        loadSupabaseData()
    }

    private fun setupDate() {
        val sdf = SimpleDateFormat("EEEE, d MMMM", Locale.getDefault())
        // In a real app, you'd find the view for the date, but for now let's just use what's in XML
    }

    private fun loadSupabaseData() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // Example of fetching from Supabase
                // val movies = SupabaseClient.client.postgrest["movies"].select().decodeList<Movie>()
                // if (movies.isNotEmpty()) {
                //    binding.tvFeaturedTitle.text = movies[0].title
                //    Glide.with(this@HomeFragment).load(movies[0].cardImageUrl).into(binding.iv_featured_image)
                // }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
