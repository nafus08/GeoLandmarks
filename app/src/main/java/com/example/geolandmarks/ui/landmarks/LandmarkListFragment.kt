package com.example.geolandmarks.ui.landmarks

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.geolandmarks.databinding.FragmentLandmarkListBinding
import com.example.geolandmarks.ui.LandmarkViewModel
import com.google.android.gms.location.LocationServices

class LandmarkListFragment : Fragment() {

    private var _binding: FragmentLandmarkListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LandmarkViewModel by activityViewModels()
    private lateinit var adapter: LandmarkAdapter

    private var minScoreFilter = -1000000000.0
    private var isAscending = true

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted, you can proceed with location-related tasks
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLandmarkListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        adapter = LandmarkAdapter { landmark ->
            visitLandmark(landmark.id, landmark.title)
        }
        
        binding.rvLandmarks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvLandmarks.adapter = adapter

        viewModel.allLandmarks.observe(viewLifecycleOwner) { landmarks ->
            updateList(landmarks)
        }

        binding.btnSort.setOnClickListener {
            isAscending = !isAscending
            updateList(viewModel.allLandmarks.value ?: emptyList())
        }

        binding.etMinScore.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                minScoreFilter = s.toString().toDoubleOrNull() ?: -1000000000.0
                updateList(viewModel.allLandmarks.value ?: emptyList())
            }
        })
    }

    private fun updateList(landmarks: List<com.example.geolandmarks.data.local.LandmarkEntity>) {
        val filtered = landmarks.filter { it.score >= minScoreFilter }
        val sorted = if (isAscending) {
            filtered.sortedBy { it.score }
        } else {
            filtered.sortedByDescending { it.score }
        }
        adapter.submitList(sorted)
    }

    private fun visitLandmark(id: Int, title: String) {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                performVisit(id, title)
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun performVisit(id: Int, title: String) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    Toast.makeText(requireContext(), "Visit initiated for $title", Toast.LENGTH_SHORT).show()
                    viewModel.visitLandmark(id, title, location.latitude, location.longitude)
                } else {
                    Toast.makeText(requireContext(), "Unable to get location", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
