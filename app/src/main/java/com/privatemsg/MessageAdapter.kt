package com.privatemsg

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.privatemsg.databinding.ItemMessageBinding

class MessageAdapter(private var messages: List<StoredMessage>) :
    RecyclerView.Adapter<MessageAdapter.VH>() {

    class VH(val binding: ItemMessageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val m = messages[position]
        holder.binding.itemMessageBody.text = m.body
        holder.binding.bubble.setBackgroundResource(
            if (m.isIncoming) R.drawable.bubble_incoming
            else R.drawable.bubble_outgoing
        )
        (holder.binding.bubble.layoutParams as? android.widget.FrameLayout.LayoutParams)?.gravity =
            if (m.isIncoming) Gravity.START else Gravity.END
    }

    override fun getItemCount(): Int = messages.size

    fun updateMessages(newList: List<StoredMessage>) {
        messages = newList
        notifyDataSetChanged()
    }
}
