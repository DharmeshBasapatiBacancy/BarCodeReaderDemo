package com.jollyes.barcodereaderdemo

import android.os.*
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.adyen.pos.android.barcodereader.Barcode
import com.adyen.pos.android.barcodereader.BarcodeListener
import com.adyen.pos.android.barcodereader.BarcodeReadingTask
import com.hsm.barcode.DecoderConfigValues
import com.hsm.barcode.SymbologyConfig
import com.jollyes.barcodereaderdemo.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var handlerThread: HandlerThread
    private lateinit var barcodeListener: BarcodeListener
    private lateinit var mHandler: Handler
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mHandler = Handler(Looper.getMainLooper())//For executing on Main Thread

        handlerThread = HandlerThread("HandlerThread")//For executing on Background Thread
        handlerThread.start()

        binding.tvDeviceDetails.text = getDeviceDetails()

        val barcodeReadingTask = initBarcodeReadingTask()

        barcodeListener = object : BarcodeListener {
            override fun onBarcodeRead(barcode: Barcode): Boolean {
                mHandler.post {
                    binding.tvScanResult.text = "On Barcode Read Success - ${barcode.data}"
                }
                return true
            }

            override fun onError(e: Exception) {
                mHandler.post {
                    binding.tvScanResult.text =
                        "On Barcode Read Error - ${e.localizedMessage}"
                }
            }
        }

        binding.btnScan.setOnClickListener {
            if (barcodeReadingTask.isRunning()) {
                barcodeReadingTask.stop()
                binding.btnScan.text = getString(R.string.label_open_scanner)
            } else {
                barcodeReadingTask.start(barcodeListener)
                binding.btnScan.text = getString(R.string.label_close_scanner)
            }
        }
    }

    private fun initBarcodeReadingTask() = BarcodeReadingTask(
        listOf(SymbologyConfig(DecoderConfigValues.SymbologyID.SYM_EAN13)
            .apply {
                Mask = DecoderConfigValues.SymbologyFlags.SYM_MASK_FLAGS
                Flags = DecoderConfigValues.SymbologyFlags.SYMBOLOGY_ENABLE or
                        DecoderConfigValues.SymbologyFlags.SYMBOLOGY_CHECK_TRANSMIT or
                        DecoderConfigValues.SymbologyFlags.SYMBOLOGY_CHECK_ENABLE
            })
    )


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
}