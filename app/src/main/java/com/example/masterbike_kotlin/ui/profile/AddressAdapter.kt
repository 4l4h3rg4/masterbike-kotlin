package com.example.masterbike_kotlin.ui.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.masterbike_kotlin.R
import com.example.masterbike_kotlin.data.models.Address
import com.example.masterbike_kotlin.databinding.ItemAddressBinding

class AddressAdapter(
    private val onEditClick: (Address) -> Unit,
    private val onDeleteClick: (Address) -> Unit,
    private val onSetDefaultClick: (Address) -> Unit
) : ListAdapter<Address, AddressAdapter.AddressViewHolder>(AddressDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val binding = ItemAddressBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AddressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AddressViewHolder(private val binding: ItemAddressBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(address: Address) {
            binding.apply {
                // Address details
                tvAddress.text = "${address.street}\n${address.city}, ${address.state} ${address.zipCode}\n${address.country}"

                // Default address indicator
                if (address.isDefault) {
                    tvDefaultIndicator.visibility = View.VISIBLE
                    tvDefaultIndicator.text = "Dirección predeterminada"
                    tvDefaultIndicator.setTextColor(itemView.context.getColor(R.color.green))
                } else {
                    tvDefaultIndicator.visibility = View.GONE
                }

                // Action buttons
                btnEdit.setOnClickListener { onEditClick(address) }
                btnDelete.setOnClickListener { onDeleteClick(address) }

                // Set as default button (only show if not default)
                if (!address.isDefault) {
                    btnSetDefault.visibility = View.VISIBLE
                    btnSetDefault.setOnClickListener { onSetDefaultClick(address) }
                } else {
                    btnSetDefault.visibility = View.GONE
                }
            }
        }
    }

    class AddressDiffCallback : DiffUtil.ItemCallback<Address>() {
        override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem == newItem
        }
    }
}