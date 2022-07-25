package com.dev.audiostreamingdemo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.dev.audiostreamingdemo.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private val PERMISSION_REQ_ID_RECORD_AUDIO = 22
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)


        with(binding) {
            startStreamBtn.setOnClickListener {
                changeActivity(Constants.ROLE_BROADCASTER)
            }
            joinStreamBtn.setOnClickListener {
                changeActivity(Constants.ROLE_RECEIVER)
            }
        }
    }

    fun checkSelfPermission(permission: String, requestCode: Int): Boolean {
        Log.i("PERMISSION", "checkSelfPermission $permission $requestCode")
        if (ContextCompat.checkSelfPermission(
                this,
                permission
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(permission),
                requestCode
            )
            return false
        }
        return true
    }

    private fun changeActivity(userRole: Int) {
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO)) {
            val intent = Intent(this, StreamingActivity::class.java)
            intent.putExtra(Constants.USER_ROLE_TAG, userRole)
            startActivity(intent)
        }else  Toast.makeText(this, "Please Allow Permissions", Toast.LENGTH_SHORT).show()
    }


}