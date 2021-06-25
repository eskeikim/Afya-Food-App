package com.apps.skimani.afyafood.utils
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import com.apps.skimani.afyafood.R
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView
import me.dm7.barcodescanner.zxing.ZXingScannerView.ResultHandler
import java.util.*


class ScannerActivity : Activity(), ResultHandler {
    private var mScannerView: ZXingScannerView? = null
    public override fun onCreate(state: Bundle?) {
        setStatusBarTranslucent(true)
        super.onCreate(state)
        mScannerView = ZXingScannerView(this)
        setContentView(mScannerView)
    }

    protected fun setStatusBarTranslucent(makeTranslucent: Boolean) {
        if (makeTranslucent) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
    }

    public override fun onResume() {
        super.onResume()
        mScannerView!!.setResultHandler(this)
        mScannerView!!.startCamera()
    }

    public override fun onPause() {
        super.onPause()
        mScannerView!!.stopCamera()
    }

    override fun handleResult(rawResult: Result) {
        val result = rawResult.text
        val format = rawResult.barcodeFormat
        Log.e(ScannerActivity.Companion.TAG, "Scanned code: " + rawResult.text)
        Log.e(
            ScannerActivity.Companion.TAG,
            "Scanend code type: " + rawResult.barcodeFormat.toString()
        )
//        Toast.makeText(this,"Food item $rawResult scanned ",Toast.LENGTH_SHORT).show()
        Constants.SCANNED_CODE=rawResult.text
        //Return error
        if (result == null) {
            setResult(RESULT_CANCELED, returnErrorCode(result, format))
            finish()
        }
        if (result!!.isEmpty()) {
            setResult(RESULT_CANCELED, returnErrorCode(result, format))
            finish()
        }

        //Return correct code
        setResult(RESULT_OK, returnCorrectCode(result, format))
        finish()
    }

    private fun returnErrorCode(result: String?, format: BarcodeFormat): Intent {
        val returnIntent = Intent()
        returnIntent.putExtra(
            ScannerActivity.ScannerConstants.Companion.ERROR_INFO,
            resources.getString(R.string.scan_err)
        )
        return returnIntent
    }

    private fun returnCorrectCode(result: String?, format: BarcodeFormat): Intent {
        val returnIntent = Intent()
        returnIntent.putExtra(ScannerActivity.ScannerConstants.Companion.SCAN_RESULT, result)
        if (format == BarcodeFormat.QR_CODE) {
            returnIntent.putExtra(
                ScannerActivity.ScannerConstants.Companion.SCAN_RESULT_TYPE,
                ScannerActivity.ScannerConstants.Companion.QR_SCAN
            )
        } else {
            returnIntent.putExtra(
                ScannerActivity.ScannerConstants.Companion.SCAN_RESULT_TYPE,
                ScannerActivity.ScannerConstants.Companion.BAR_SCAN
            )
        }
        return returnIntent
    }

    fun excludeFormats(item: BarcodeFormat) {
        val defaultFormats = mScannerView!!.formats
        val formats: MutableList<BarcodeFormat> = ArrayList()
        for (format in defaultFormats) {
            if (format != item) {
                formats.add(format)
            }
        }
        mScannerView!!.setFormats(formats)
    }

    interface ScannerConstants {
        companion object {
            const val SCAN_MODES = "SCAN_MODES"
            const val SCAN_RESULT = "SCAN_RESULT"
            const val SCAN_RESULT_TYPE = "SCAN_RESULT_TYPE"
            const val ERROR_INFO = "ERROR_INFO"
            const val BAR_SCAN = 0
            const val QR_SCAN = 1
        }
    }

    companion object {
        const val EXCLUDED_FORMAT = "ExcludedFormat"
        private val TAG = ScannerActivity::class.java.simpleName
    }
}