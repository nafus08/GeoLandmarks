package com.example.geolandmarks.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.geolandmarks.databinding.FragmentMapBinding
import com.example.geolandmarks.ui.LandmarkViewModel
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import android.graphics.Color
import androidx.core.content.ContextCompat

class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LandmarkViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.mapView.setTileSource(org.osmdroid.tileprovider.tilesource.XYTileSource(
            "PublicTransport",
            0, 18, 256, ".png", arrayOf(
                "https://tile.memomaps.de/tilegen/"
            ), "© OpenStreetMap contributors"
        ))
        binding.mapView.setMultiTouchControls(true)
        
        val mapController = binding.mapView.controller
        mapController.setZoom(7.5)
        val bangladesh = GeoPoint(23.6850, 90.3563)
        mapController.setCenter(bangladesh)

        viewModel.allLandmarks.observe(viewLifecycleOwner) { landmarks ->
            binding.mapView.overlays.clear()
            landmarks.forEach { landmark ->
                val marker = Marker(binding.mapView)
                marker.position = GeoPoint(landmark.lat, landmark.lng)
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                marker.title = landmark.title
                marker.snippet = "Score: ${landmark.score}"
                
                val color = when {
                    landmark.score < 20 -> Color.RED
                    landmark.score < 50 -> Color.rgb(255, 165, 0) // Orange
                    landmark.score < 80 -> Color.YELLOW
                    else -> Color.GREEN
                }
                
                val icon = ContextCompat.getDrawable(requireContext(), org.osmdroid.library.R.drawable.marker_default)?.mutate()
                icon?.setTint(color)
                marker.icon = icon
                
                binding.mapView.overlays.add(marker)
            }
            binding.mapView.invalidate()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
