package com.example.myapplication.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.data.User
import com.example.myapplication.databinding.ItemDeanBinding
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class DeanAdapter(
    private val deans: List<User>,
    private val onDelete: (User) -> Unit
) : RecyclerView.Adapter<DeanAdapter.VH>() {

    inner class VH(val b: ItemDeanBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemDeanBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = deans.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val dean = deans[position]
        holder.b.tvEmail.text = dean.email
        holder.b.tvRole.text = "University Dean"
        
        val lastLogin = dean.lastSignInAt?.let {
            try {
                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
                val formatter = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
                val date = parser.parse(it)
                "Last login: ${formatter.format(date!!)}"
            } catch (e: Exception) {
                "Last login: $it"
            }
        } ?: "Last login: Never"
        
        holder.b.tvLastLogin.text = lastLogin
        holder.b.btnDelete.setOnClickListener { onDelete(dean) }
        holder.b.tvInitials.text = dean.email.firstOrNull()?.uppercase() ?: "D"
    }
}
