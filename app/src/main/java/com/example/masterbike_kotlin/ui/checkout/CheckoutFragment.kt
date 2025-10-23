package com.example.masterbike_kotlin.ui.checkout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.masterbike_kotlin.R
import com.example.masterbike_kotlin.databinding.FragmentCheckoutBinding
import com.example.masterbike_kotlin.viewmodels.CheckoutViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CheckoutFragment : Fragment() {

    private var _binding: FragmentCheckoutBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CheckoutViewModel by viewModels()

    private lateinit var checkoutAdapter: CheckoutAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCheckoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupPaymentMethods()
        setupClickListeners()
        observeViewModel()
        loadCheckoutData()
    }

    private fun setupRecyclerView() {
        checkoutAdapter = CheckoutAdapter()
        binding.recyclerViewOrderItems.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = checkoutAdapter
        }
    }

    private fun setupPaymentMethods() {
        binding.radioGroupPayment.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_credit_card -> {
                    binding.layoutCreditCard.visibility = View.VISIBLE
                    binding.layoutPaypal.visibility = View.GONE
                }
                R.id.radio_paypal -> {
                    binding.layoutCreditCard.visibility = View.GONE
                    binding.layoutPaypal.visibility = View.VISIBLE
                }
            }
        }
        // Default to credit card
        binding.radioCreditCard.isChecked = true
    }

    private fun setupClickListeners() {
        binding.btnPlaceOrder.setOnClickListener {
            if (validateOrder()) {
                placeOrder()
            }
        }

        binding.btnBackToCart.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun validateOrder(): Boolean {
        var isValid = true

        // Validate shipping address
        if (binding.etShippingAddress.text.toString().trim().isEmpty()) {
            binding.etShippingAddress.error = "Dirección de envío requerida"
            isValid = false
        }

        // Validate payment method
        val paymentMethod = when (binding.radioGroupPayment.checkedRadioButtonId) {
            R.id.radio_credit_card -> "credit_card"
            R.id.radio_paypal -> "paypal"
            else -> null
        }

        if (paymentMethod == null) {
            Toast.makeText(requireContext(), "Selecciona un método de pago", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        // Validate credit card if selected
        if (paymentMethod == "credit_card") {
            if (binding.etCardNumber.text.toString().trim().length < 16) {
                binding.etCardNumber.error = "Número de tarjeta inválido"
                isValid = false
            }
            if (binding.etExpiryDate.text.toString().trim().isEmpty()) {
                binding.etExpiryDate.error = "Fecha de expiración requerida"
                isValid = false
            }
            if (binding.etCvv.text.toString().trim().length < 3) {
                binding.etCvv.error = "CVV inválido"
                isValid = false
            }
        }

        return isValid
    }

    private fun placeOrder() {
        val shippingAddress = binding.etShippingAddress.text.toString().trim()
        val paymentMethod = when (binding.radioGroupPayment.checkedRadioButtonId) {
            R.id.radio_credit_card -> "credit_card"
            R.id.radio_paypal -> "paypal"
            else -> "credit_card"
        }

        viewModel.placeOrder(shippingAddress, paymentMethod)
    }

    private fun observeViewModel() {
        viewModel.orderItems.observe(viewLifecycleOwner) { items ->
            checkoutAdapter.submitList(items)
        }

        viewModel.totalAmount.observe(viewLifecycleOwner) { total ->
            binding.tvOrderTotal.text = String.format("$%.2f", total)
        }

        viewModel.shippingAddress.observe(viewLifecycleOwner) { address ->
            address?.let {
                binding.etShippingAddress.setText(it)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.btnPlaceOrder.isEnabled = !isLoading
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.orderPlaced.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "¡Pedido realizado exitosamente!", Toast.LENGTH_LONG).show()
                // Navigate to order confirmation or back to catalog
                findNavController().navigate(R.id.action_checkoutFragment_to_catalogFragment)
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun loadCheckoutData() {
        viewModel.loadCheckoutData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}