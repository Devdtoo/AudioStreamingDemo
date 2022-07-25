package com.dev.audiostreamingdemo.stream_core

import android.content.Context
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.dev.audiostreamingdemo.R
import io.agora.rtc.Constants
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import kotlinx.coroutines.coroutineScope
import java.io.File

class StreamCore(private val mContext: Context) {
//    private var mContext: Context? = null
    private var mRtcEngine: RtcEngine? = null
    private var mEngineConfig: EngineConfig? = null
    private var iRtcEngineEventHandler: IRtcEngineEventHandler? = null
    var mAudioRouting = -1

    fun initRtcEngineEventHandler(iRtc: IRtcEngineEventHandler?) {
        iRtcEngineEventHandler = iRtc
    }

    fun joinChannel(channel: String, uid: Int, token: String?) {
        ensureRtcEngineReadyLock();
        mRtcEngine!!.joinChannel(token, channel, "OpenVCall", uid)
        mEngineConfig!!.mChannel = channel
        Log.d("StreamClass", "joinChannel $channel $uid")
    }

    private fun ensureRtcEngineReadyLock(): RtcEngine? {
        if (mRtcEngine == null) {
//            val appId = mContext!!.getString(R.string.private_app_id)
            val appId: String = mContext.applicationContext.getString(R.string.app_id)
            if (TextUtils.isEmpty(appId)) {
                throw RuntimeException("NEED TO use your App ID, get your own ID at https://dashboard.agora.io/")
            }
            mRtcEngine = try {
                RtcEngine.create(mContext.applicationContext, appId, iRtcEngineEventHandler)
            } catch (e: Exception) {
                //                log.error(Log.getStackTraceString(e));
                Log.e("RtcException", Log.getStackTraceString(e))
                throw RuntimeException(
                    """
                        AppID: $appId
                    NEED TO check rtc sdk init fatal error
                    ${Log.getStackTraceString(e)}
                    """.trimIndent()
                )
            }

            // Sets the channel profile of the Agora RtcEngine.
            // The Agora RtcEngine differentiates channel profiles and applies different optimization algorithms accordingly. For example, it prioritizes smoothness and low latency for a video call, and prioritizes video quality for a video broadcast.
            mRtcEngine?.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING)
            mRtcEngine?.enableAudioVolumeIndication(200, 3, false) // 200 ms
            /*mRtcEngine?.setLogFile(
                Environment.getExternalStorageDirectory()
                    .toString() + File.separator + mContext!!.packageName + "/log/agora-rtc.log"
            )*/
        }
        return mRtcEngine
    }

    fun onSwitchSpeakerClicked() {
//        io.agora.openlive.voice.only.ui.LiveRoomActivity.log.info("onSwitchSpeakerClicked $view $mAudioMuted $mAudioRouting")

        // Enables/Disables the audio playback route to the speakerphone.
        // This method sets whether the audio is routed to the speakerphone or earpiece. After calling this method, the SDK returns the onAudioRouteChanged callback to indicate the changes.
        mRtcEngine?.setEnableSpeakerphone(mAudioRouting != 3)
    }


    fun configEngine(cRole: Int) {
        ensureRtcEngineReadyLock();
        mEngineConfig!!.mClientRole = cRole
        mRtcEngine!!.setClientRole(cRole)
        Log.d("Stream", "configEngine $cRole")
    }

    fun leaveChannel(channel: String?) {
        if (mRtcEngine != null) {
            mRtcEngine!!.leaveChannel()
        }
        mEngineConfig!!.reset()
        Log.d("Stream", "leaveChannel $channel")
    }
}