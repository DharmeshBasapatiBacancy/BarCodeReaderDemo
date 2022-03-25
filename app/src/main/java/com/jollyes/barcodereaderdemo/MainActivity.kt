package com.jollyes.barcodereaderdemo

import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.adyen.pos.android.barcodereader.Barcode
import com.adyen.pos.android.barcodereader.BarcodeListener
import com.adyen.pos.android.barcodereader.BarcodeReadingTask
import com.hsm.barcode.DecoderConfigValues
import com.hsm.barcode.SymbologyConfig

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btnScan = findViewById<Button>(R.id.btnScan)
        btnScan.setOnClickListener {
            if (Build.BRAND.lowercase() == "adyen" || Build.MODEL.lowercase() == "s1e") {
                openBarcodeScanner()
            } else {
                Toast.makeText(
                    this@MainActivity,
                    "This is not an Adyen Device. Please use this app on Adyen device to test the library.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        Log.d(TAG, "onCreate: System Details - ${getSystemDetail()}")
    }

    private fun openBarcodeScanner() {
        // Create BarcodeReadingTask with symbology, e.g. SYM_ALL or SYM_QR
        val symbology = DecoderConfigValues.SymbologyID.SYM_QR
        val barcodeReadingTask = BarcodeReadingTask(listOf(SymbologyConfig(symbology)))

        // Setup listener
        val barcodeListener = object : BarcodeListener {
            override fun onBarcodeRead(barcode: Barcode): Boolean {
                // Handle barcode and return whether scanning should continue
                Log.d(TAG, "onBarcodeRead: ${barcode.data}")
                Toast.makeText(
                    this@MainActivity,
                    "onBarcodeRead: ${barcode.data}",
                    Toast.LENGTH_LONG
                ).show()
                return true
            }

            override fun onError(e: Exception) {
                //  Handle error
                Toast.makeText(this@MainActivity, "onBarcodeRead: $e", Toast.LENGTH_LONG).show()
                Log.d(TAG, "onError: $e")
            }
        }

        barcodeReadingTask.start(barcodeListener)
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
}