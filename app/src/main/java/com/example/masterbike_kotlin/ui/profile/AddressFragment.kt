package com.example.masterbike_kotlin.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.masterbike_kotlin.databinding.FragmentAddressBinding
import com.example.masterbike_kotlin.viewmodels.AddressViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddressFragment : Fragment() {

    private var _binding: FragmentAddressBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddressViewModel by viewModels()

    private lateinit var addressAdapter: AddressAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupFab()
        observeViewModel()
        loadAddresses()
    }

    private fun setupRecyclerView() {
        addressAdapter = AddressAdapter(
            onEditClick = { address ->
                // TODO: Show edit dialog
                Toast.makeText(requireContext(), "Editar dirección", Toast.LENGTH_SHORT).show()
            },
            onDeleteClick = { address ->
                showDeleteConfirmationDialog(address)
            },
            onSetDefaultClick = { address ->
                viewModel.setDefaultAddress(address.id)
            }
        )

        binding.recyclerViewAddresses.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = addressAdapter
        }
    }

    private fun setupFab() {
        binding.fabAddAddress.setOnClickListener {
            showAddAddressDialog()
        }
    }

    private fun showAddAddressDialog() {
        val dialogView = layoutInflater.inflate(com.example.masterbike_kotlin.R.layout.dialog_add_address, null)
        val streetEdit = dialogView.findViewById<android.widget.EditText>(com.example.masterbike_kotlin.R.id.et_street)
        val cityEdit = dialogView.findViewById<android.widget.EditText>(com.example.masterbike_kotlin.R.id.et_city)
        val stateEdit = dialogView.findViewById<android.widget.EditText>(com.example.masterbike_kotlin.R.id.et_state)
        val zipCodeEdit = dialogView.findViewById<android.widget.EditText>(com.example.masterbike_kotlin.R.id.et_zip_code)
        val countryEdit = dialogView.findViewById<android.widget.EditText>(com.example.masterbike_kotlin.R.id.et_country)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Agregar Dirección")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val street = streetEdit.text.toString().trim()
                val city = cityEdit.text.toString().trim()
                val state = stateEdit.text.toString().trim()
                val zipCode = zipCodeEdit.text.toString().trim()
                val country = countryEdit.text.toString().trim()

                if (validateAddress(street, city, state, zipCode, country)) {
                    viewModel.addAddress(street, city, state, zipCode, country)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showDeleteConfirmationDialog(address: com.example.masterbike_kotlin.data.models.Address) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Eliminar Dirección")
            .setMessage("¿Estás seguro de que quieres eliminar esta dirección?")
            .setPositiveButton("Eliminar") { _, _ ->
                viewModel.deleteAddress(address.id)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun validateAddress(street: String, city: String, state: String, zipCode: String, country: String): Boolean {
        var isValid = true

        if (street.isEmpty()) {
            Toast.makeText(requireContext(), "La calle es requerida", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        if (city.isEmpty()) {
            Toast.makeText(requireContext(), "La ciudad es requerida", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        if (state.isEmpty()) {
            Toast.makeText(requireContext(), "El estado es requerido", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        if (zipCode.isEmpty()) {
            Toast.makeText(requireContext(), "El código postal es requerido", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        if (country.isEmpty()) {
            Toast.makeText(requireContext(), "El país es requerido", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        return isValid
    }

    private fun observeViewModel() {
        viewModel.addresses.observe(viewLifecycleOwner) { addresses ->
            addressAdapter.submitList(addresses)
            binding.tvEmptyAddresses.visibility = if (addresses.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }

        viewModel.addressAdded.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Dirección agregada exitosamente", Toast.LENGTH_SHORT).show()
                loadAddresses()
            }
        }

        viewModel.addressDeleted.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Dirección eliminada", Toast.LENGTH_SHORT).show()
                loadAddresses()
            }
        }

        viewModel.defaultAddressSet.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Dirección predeterminada actualizada", Toast.LENGTH_SHORT).show()
                loadAddresses()
            }
        }
    }

    private fun loadAddresses() {
        viewModel.loadAddresses()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}