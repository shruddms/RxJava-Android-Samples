package com.kyungeun.rxjava_android_samples.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kyungeun.rxjava_android_samples.databinding.ItemRepoBinding
import com.kyungeun.rxjava_android_samples.model.Repo
import com.kyungeun.rxjava_android_samples.ui.webview.WebViewActivity

class ReposAdapter(private val listener: RepoItemListener) : RecyclerView.Adapter<RepoViewHolder>() {

    interface RepoItemListener {
        fun onItemClick(item: Repo)
    }

    private val items = ArrayList<Repo>()

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(items: ArrayList<Repo>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
        val binding: ItemRepoBinding = ItemRepoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RepoViewHolder(binding, listener)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) = holder.bind(items[position])
}

class RepoViewHolder(private val itemBinding: ItemRepoBinding, private val listener: ReposAdapter.RepoItemListener) : RecyclerView.ViewHolder(itemBinding.root),
    View.OnClickListener {

    private lateinit var repo: Repo

    init {
        itemBinding.root.setOnClickListener(this)
    }

    @SuppressLint("SetTextI18n")
    fun bind(item: Repo) {
        this.repo = item
        itemBinding.tvName.text = item.name
        itemBinding.tvDesc.text = item.description
    }

    override fun onClick(v: View?) {
        listener.onItemClick(repo)
    }
}

