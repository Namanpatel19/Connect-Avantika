package com.example.myapplication.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.data.User
import com.example.myapplication.databinding.ItemDeanBinding
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class UserAdapter(
    private val users: List<User>
) : RecyclerView.Adapter<UserAdapter.VH>() {

    inner class VH(val b: ItemDeanBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemDeanBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = users.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val user = users[position]
        holder.b.tvEmail.text = user.email
        holder.b.tvRole.text = "Role: ${user.role.uppercase()}"
        
        val lastLogin = user.lastSignInAt?.let { rawDate ->
            try {
                // Robust parsing for ISO 8601 strings from Supabase Auth
                val date = if (rawDate.endsWith("Z")) {
                    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
                    sdf.timeZone = TimeZone.getTimeZone("UTC")
                    sdf.parse(rawDate.replace("Z", ""))
                } else {
                    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX", Locale.US)
                    sdf.parse(rawDate)
                }

                if (date != null) {
                    val formatter = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                    "Last Login: ${formatter.format(date)}"
                } else {
                    "Last Login: $rawDate"
                }
            } catch (e: Exception) {
                "Last Login: $rawDate"
            }
        } ?: "Last Login: Never"
        
        holder.b.tvLastLogin.text = lastLogin
        holder.b.btnDelete.visibility = android.view.View.GONE // Audit only view
        holder.b.tvInitials.text = user.email.firstOrNull()?.uppercase() ?: "U"
    }
}
