package com.jollyes.barcodereaderdemo

import android.R.attr
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.adyen.pos.android.barcodereader.Barcode
import com.adyen.pos.android.barcodereader.BarcodeListener
import com.adyen.pos.android.barcodereader.BarcodeReadingTask
import com.google.zxing.integration.android.IntentIntegrator
import com.hsm.barcode.DecoderConfigValues


class MainActivity : AppCompatActivity() {
    private var messageFormat: TextView? = null
    private var messageText: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
         messageText = findViewById<TextView>(R.id.messageText)
         messageFormat = findViewById<TextView>(R.id.messageFormat)
        val btnScan = findViewById<Button>(R.id.btnScan)
        btnScan.setOnClickListener {
            openBarCodeReader()
        }
    }

    companion object{
        private const val TAG = "MainActivity"
    }

    private fun barcodeSample() {
        // Create BarcodeReadingTask with symbology, e.g. SYM_ALL or SYM_QR
        val symbology = DecoderConfigValues.SymbologyID.SYM_QR
        val barcodeReadingTask = BarcodeReadingTask(symbology)

        // Setup listener
        val barcodeListener = object : BarcodeListener {
            override fun onBarcodeRead(barcode: Barcode): Boolean {
                // Handle barcode and return whether scanning should continue
                Log.d(TAG, "onBarcodeRead: ${barcode.data}")
                return true
            }

            override fun onError(e: Exception) {
                //  Handle error
                Log.d(TAG, "onError: ${e.toString()}")
            }
        }

        barcodeReadingTask.start(barcodeListener)


    }

    private fun openBarCodeReader(){
        val intentIntegrator = IntentIntegrator(this)
        intentIntegrator.setPrompt("Scan a barcode or QR Code")
        intentIntegrator.setOrientationLocked(true)
        intentIntegrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (intentResult != null) {
            if (intentResult.contents == null) {
                Toast.makeText(baseContext, "Cancelled", Toast.LENGTH_SHORT).show()
            } else {
                messageText?.text = intentResult.contents
                messageFormat?.text = intentResult.formatName
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }

    }
}