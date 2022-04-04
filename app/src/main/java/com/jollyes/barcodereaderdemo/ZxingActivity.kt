package com.jollyes.barcodereaderdemo

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.ResultPoint
import com.google.zxing.client.android.BeepManager
import com.jollyes.barcodereaderdemo.databinding.ActivityZxingBinding
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DefaultDecoderFactory

class ZxingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityZxingBinding
    private lateinit var beepManager: BeepManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityZxingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        beepManager = BeepManager(this@ZxingActivity)

        binding.apply {

            setupBarcodeUI()

            if (!hasFlash()) {
                flashSwitcherButton.visibility = View.GONE
            }

            flashSwitcherButton.setOnClickListener {
                if (flashSwitcherButton.text == "Turn Flash On") {
                    flashSwitcherButton.text = "Turn Flash Off"
                    zxingBarcodeScanner.setTorchOn()
                } else {
                    flashSwitcherButton.text = "Turn Flash On"
                    zxingBarcodeScanner.setTorchOff()
                }
            }

            openCloseScanner.setOnClickListener {
                if (hasCameraPermission()) {
                    showScannerWithPermissionGranted()
                } else {
                    askMultiplePermissions.launch(
                        arrayOf(
                            android.Manifest.permission.CAMERA,
                            android.Manifest.permission.BLUETOOTH
                        )
                    )
                }

            }
        }

    }

    private fun setupBarcodeUI() {
        binding.apply {
            val formats: Collection<BarcodeFormat> =
                listOf(BarcodeFormat.EAN_13, BarcodeFormat.QR_CODE)
            zxingBarcodeScanner.barcodeView.decoderFactory = DefaultDecoderFactory(formats)
            zxingBarcodeScanner.initializeFromIntent(intent)
            /*zxingBarcodeScanner.cameraSettings = CameraSettings().also {
                it.requestedCameraId = 1
            }*/
            zxingBarcodeScanner.setStatusText("Scan Product Barcode...")
            zxingBarcodeScanner.decodeContinuous(callback)
        }
    }

    private fun showScannerWithPermissionGranted() {
        binding.apply {
            if (openCloseScanner.text == "Open Scanner") {
                onOpenScanner()
            } else {
                onCloseScanner()
            }
        }
    }

    private fun onCloseScanner() {
        binding.apply {
            openCloseScanner.text = "Open Scanner"
            zxingBarcodeScanner.visibility = View.GONE
            zxingBarcodeScanner.pause()
        }
    }

    private fun onOpenScanner() {
        binding.apply {
            openCloseScanner.text = "Close Scanner"
            zxingBarcodeScanner.visibility = View.VISIBLE
            zxingBarcodeScanner.resume()
        }
    }

    private fun hasCameraPermission(): Boolean {
        return checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasBluetoothPermission(): Boolean {
        return checkSelfPermission(android.Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED
    }

    private val askMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { map ->
            var isDenied = false
            for (entry in map.entries) {
                isDenied = entry.value
            }
            if (!isDenied) {
                showScannerWithPermissionGranted()
            }
        }

    private fun hasFlash(): Boolean {
        return applicationContext.packageManager
            .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
    }

    private val callback: BarcodeCallback = object : BarcodeCallback {
        override fun barcodeResult(result: BarcodeResult) {
            Toast.makeText(
                this@ZxingActivity,
                "onBarcodeRead: ${result.result.text}",
                Toast.LENGTH_LONG
            ).show()
            beepManager.playBeepSoundAndVibrate()
            onCloseScanner()
        }

        override fun possibleResultPoints(resultPoints: List<ResultPoint>) {}
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return binding.zxingBarcodeScanner.onKeyDown(keyCode, event) || super.onKeyDown(
            keyCode,
            event
        )
    }
}