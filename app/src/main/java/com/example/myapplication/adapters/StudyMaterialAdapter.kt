package com.example.myapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.data.Faculty
import com.example.myapplication.data.StudyMaterial
import com.example.myapplication.databinding.ItemStudyMaterialBinding

class StudyMaterialAdapter(
    private var materials: List<StudyMaterial>,
    private var faculties: List<Faculty> = emptyList(),
    private val canManage: Boolean = false,
    private val onViewClick: (StudyMaterial) -> Unit = {},
    private val onDeleteClick: (StudyMaterial) -> Unit = {}
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
            chipBatch.text = m.batch?.takeIf { it.isNotBlank() } ?: "All Batches"
            chipDept.text = m.department?.takeIf { it.isNotBlank() } ?: "All Depts"
            
            val faculty = faculties.find { it.userId == m.uploadedBy }
            tvUploadedBy.text = "Uploaded by: ${faculty?.name ?: "Faculty"}"
            
            btnView.visibility = View.VISIBLE
            btnDelete.visibility = if (canManage) View.VISIBLE else View.GONE

            btnView.setOnClickListener { onViewClick(m) }
            btnDelete.setOnClickListener { onDeleteClick(m) }

            root.setOnClickListener {
                onViewClick(m)
            }
        }
    }

    fun update(list: List<StudyMaterial>, facultyList: List<Faculty> = faculties) {
        materials = list
        faculties = facultyList
        notifyDataSetChanged()
    }
}
