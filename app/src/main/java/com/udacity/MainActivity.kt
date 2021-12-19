package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.*
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            download()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
        unregisterReceiver(receiver)
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val action = intent?.action
            val downloadManager = context!!.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

            if (id == downloadID) {
                val query = DownloadManager.Query()
                query.setFilterById(downloadID);
                val cursor = downloadManager.query(query)

                if (cursor.moveToFirst()) {
                    val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    when (status)
                    {
                        DownloadManager.STATUS_SUCCESSFUL ->
                        {
                            //Timber.d("STATUS_SUCCESSFUL")
                            Toast.makeText(this@MainActivity, getString(R.string.download_completed), Toast.LENGTH_LONG).show()
                        }

                        DownloadManager.STATUS_FAILED ->
                        {
                            //Timber.d("STATUS_FAILED")
                            Toast.makeText(this@MainActivity, getString(R.string.download_failed), Toast.LENGTH_LONG).show()
                        }

                        DownloadManager.STATUS_PAUSED->
                        {
                            //Timber.d("STATUS_PAUSED")
                            Toast.makeText(this@MainActivity, getString(R.string.download_paused), Toast.LENGTH_LONG).show()
                        }

                        DownloadManager.STATUS_RUNNING ->
                        {
                            //Timber.d("STATUS_RUNNING")
                        }

                        DownloadManager.STATUS_PENDING ->
                        {
                            //Timber.d("STATUS_PENDING")

                        }

                        else ->
                        {

                        }

                    }
                }
                else
                {
                    Toast.makeText(this@MainActivity, getString(R.string.download_failed), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun radioOnClick(view : View)
    {
        val bSelected = (view as RadioButton).isChecked
        if (bSelected)
        {
            when (view.id)
            {
                R.id.rb_glide ->
                {
                    URL = getString(R.string.url_glide)
                    REPO_NAME = getString(R.string.str_download_glide)
                }

                R.id.rb_udacity ->
                {
                    URL = getString(R.string.url_loadapp)
                    REPO_NAME = getString(R.string.str_download_loadapp)
                }

                R.id.rb_retrofit ->
                {
                    URL = getString(R.string.url_retrofit)
                    REPO_NAME = getString(R.string.str_download_retrofit)
                }
            }
        }
    }

    private fun download() {
        if (URL.isEmpty())
        {
            Toast.makeText(this, getString(R.string.msg_select_repo), Toast.LENGTH_LONG).show()
            return;
        }

        val request =
            DownloadManager.Request(Uri.parse(URL))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID = downloadManager.enqueue(request)// enqueue puts the download request in the queue.


        ////////////////////////////////////
        getDownloadProgress(downloadManager)
/*        val threadWithRunnable = Thread()
        {
            getStatusDownload(downloadManager)
        }
        threadWithRunnable.start()*/

/*        var percentage = 0.0
        var finishDownload = false
        while (!finishDownload) {
            val query = DownloadManager.Query()
            query.setFilterById(downloadID)
            val cursor = downloadManager.query(query)

            try {
                cursor.moveToFirst()
                val status = cursor.getInt(cursor.getColumnIndex((DownloadManager.COLUMN_STATUS)))
                when (status) {
                    DownloadManager.STATUS_FAILED -> {
                        //sendFileDataToDownloadReceiver(title, desc, false, fragContext)
                        Timber.d("STATUS_FAILED")
                        finishDownload = true
                    }

                    DownloadManager.STATUS_RUNNING -> {
                        val total_Download_Size =
                            cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                        val total_downloaded =
                            cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                        if (total_Download_Size != -1) {
                            percentage = (total_downloaded / total_Download_Size.toDouble())
                            var text = "Downloading $percentage%"
                            Timber.d(text)
                        }
                    }
                    DownloadManager.STATUS_SUCCESSFUL -> {
                        //sendFileDataToDownloadReceiver(title, desc, true, fragContext)
                        Timber.d("STATUS_SUCCESSFUL")
                        finishDownload = true
                    }
                }
            } finally {
                cursor.close()
            }
        }*/

        ////////////////////////////////////
    }

    private fun getStatusDownload(downloadManager : DownloadManager)
    {
        var finishDownload = false
        var progress = 0f;

        while (!finishDownload)
        {
            var cursor = downloadManager.query(DownloadManager.Query().setFilterById(downloadID))

            if (cursor.moveToFirst())
            {
                var status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                when(status)
                {
                    DownloadManager.STATUS_FAILED ->
                    {
                        Timber.d("STATUS_FAILED")
                        finishDownload = true
                    }

                    DownloadManager.STATUS_RUNNING ->
                    {
                        var total = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                        if (total >= 0) {
                            var downloaded = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                            progress = downloaded * 1f / total;
                            var percent = (progress*100).toInt()
                            var text = "Downloading $percent%"
                            Timber.d(text)
                            //custom_button.setLoadingButton(text, progress, ButtonState.Loading)
                        }
                    }

                    DownloadManager.STATUS_SUCCESSFUL ->
                    {
                        Timber.d("STATUS_SUCCESSFUL")
                        progress = 1f
                        finishDownload = true;
                    }
                }

            }

        }

    }

    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private fun getDownloadProgress(downloadManager : DownloadManager)
    {
        coroutineScope.launch {
            var finishDownload = false

            while (!finishDownload)
            {
                var cursor = downloadManager.query(DownloadManager.Query().setFilterById(downloadID))

                if (cursor.moveToFirst())
                {
                    var status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                    when (status)
                    {
                        DownloadManager.STATUS_RUNNING ->
                        {
                            var total = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                            if (total >= 0) {
                                var downloaded = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                                var progress = downloaded * 1f / total;
                                var percent = (progress*100).toInt()
                                var text = String.format(getString(R.string.download_progress), percent)
                                //Timber.d(text)
                                withContext(Dispatchers.Main) {
                                    custom_button.setLoadingButton(text, progress, ButtonState.Loading)
                                }

                                delay(100)
                            }
                        }

                        DownloadManager.STATUS_SUCCESSFUL, DownloadManager.STATUS_FAILED ->
                        {
                            finishDownload = true;
                        }
                    }

                }
                cursor.close()
            }

        }
    }


    companion object {
        private var URL = ""
        private var REPO_NAME = ""
        private const val CHANNEL_ID = "channelId"
    }


}
