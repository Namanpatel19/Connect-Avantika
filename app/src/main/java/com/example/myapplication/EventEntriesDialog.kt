package com.example.myapplication

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapters.EventEntriesAdapter
import com.example.myapplication.data.Event
import com.example.myapplication.databinding.DialogEventEntriesBinding
import com.example.myapplication.ui.viewmodel.AppViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.io.File
import java.io.FileOutputStream

class EventEntriesDialog(private val event: Event) : BottomSheetDialogFragment() {

    private var _binding: DialogEventEntriesBinding? = null
    private val binding get() = _binding!!
    private lateinit var vm: AppViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        DialogEventEntriesBinding.inflate(inflater, container, false).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(requireActivity())[AppViewModel::class.java]

        binding.tvEventTitle.text = event.title
        binding.rvEntries.layoutManager = LinearLayoutManager(context)
        binding.btnClose.setOnClickListener { dismiss() }
        
        binding.btnDownloadPdf.setOnClickListener {
            generatePdf()
        }

        vm.eventRegistrations.observe(viewLifecycleOwner) { registrations ->
            binding.tvCount.text = "${registrations.size} registered"
            binding.tvEmpty.visibility = if (registrations.isEmpty()) View.VISIBLE else View.GONE
            
            binding.rvEntries.adapter = EventEntriesAdapter(registrations, vm.students.value ?: emptyList())
        }

        vm.isLoading.observe(viewLifecycleOwner) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }

        vm.loadEventEntries(event.id!!)
        if (vm.students.value.isNullOrEmpty()) vm.loadAllStudents()
    }

    private fun generatePdf() {
        val registrations = vm.eventRegistrations.value ?: return
        if (registrations.isEmpty()) {
            Toast.makeText(context, "No registrations to export", Toast.LENGTH_SHORT).show()
            return
        }

        val pdfDocument = PdfDocument()
        val paint = Paint()
        val titlePaint = Paint().apply {
            textSize = 20f
            isFakeBoldText = true
        }
        val textPaint = Paint().apply {
            textSize = 14f
        }

        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        canvas.drawText("Event: ${event.title}", 50f, 50f, titlePaint)
        canvas.drawText("Registration List", 50f, 80f, textPaint)
        canvas.drawText("Total: ${registrations.size}", 50f, 100f, textPaint)

        var y = 140f
        canvas.drawText("Name", 50f, y, titlePaint)
        canvas.drawText("Enrollment", 250f, y, titlePaint)
        canvas.drawText("Contact", 400f, y, titlePaint)
        
        y += 30f
        val students = vm.students.value ?: emptyList()
        
        for (reg in registrations) {
            val student = students.find { it.userId == reg.studentId }
            canvas.drawText(student?.name ?: "Unknown", 50f, y, textPaint)
            canvas.drawText(student?.enrollment ?: "N/A", 250f, y, textPaint)
            canvas.drawText(reg.contact ?: student?.contact ?: "N/A", 400f, y, textPaint)
            y += 25f
            
            if (y > 800) break // Simple single page support for now
        }

        pdfDocument.finishPage(page)

        val fileName = "Registrations_${event.title.replace(" ", "_")}.pdf"
        val file = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)

        try {
            pdfDocument.writeTo(FileOutputStream(file))
            Toast.makeText(context, "PDF saved: ${file.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Error saving PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            pdfDocument.close()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
