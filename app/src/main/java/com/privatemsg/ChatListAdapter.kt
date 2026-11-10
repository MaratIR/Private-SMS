package com.privatemsg

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.privatemsg.databinding.ItemChatBinding

class ChatListAdapter(
    private var chats: List<Chat>,
    private val onChatClick: (Chat) -> Unit
) : RecyclerView.Adapter<ChatListAdapter.VH>() {

    class VH(val binding: ItemChatBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val c = chats[position]
        holder.binding.itemChatName.text = c.displayName ?: c.phoneNumber
        holder.binding.itemChatPhone.text = c.phoneNumber
        holder.itemView.setOnClickListener { onChatClick(c) }
    }

    override fun getItemCount(): Int = chats.size

    fun updateChats(newChats: List<Chat>) {
        chats = newChats
        notifyDataSetChanged()
    }
}
