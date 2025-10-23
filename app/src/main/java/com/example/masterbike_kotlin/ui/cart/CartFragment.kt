package com.example.masterbike_kotlin.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.masterbike_kotlin.databinding.FragmentCartBinding
import com.example.masterbike_kotlin.viewmodels.CartViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CartViewModel by viewModels()

    private lateinit var cartAdapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupCheckoutButton()
        observeViewModel()
        loadCartItems()
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            onQuantityChanged = { cartItem, newQuantity ->
                viewModel.updateCartItemQuantity(cartItem.id, newQuantity)
            },
            onRemoveItem = { cartItem ->
                viewModel.removeFromCart(cartItem.id)
            }
        )

        binding.recyclerViewCart.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = cartAdapter
        }
    }

    private fun setupCheckoutButton() {
        binding.btnCheckout.setOnClickListener {
            // Navigate to checkout
            findNavController().navigate(R.id.action_cartFragment_to_checkoutFragment)
        }
    }

    private fun observeViewModel() {
        viewModel.cartItems.observe(viewLifecycleOwner) { cartItems ->
            cartAdapter.submitList(cartItems)
            updateUI(cartItems)
        }

        viewModel.totalAmount.observe(viewLifecycleOwner) { total ->
            binding.tvTotalAmount.text = String.format("$%.2f", total)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun updateUI(cartItems: List<com.example.masterbike_kotlin.data.models.CartItem>) {
        val isEmpty = cartItems.isEmpty()
        binding.apply {
            recyclerViewCart.visibility = if (isEmpty) View.GONE else View.VISIBLE
            layoutTotal.visibility = if (isEmpty) View.GONE else View.VISIBLE
            btnCheckout.visibility = if (isEmpty) View.GONE else View.VISIBLE
            tvEmptyCart.visibility = if (isEmpty) View.VISIBLE else View.GONE
        }
    }

    private fun loadCartItems() {
        viewModel.loadCartItems()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}