package com.jollyes.barcodereaderdemo

import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.adyen.pos.android.barcodereader.Barcode
import com.adyen.pos.android.barcodereader.BarcodeListener
import com.adyen.pos.android.barcodereader.BarcodeReadingTask
import com.hsm.barcode.DecoderConfigValues
import com.hsm.barcode.SymbologyConfig
import com.jollyes.barcodereaderdemo.databinding.ActivityMainBinding
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions


class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "onCreate: ${getDeviceDetails()}")

        binding.tvDeviceDetails.text = getDeviceDetails()

        binding.btnScan.setOnClickListener {
            updatedBarcodeScanner()
//            if (Build.BRAND.lowercase() == "adyen" || Build.MODEL.lowercase() == "s1e") {
//            } else {
//                Toast.makeText(
//                    this@MainActivity,
//                    "This is not an Adyen Device. Please use this app on Adyen device to test the library.",
//                    Toast.LENGTH_LONG
//                ).show()
//            }
        }
        Log.d(TAG, "onCreate: System Details - ${getSystemDetail()}")
    }

    private fun updatedBarcodeScanner() {

        // Create BarcodeReadingTask with symbology, e.g. SYM_ALL or SYM_QR
        val barcodeReadingTask = BarcodeReadingTask(
            listOf(SymbologyConfig(DecoderConfigValues.SymbologyID.SYM_EAN13)
                .apply {
                    Mask = DecoderConfigValues.SymbologyFlags.SYM_MASK_FLAGS
                    Flags = DecoderConfigValues.SymbologyFlags.SYMBOLOGY_ENABLE or
                            DecoderConfigValues.SymbologyFlags.SYMBOLOGY_CHECK_TRANSMIT or
                            DecoderConfigValues.SymbologyFlags.SYMBOLOGY_CHECK_ENABLE
                })
        )

        // Setup listener
        val barcodeListener = object : BarcodeListener {
            override fun onBarcodeRead(barcode: Barcode): Boolean {
                // Handle barcode and return whether scanning should continue
                Log.d(TAG, "onBarcodeRead: ${barcode.data}")
                binding.tvScanResult.text = "On Barcode Read Success - ${barcode.data}"
                return true
            }

            override fun onError(e: Exception) {
                //  Handle error
                Log.d(TAG, "onError: ${e.localizedMessage}")
                binding.tvScanResult.text = "On Barcode Read Error - ${e.localizedMessage}"
            }
        }

        if (binding.btnScan.text == "Open Scanner") {
            binding.btnScan.text = "Close Scanner"
            barcodeReadingTask.start(barcodeListener)
        } else {
            binding.btnScan.text = "Open Scanner"
            barcodeReadingTask.stop()
        }
    }


    private fun getSystemDetail(): String {
        try {
            return "Brand: ${Build.BRAND} \n" +
                    ("DeviceID: " + Settings.Secure.getString(
                        contentResolver,
                        Settings.Secure.ANDROID_ID
                    ) + " \n") +
                    "Model: ${Build.MODEL} \n" +
                    "ID: ${Build.ID} \n" +
                    "SDK: ${Build.VERSION.SDK_INT} \n" +
                    "Manufacture: ${Build.MANUFACTURER} \n" +
                    "Brand: ${Build.BRAND} \n" +
                    "User: ${Build.USER} \n" +
                    "Type: ${Build.TYPE} \n" +
                    "Base: ${Build.VERSION_CODES.BASE} \n" +
                    "Incremental: ${Build.VERSION.INCREMENTAL} \n" +
                    "Board: ${Build.BOARD} \n" +
                    "Host: ${Build.HOST} \n" +
                    "FingerPrint: ${Build.FINGERPRINT} \n" +
                    "Version Code: ${Build.VERSION.RELEASE}"
        } catch (e: Exception) {
            return e.toString()
        }
    }

    private fun getDeviceDetails(): String {
        return try {
            "Brand: ${Build.BRAND} \n\n" +
                    ("DeviceID: " + Settings.Secure.getString(
                        contentResolver,
                        Settings.Secure.ANDROID_ID
                    ) + " \n\n") +
                    "Model: ${Build.MODEL} \n\n" +
                    "ID: ${Build.ID} \n\n" +
                    "SDK: ${Build.VERSION.SDK_INT} \n\n" +
                    "Manufacture: ${Build.MANUFACTURER} \n\n"
        } catch (e: Exception) {
            e.toString()
        }
    }

    fun openCustomZxingScanner() {
        zxingActivityResultLauncher.launch(
            ScanContract().createIntent(
                this@MainActivity,
                ScanOptions()
                    .setBeepEnabled(true)
                    .setCaptureActivity(ZxingActivity::class.java)
                    .setOrientationLocked(true)
                    .setPrompt("Scan Barcode")
            )
        )

    }

    private lateinit var binding: ActivityMainBinding
    private val zxingActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

            val intentResult = ScanIntentResult.parseActivityResult(it.resultCode, it.data)

            if (intentResult.contents == null) {
                Toast.makeText(
                    baseContext,
                    "Cancelled",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    baseContext,
                    intentResult.contents,
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
}