package com.dev.audiostreamingdemo

import android.Manifest
import android.R
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.dev.audiostreamingdemo.databinding.ActivityStreamingBinding
import com.dev.audiostreamingdemo.stream_core.EngineConfig
import com.dev.audiostreamingdemo.stream_core.StreamCore
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine


class StreamingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStreamingBinding
    private var mRtcEngine: RtcEngine? = null
    private var userRole = 0
    private var mCallEnd = false
    private var engineConfig = EngineConfig()
//    private var eventHandler = EventHandler(this)
    lateinit var streamCore: StreamCore
    companion object {
        const val PERMISSION_REQ_ID_RECORD_AUDIO = 22
        const val TAG = "agora"
    }

    private val mRtcEventHandler = object : IRtcEngineEventHandler() {
        // Listen for the onJoinChannelSuccess callback.
        // This callback occurs when the local user successfully joins the channel.
        override fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
            runOnUiThread {
                Log.i(
                    "agora",
                    "Join channel success, uid: $uid"
                )
            }
        }

        // Listen for the onUserOffline callback.
        // This callback occurs when the host leaves the channel or drops offline.
        override fun onUserOffline(uid: Int, reason: Int) {
            mCallEnd = true
            runOnUiThread {
                Log.i("agora", "User offline, uid: $uid")
//                    onRemoteUserLeft()
            }
        }
    }

    /*inner class EventHandler(context: Context) : IRtcEngineEventHandler() {
        override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
            super.onJoinChannelSuccess(channel, uid, elapsed)
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            super.onUserOffline(uid, reason)
        }
        fun getIrtcEventHandler(): IRtcEngineEventHandler{
            return this
        }
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, com.dev.audiostreamingdemo.R.layout.activity_streaming)
//        streamCore = StreamCore(this)
        val intent = intent
        userRole = intent.getIntExtra(Constants.USER_ROLE_TAG, 2)
        engineConfig.mClientRole = userRole
//        streamCore.configEngine(userRole)
        engineConfig.mChannel = "6"

        initializeEngine()
        setChannelProfile()
        setClientRole(userRole)
        joinChannel()

        /*if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO)) {

            if (engineConfig.mClientRole == 1 ) {
                Log.e("Stream", "Broadcaster Detected, Role = ${engineConfig.mClientRole}")
                streamCore.initRtcEngineEventHandler(eventHandler.getIrtcEventHandler())
                streamCore.joinChannel(engineConfig.mChannel, engineConfig.mUid, Constants.TEMP_TOKEN)
            }
        }*/




        with(binding) {
            bottomActionEndCall.setOnClickListener {
                streamCore.leaveChannel(engineConfig.mChannel)
                engineConfig.reset()
            }
            switchSpeakerId.setOnClickListener {
                streamCore.onSwitchSpeakerClicked()
            }
        }

         /*with(binding) {
             endStreamingBtn.setOnClickListener {
                 leaveChannel()
                 changeActivity()
             }
         }

        initializeEngine()
        setChannelProfile()
        setClientRole(userRole)
        Log.d("agora", "User Role: $userRole")
        mRtcEngine?.enableAudio()
        if (userRole == 1) {
            mRtcEngine?.muteAllRemoteAudioStreams(true)
            mRtcEngine?.muteLocalAudioStream(false)
            Log.d("agora", "Audio Enabled: for User Role $userRole")
        }
        joinChannel()
        *//*if (userRole == 1) {
            //Broadcaster
            Log.d("agora", "User Role Broadcaster")
            setClientRole(IRtcEngineEventHandler.ClientRole.CLIENT_ROLE_BROADCASTER)
        } else {
            //Receiver
            Log.d("agora", "User Role Receiver")
            setClientRole(IRtcEngineEventHandler.ClientRole.CLIENT_ROLE_AUDIENCE)
            joinChannel()
        }*/

    }

   /* private fun initAgoraEngineAndJoinChannel() {
        initializeAgoraEngine()
        joinChannel()
    }

    private fun initializeAgoraEngine() {
        mRtcEngine = try {
            RtcEngine.create(baseContext, "", mRtcEventHandler)
        } catch (e: Exception) {
            Log.e(TAG, Log.getStackTraceString(e))
            throw RuntimeException(
                """
                    NEED TO check rtc sdk init fatal error
                    ${Log.getStackTraceString(e)}
                    """.trimIndent()
            )
        }
    }*/

    fun checkSelfPermission(permission: String, requestCode: Int): Boolean {
        Log.i("Permission", "checkSelfPermission $permission $requestCode")
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




    private fun initializeEngine() {
        mRtcEngine = try {
            RtcEngine.create(baseContext, getString(com.dev.audiostreamingdemo.R.string.app_id), mRtcEventHandler)
        } catch (e: Exception) {
            Log.e("agora", Log.getStackTraceString(e))
            throw RuntimeException(
                """
                    NEED TO check rtc sdk init fatal error
                    ${Log.getStackTraceString(e)}
                    """.trimIndent()
            )
        }
    }

    private fun setChannelProfile() {
        mRtcEngine!!.setChannelProfile(io.agora.rtc.Constants.CHANNEL_PROFILE_LIVE_BROADCASTING)
    }

    private fun setClientRole(clientRole: Int) {
        mRtcEngine?.setClientRole(clientRole)
    }


    private fun joinChannel() {
        // Join a channel with a token.
        val tempToken = getString(com.dev.audiostreamingdemo.R.string.agora_access_token)
        mRtcEngine!!.joinChannel(tempToken, "6",  null, 0)
    }

    private fun leaveChannel() {
        // Leave the current channel.
        mRtcEngine!!.leaveChannel()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!mCallEnd) {
            leaveChannel();
        }
        RtcEngine.destroy();
        mRtcEngine = null
    }

    private fun changeActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }




}