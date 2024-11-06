package com.fireeemaan.journapp.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fireeemaan.journapp.database.story.StoryEntity
import com.fireeemaan.journapp.databinding.ItemListBinding
import com.fireeemaan.journapp.utils.Utils.formatDateTime

class ListStoryAdapter(
    private val onItemClicked: (String) -> Unit
) : ListAdapter<StoryEntity, ListStoryAdapter.ListViewHolder>(DIFF_CALLBACK) {
    inner class ListViewHolder(private val binding: ItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("ClickableViewAccessibility")
        fun bind(story: StoryEntity) {
            with(binding) {
                tvItemName.text = story.name
                tvItemTime.text = formatDateTime(story.createdAt)
                tvItemDescription.text = story.description

                Glide.with(root.context).load(story.photoUrl).into(binding.ivItemPhoto)
                root.setOnClickListener {
                    onItemClicked(story.id)
                }

                root.setOnTouchListener { v, event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            v.animate().scaleX(0.96f).scaleY(0.96f).setDuration(100).start()
                        }

                        MotionEvent.ACTION_UP -> {
                            v.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(100)
                                .start()
                            v.performClick()
                        }

                        MotionEvent.ACTION_CANCEL -> {
                            v.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
                        }
                    }
                    false
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val story = getItem(position)
        holder.bind(story)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryEntity>() {
            override fun areItemsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem == newItem
            }

        }
    }
}