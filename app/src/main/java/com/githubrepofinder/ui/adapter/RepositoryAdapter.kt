package com.githubrepofinder.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.githubrepofinder.databinding.ItemRepositoryBinding
import com.githubrepofinder.model.GHRepo

class RepositoryAdapter(private val onItemClick: (GHRepo) -> Unit) :
    ListAdapter<GHRepo, RepositoryAdapter.ViewHolder>(RepoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRepositoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemRepositoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(repo: GHRepo) {
            binding.apply {
                textId.text = repo.id.toString()
                textName.text = repo.name
                textOwner.text = repo.ownerLogin
                textDescription.text = repo.description ?: "No description"
                textLanguage.text = repo.language ?: "Unknown language"
                textStars.text = "⭐ ${repo.stars}"
            }
        }
    }

    private class RepoDiffCallback : DiffUtil.ItemCallback<GHRepo>() {
        override fun areItemsTheSame(oldItem: GHRepo, newItem: GHRepo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GHRepo, newItem: GHRepo): Boolean {
            return oldItem == newItem
        }
    }
}