package com.dev.audiostreamingdemo.stream_core

data class EngineConfig(
    var mClientRole: Int = 0,
    var mUid: Int = 0,
    var mChannel: String = "") {

    fun reset() {
        mChannel = ""
    }
}
