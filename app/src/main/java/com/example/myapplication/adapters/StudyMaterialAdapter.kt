package com.example.myapplication.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.data.StudyMaterial
import com.example.myapplication.databinding.ItemStudyMaterialBinding

class StudyMaterialAdapter(
    private var materials: List<StudyMaterial>
) : RecyclerView.Adapter<StudyMaterialAdapter.VH>() {

    inner class VH(val b: ItemStudyMaterialBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemStudyMaterialBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = materials.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val m = materials[position]
        with(holder.b) {
            tvMaterialTitle.text = m.title
            tvMaterialSubject.text = m.subject ?: "No subject"
            chipBatch.text = m.batch ?: "All Batches"
            chipDept.text = m.department ?: "All Depts"
            
            // Click to open/download the file
            root.setOnClickListener {
                m.fileUrl?.let { url ->
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        root.context.startActivity(intent)
                    } catch (_: Exception) { }
                }
            }
        }
    }

    fun update(list: List<StudyMaterial>) {
        materials = list
        notifyDataSetChanged()
    }
}
