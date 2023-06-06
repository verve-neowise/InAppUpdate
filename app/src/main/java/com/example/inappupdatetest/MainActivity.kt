package com.example.inappupdatetest

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.example.inappupdatetest.ui.theme.InAppUpdateTestTheme
import java.io.File





class MainActivity : ComponentActivity() {

    private val storagePermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) {
            installAPK()
        }
    }

    private val installPermission = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        Log.d("TAG", "result: ${it.resultCode}")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InAppUpdateTestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Button(onClick = { installAPK() }) {
                            checkAndUpdate()
                        }
                    }
                }
            }
        }
    }

    private fun checkAndUpdate() {
        if (hasStoragePermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (packageManager.canRequestPackageInstalls()) {
                    installAPK()
                } else {
                    requestUnknownSourcesPermission()
                }
            } else {
                installAPK()
            }
        } else {
            storagePermission.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun requestUnknownSourcesPermission() {
        val intent = Intent(ACTION_MANAGE_UNKNOWN_APP_SOURCES)
            .setData(Uri.parse(String.format("package:%s", packageName)))
        installPermission.launch(intent)
    }

    private fun hasStoragePermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun installAPK() {
        val uri = FileProvider.getUriForFile(this, "$packageName.provider", File("/storage/emulated/0/Download/Files/updates-1.4.8.apk"));
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/vnd.android.package-archive")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(intent)
    }
}



