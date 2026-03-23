package com.example.myapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.ItemNoticeBinding
import com.example.myapplication.models.Notice

class NoticeAdapter(
    private var notices: List<Notice>
) : RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder>() {

    class NoticeViewHolder(val binding: ItemNoticeBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        val binding = ItemNoticeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoticeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        val notice = notices[position]
        holder.binding.noticeTitle.text = notice.title
        holder.binding.noticeDescription.text = notice.description
        holder.binding.noticeDate.text = "${notice.date} | ${notice.category}"

        if (notice.isImportant) {
            holder.binding.noticeCategory.visibility = View.VISIBLE
            holder.binding.noticeCategory.text = "URGENT"
            holder.binding.noticeCategory.setBackgroundColor(
                ContextCompat.getColor(holder.itemView.context, R.color.accent)
            )
            holder.binding.noticeContainer.setBackgroundResource(R.drawable.bg_important_notice)
        } else {
            holder.binding.noticeCategory.visibility = View.GONE
            holder.binding.noticeContainer.setBackgroundResource(0)
        }
    }

    override fun getItemCount() = notices.size

    fun updateNotices(newNotices: List<Notice>) {
        notices = newNotices
        notifyDataSetChanged()
    }
}