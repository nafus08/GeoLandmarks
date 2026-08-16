package com.example.geolandmarks.ui.add

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.geolandmarks.databinding.FragmentAddLandmarkBinding
import com.example.geolandmarks.ui.LandmarkViewModel
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

class AddLandmarkFragment : Fragment() {

    private var _binding: FragmentAddLandmarkBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LandmarkViewModel by activityViewModels()
    private var selectedImageUri: Uri? = null

    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            binding.ivPreview.setImageURI(uri)
            binding.ivPreview.visibility = View.VISIBLE
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            fetchLocation()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddLandmarkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchLocation()

        binding.btnSelectImage.setOnClickListener {
            checkStoragePermissionAndSelectImage()
        }

        binding.btnSubmit.setOnClickListener {
            submitLandmark()
        }
    }

    private fun fetchLocation() {
        val ctx = context ?: return
        if (ActivityCompat.checkSelfPermission(
                ctx,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
            return
        }

        val activity = activity ?: return
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            _binding?.let { b ->
                if (location != null) {
                    b.etLat.setText(location.latitude.toString())
                    b.etLng.setText(location.longitude.toString())
                }
            }
        }
    }

    private fun checkStoragePermissionAndSelectImage() {
        val ctx = context ?: return
        val permission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(ctx, permission) == PackageManager.PERMISSION_GRANTED) {
            selectImageLauncher.launch("image/*")
        } else {
            storagePermissionLauncher.launch(permission)
        }
    }

    private val storagePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            selectImageLauncher.launch("image/*")
        } else {
            context?.let {
                Toast.makeText(it, "Storage permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun submitLandmark() {
        val title = binding.etTitle.text.toString()
        val lat = binding.etLat.text.toString().toDoubleOrNull()
        val lng = binding.etLng.text.toString().toDoubleOrNull()
        val score = binding.etScore.text.toString().toDoubleOrNull()

        if (title.isEmpty() || lat == null || lng == null || score == null || selectedImageUri == null) {
            context?.let {
                Toast.makeText(it, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show()
            }
            return
        }

        val file = getFileFromUri(selectedImageUri!!)
        if (file == null) return
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)

        viewModel.addLandmark(title, lat, lng, score, imagePart) { success ->
            context?.let { ctx ->
                if (success) {
                    Toast.makeText(ctx, "Landmark added successfully", Toast.LENGTH_SHORT).show()
                    clearForm()
                } else {
                    Toast.makeText(ctx, "Failed to add landmark", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getFileFromUri(uri: Uri): File? {
        val ctx = context ?: return null
        val inputStream = ctx.contentResolver.openInputStream(uri)
        val file = File(ctx.cacheDir, "upload_image.jpg")
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        return file
    }

    private fun clearForm() {
        _binding?.let { b ->
            b.etTitle.text.clear()
            b.etScore.text.clear()
            b.ivPreview.visibility = View.GONE
            selectedImageUri = null
            fetchLocation()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
