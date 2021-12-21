package com.udacity

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val url = intent.getStringExtra(DOWNLOADED_URL)
        val bSuccess = intent.getBooleanExtra(DOWNLOAD_STATUS, true)
        tv_url.text = url
        tv_status.text = if (bSuccess) getString(R.string.status_success) else
            getString(R.string.status_fail)
        if (bSuccess) {
            tv_status.setBackgroundColor(Color.GREEN)
            img_status.setImageResource(R.drawable.ic_download_success)
        }
        else {
            tv_status.setBackgroundColor(Color.RED)
            img_status.setImageResource(R.drawable.ic_download_failed)
        }

        bt_close.setOnClickListener{
            finish()
        }
    }

    companion object {
        const val DOWNLOADED_URL = "DOWNLOAD_URL"
        const val DOWNLOAD_STATUS = "DOWNLOAD_STATUS"
    }

}
