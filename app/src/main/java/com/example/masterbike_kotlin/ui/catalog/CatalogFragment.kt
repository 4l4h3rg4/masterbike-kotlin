package com.example.masterbike_kotlin.ui.catalog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.masterbike_kotlin.R
import com.example.masterbike_kotlin.data.models.Product
import com.example.masterbike_kotlin.data.models.ProductCategory
import com.example.masterbike_kotlin.databinding.FragmentCatalogBinding
import com.example.masterbike_kotlin.viewmodels.CatalogViewModel
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CatalogFragment : Fragment() {

    private var _binding: FragmentCatalogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CatalogViewModel by viewModels()

    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCatalogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchView()
        setupCategoryChips()
        setupSwipeRefresh()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter(
            onProductClick = { product ->
                // Navigate to product detail
                val action = CatalogFragmentDirections.actionCatalogFragmentToProductDetailFragment(product.id)
                findNavController().navigate(action)
            },
            onAddToCartClick = { product ->
                // Add to cart functionality
                viewModel.addToCart(product.id)
                Toast.makeText(requireContext(), "Agregado al carrito: ${product.name}", Toast.LENGTH_SHORT).show()
            }
        )

        binding.recyclerViewProducts.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = productAdapter
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.searchProducts(query ?: "")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.searchProducts(newText ?: "")
                return true
            }
        })

        binding.searchView.setOnCloseListener {
            viewModel.clearFilters()
            false
        }
    }

    private fun setupCategoryChips() {
        // All products chip
        val allChip = Chip(requireContext()).apply {
            text = "Todos"
            isCheckable = true
            isChecked = true
            setOnClickListener {
                viewModel.filterByCategory(null)
                updateChipSelection(this)
            }
        }

        // Bicycles chip
        val bicycleChip = Chip(requireContext()).apply {
            text = "🚴 Bicicletas"
            isCheckable = true
            setOnClickListener {
                viewModel.filterByCategory(ProductCategory.BICYCLE)
                updateChipSelection(this)
            }
        }

        // Parts chip
        val partsChip = Chip(requireContext()).apply {
            text = "⚙️ Repuestos"
            isCheckable = true
            setOnClickListener {
                viewModel.filterByCategory(ProductCategory.PART)
                updateChipSelection(this)
            }
        }

        binding.chipGroupCategories.apply {
            addView(allChip)
            addView(bicycleChip)
            addView(partsChip)
        }
    }

    private fun updateChipSelection(selectedChip: Chip) {
        for (i in 0 until binding.chipGroupCategories.childCount) {
            val chip = binding.chipGroupCategories.getChildAt(i) as Chip
            chip.isChecked = chip == selectedChip
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadProducts()
        }
    }

    private fun observeViewModel() {
        viewModel.filteredProducts.observe(viewLifecycleOwner) { products ->
            productAdapter.submitList(products)
            binding.tvEmptyState.visibility = if (products.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.swipeRefreshLayout.isRefreshing = isLoading
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}