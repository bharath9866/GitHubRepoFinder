package com.githubrepofinder.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.githubrepofinder.R
import com.githubrepofinder.databinding.ItemRepositoryBinding
import com.githubrepofinder.model.GHRepo

class RepositoryAdapter(private val onItemClick: (GHRepo) -> Unit) :
    ListAdapter<GHRepo, RepositoryAdapter.RepositoryViewHolder>(RepoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepositoryViewHolder {
        val binding =
            ItemRepositoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RepositoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RepositoryViewHolder, position: Int) {
        val repo = getItem(position)
        holder.bind(repo)
    }

    inner class RepositoryViewHolder(private val binding: ItemRepositoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(repo: GHRepo) {
            binding.apply {
                textId.text = repo.id.toString()
                textName.text = repo.name
                textOwner.text = repo.ownerLogin
                textDescription.text =
                    repo.description ?: root.context.getString(R.string.no_description)
                textLanguage.text =
                    repo.language ?: root.context.getString(R.string.unknown_language)
                textStars.text = root.context.getString(R.string.stars_count, repo.stars)
            }

            binding.root.setOnClickListener {
                onItemClick(repo)
            }
        }
    }
}

class RepoDiffCallback : DiffUtil.ItemCallback<GHRepo>() {
    override fun areItemsTheSame(oldItem: GHRepo, newItem: GHRepo): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: GHRepo, newItem: GHRepo): Boolean =
        oldItem == newItem
}