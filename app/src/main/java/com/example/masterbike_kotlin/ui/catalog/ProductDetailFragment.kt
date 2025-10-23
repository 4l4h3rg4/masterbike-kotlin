package com.example.masterbike_kotlin.ui.catalog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import coil.load
import com.example.masterbike_kotlin.R
import com.example.masterbike_kotlin.databinding.FragmentProductDetailBinding
import com.example.masterbike_kotlin.viewmodels.ProductDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import java.util.*

@AndroidEntryPoint
class ProductDetailFragment : Fragment() {

    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProductDetailViewModel by viewModels()
    private val args: ProductDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadProductDetails()
        setupClickListeners()
        observeViewModel()
    }

    private fun loadProductDetails() {
        viewModel.loadProduct(args.productId)
    }

    private fun setupClickListeners() {
        binding.btnAddToCart.setOnClickListener {
            val quantity = binding.quantityPicker.value
            viewModel.addToCart(quantity)
        }

        binding.btnIncreaseQuantity.setOnClickListener {
            val currentValue = binding.quantityPicker.value
            if (currentValue < 99) {
                binding.quantityPicker.value = currentValue + 1
            }
        }

        binding.btnDecreaseQuantity.setOnClickListener {
            val currentValue = binding.quantityPicker.value
            if (currentValue > 1) {
                binding.quantityPicker.value = currentValue - 1
            }
        }
    }

    private fun observeViewModel() {
        viewModel.product.observe(viewLifecycleOwner) { product ->
            product?.let { displayProduct(it) }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }

        viewModel.addToCartSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Producto agregado al carrito", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun displayProduct(product: com.example.masterbike_kotlin.data.models.Product) {
        binding.apply {
            // Product image
            ivProductImage.load(product.imageUrl) {
                placeholder(R.drawable.ic_launcher_background)
                error(R.drawable.ic_launcher_background)
                crossfade(true)
            }

            // Product name
            tvProductName.text = product.name

            // Product price
            tvProductPrice.text = formatPrice(product.price)

            // Product description
            tvProductDescription.text = product.description ?: "Sin descripción disponible"

            // Product category
            tvProductCategory.text = when (product.category) {
                com.example.masterbike_kotlin.data.models.ProductCategory.BICYCLE -> "🚴 Bicicleta"
                com.example.masterbike_kotlin.data.models.ProductCategory.PART -> "⚙️ Repuesto"
            }

            // Stock status
            tvStockStatus.text = if (product.stock > 0) {
                "En stock (${product.stock} disponibles)"
            } else {
                "Agotado"
            }
            tvStockStatus.setTextColor(
                requireContext().getColor(
                    if (product.stock > 0) R.color.green else R.color.red
                )
            )

            // Enable/disable add to cart button
            btnAddToCart.isEnabled = product.stock > 0
            btnAddToCart.alpha = if (product.stock > 0) 1.0f else 0.5f

            // Set max quantity based on stock
            binding.quantityPicker.maxValue = minOf(product.stock, 99)
            binding.quantityPicker.minValue = 1
        }
    }

    private fun formatPrice(price: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
        return format.format(price)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}