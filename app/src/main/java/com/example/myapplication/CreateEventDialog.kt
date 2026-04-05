package com.example.myapplication

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.data.Event
import com.example.myapplication.data.User
import com.example.myapplication.databinding.DialogCreateEventBinding
import com.example.myapplication.ui.viewmodel.AppViewModel
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar
import java.util.Locale

class CreateEventDialog : DialogFragment() {
    private var _binding: DialogCreateEventBinding? = null
    private val binding get() = _binding!!
    private lateinit var vm: AppViewModel
    private var selectedBannerUri: Uri? = null
    private var selectedDate: Calendar? = null
    private var deans: List<User> = emptyList()
    private var selectedDeanId: String? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedBannerUri = result.data?.data
            binding.ivBannerPreview.setImageURI(selectedBannerUri)
            binding.ivBannerPreview.visibility = View.VISIBLE
            binding.llUploadPlaceholder.visibility = View.GONE
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        DialogCreateEventBinding.inflate(inflater, container, false).also { _binding = it }.root

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(requireActivity())[AppViewModel::class.java]

        vm.deans.observe(viewLifecycleOwner) { deanList ->
            deans = deanList
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, deanList.map { it.email })
            binding.spinnerDean.setAdapter(adapter)
        }
        vm.loadDeans()

        binding.spinnerDean.setOnItemClickListener { _, _, position, _ ->
            selectedDeanId = deans[position].id
        }

        binding.btnPickBanner.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
        }

        binding.btnPickDate.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, y, m, d ->
                val chosenDate = Calendar.getInstance().apply {
                    set(y, m, d, 0, 0, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                
                val today = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                if (chosenDate.before(today)) {
                    Toast.makeText(context, "Cannot select a past date", Toast.LENGTH_SHORT).show()
                } else {
                    selectedDate = chosenDate
                    val displayDate = "$y-${(m + 1).toString().padStart(2,'0')}-${d.toString().padStart(2,'0')}"
                    binding.tvDate.text = displayDate
                    binding.tvDate.visibility = View.VISIBLE
                    binding.btnPickDate.text = "Change Date: $displayDate"
                }
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        binding.btnSubmit.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            val desc  = binding.etDescription.text.toString().trim()
            val feeStr = binding.etEntryFee.text.toString().trim()
            val fee = feeStr.toDoubleOrNull() ?: 0.0
            
            if (title.isEmpty()) { 
                Toast.makeText(context, "Enter event title", Toast.LENGTH_SHORT).show()
                return@setOnClickListener 
            }
            if (selectedDeanId == null) {
                Toast.makeText(context, "Please select a dean for approval", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (selectedDate == null) {
                Toast.makeText(context, "Please select a date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val clubId = vm.myClub.value?.id 
            val dateStr = binding.tvDate.text.toString()

            val event = Event(
                title       = title,
                description = desc,
                clubId      = clubId,
                createdBy   = vm.userId,
                status      = "pending",
                eventDate   = "${dateStr}T00:00:00",
                deanId      = selectedDeanId,
                entryFee    = fee,
                isPaid      = fee > 0
            )
            
            val bannerFile = selectedBannerUri?.let { uriToTempFile(it) }

            binding.btnSubmit.isEnabled = false
            vm.submitEvent(event, bannerFile) { success ->
                binding.btnSubmit.isEnabled = true
                if (success) {
                    Toast.makeText(context, "Event submitted for dean approval!", Toast.LENGTH_SHORT).show()
                    dismiss()
                } else {
                    Toast.makeText(context, "Failed to submit event", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnCancel.setOnClickListener { dismiss() }
    }

    private fun uriToTempFile(uri: Uri): File? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val tempFile = File.createTempFile("upload_banner", ".jpg", requireContext().cacheDir)
            val outputStream = FileOutputStream(tempFile)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            tempFile
        } catch (e: Exception) {
            null
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
