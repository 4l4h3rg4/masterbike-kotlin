package com.example.masterbike_kotlin.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.masterbike_kotlin.databinding.FragmentProfileBinding
import com.example.masterbike_kotlin.viewmodels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        observeViewModel()
        loadUserInfo()
    }

    private fun setupClickListeners() {
        binding.btnLogout.setOnClickListener {
            viewModel.logout()
        }

        binding.btnOrderHistory.setOnClickListener {
            // Navigate to order history
            findNavController().navigate(R.id.action_profileFragment_to_orderHistoryFragment)
        }

        binding.btnAddresses.setOnClickListener {
            // Navigate to addresses management
            findNavController().navigate(R.id.action_profileFragment_to_addressFragment)
        }
    }

    private fun observeViewModel() {
        viewModel.loginState.observe(viewLifecycleOwner) { isLoggedIn ->
            if (!isLoggedIn) {
                // User logged out, navigate to login
                // TODO: Navigate to login fragment
                Toast.makeText(requireContext(), "Sesión cerrada", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.btnLogout.isEnabled = !isLoading
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun loadUserInfo() {
        val user = viewModel.getCurrentUser()
        user?.let {
            binding.tvUserEmail.text = it.email ?: "Sin email"
            binding.tvUserId.text = "ID: ${it.id}"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}