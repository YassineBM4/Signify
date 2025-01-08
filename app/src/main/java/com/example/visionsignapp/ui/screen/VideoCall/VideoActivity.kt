package com.example.interactivevideocall

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.TextureView
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
//import com.example.visionsignapp.ui.screen.VideoCall.initializeHandLandmarker
import io.agora.rtc.Constants
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.VideoCanvas
import kotlinx.coroutines.runBlocking

private const val APP_ID = "fde109e4b55b426c95d54b2cc809179e"
private const val TOKEN = "007eJxTYNhmc3VaOFO/QtMH1jT7+4Ktv3eFBn/+YHj12wdXtQ3BciwKDCZmpuapSamppkbJBiZmqRZJicYmScaJyWnmaSbJiUaJN1/XpDcEMjIcP8jKwsgAgSA+O0NwZnpeZlolAwMA8YgiLA=="
private const val TAG = "InteractiveVideoCall"
private val PERMISSIONS = arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA)

class VideoActivity : ComponentActivity() {

    private var mEngine: RtcEngine? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val channelName = intent.getStringExtra("ChannelName") ?: "TestChannel"
        val userRole = intent.getStringExtra("UserRole") ?: "Audience"

        setContent {
            PermissionHandler(permissions = PERMISSIONS) { granted ->
                if (granted) {
                    InteractiveVideoCallScreen(channelName, userRole)
                } else {
                    PermissionDeniedScreen()
                }
            }
        }
        /*runBlocking {
            initializeHandLandmarker(applicationContext)
        }*/
    }

    override fun onDestroy() {
        super.onDestroy()
        mEngine?.leaveChannel()
        RtcEngine.destroy()
    }
}

@Composable
fun InteractiveVideoCallScreen(channelName: String, userRole: String) {
    val context = LocalContext.current
    val activity = context as? Activity
    var remoteUsers by remember { mutableStateOf(mapOf<Int, TextureView?>()) }
    val localSurfaceView = remember { RtcEngine.CreateTextureView(context) }
    var mainUser by remember { mutableStateOf(0) } // 0 represents the local user

    val mEngine = remember {
        RtcEngine.create(context, APP_ID, object : IRtcEngineEventHandler() {
            override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
                Log.d(TAG, "Joined channel: $channel, uid: $uid")
            }

            override fun onUserJoined(uid: Int, elapsed: Int) {
                Log.d(TAG, "User joined: $uid")
                remoteUsers = remoteUsers.toMutableMap().apply { put(uid, null) }
            }

            override fun onUserOffline(uid: Int, reason: Int) {
                Log.d(TAG, "User offline: $uid")
                remoteUsers = remoteUsers.toMutableMap().apply { remove(uid) }
                if (mainUser == uid) {
                    mainUser = 0 // Revert to local if main user goes offline
                }
            }
        }).apply {
            enableVideo()
            setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION)
            setClientRole(
                if (userRole == "Broadcaster") Constants.CLIENT_ROLE_BROADCASTER else Constants.CLIENT_ROLE_AUDIENCE
            )
            joinChannel(TOKEN, channelName, null, 0)
        }
    }

    LaunchedEffect(userRole) {
        if (userRole == "Broadcaster" || userRole == "Audience") {
            mEngine.setupLocalVideo(VideoCanvas(localSurfaceView, Constants.RENDER_MODE_HIDDEN, 0))
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f).fillMaxSize()) {
            if (mainUser == 0) {
                // Show local video in full screen
                AndroidView(factory = { localSurfaceView }, modifier = Modifier.fillMaxSize())
            } else {
                // Show remote user in full screen
                val remoteSurfaceView = remoteUsers[mainUser] ?: RtcEngine.CreateTextureView(context).also {
                    remoteUsers = remoteUsers.toMutableMap().apply { put(mainUser, it) }
                }
                (remoteSurfaceView.parent as? ViewGroup)?.removeView(remoteSurfaceView)
                mEngine.setupRemoteVideo(VideoCanvas(remoteSurfaceView, Constants.RENDER_MODE_HIDDEN, mainUser))
                AndroidView(factory = { remoteSurfaceView }, modifier = Modifier.fillMaxSize())
            }

            // Render remote users as thumbnails (floating overlay)
            RemoteUsersView(remoteUsers, mEngine, Modifier.align(Alignment.TopEnd)) { selectedUserId ->
                mainUser = if (mainUser == selectedUserId) 0 else selectedUserId
            }
        }

        // User controls (audio/video toggle, leave button)
        UserControls(mEngine, activity)
    }
}

@Composable
fun RemoteUsersView(remoteUsers: Map<Int, TextureView?>, mEngine: RtcEngine, modifier: Modifier = Modifier, onUserSelected: (Int) -> Unit) {
    val context = LocalContext.current
    Column(modifier = modifier.padding(8.dp), verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.End) {
        remoteUsers.forEach { (uid, textureView) ->
            val remoteSurfaceView = textureView ?: RtcEngine.CreateTextureView(context).also {
                remoteUsers.toMutableMap()[uid] = it
            }
            (remoteSurfaceView.parent as? ViewGroup)?.removeView(remoteSurfaceView)

            mEngine.setupRemoteVideo(VideoCanvas(remoteSurfaceView, Constants.RENDER_MODE_HIDDEN, uid))

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .padding(bottom = 8.dp)
                    .clickable { onUserSelected(uid) }
            ) {
                AndroidView(factory = { remoteSurfaceView }, modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
fun UserControls(mEngine: RtcEngine, activity: Activity?) {
    var isMuted by remember { mutableStateOf(false) }
    var isVideoDisabled by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            onClick = {
                isMuted = !isMuted
                if (isMuted) mEngine.disableAudio() else mEngine.enableAudio()
            },
            shape = CircleShape,
            colors = ButtonDefaults.outlinedButtonColors(backgroundColor = if (isMuted) Color.Transparent else Color.Transparent)
        ) {
            Icon(if (isMuted) Icons.Rounded.MicOff else Icons.Rounded.Mic, contentDescription = null)
        }
        OutlinedButton(
            onClick = {
                isVideoDisabled = !isVideoDisabled
                if (isVideoDisabled) {
                    mEngine.disableVideo()
                } else {
                    mEngine.enableVideo()
                }
            },
            shape = CircleShape,
            colors = ButtonDefaults.outlinedButtonColors(backgroundColor = if (isVideoDisabled) Color.Transparent else Color.Transparent)
        ) {
            Icon(if (isVideoDisabled) Icons.Rounded.VideocamOff else Icons.Rounded.Videocam, contentDescription = null)
        }
        OutlinedButton(
            onClick = {
                mEngine.leaveChannel()
                activity?.finish()
            },
            shape = CircleShape,
            colors = ButtonDefaults.outlinedButtonColors(backgroundColor = Color.Transparent)
        ) {
            Icon(Icons.Rounded.CallEnd, contentDescription = null)
        }
    }
}

@Composable
fun PermissionHandler(permissions: Array<String>, onPermissionResult: @Composable (Boolean) -> Unit) {
    val context = LocalContext.current
    var permissionsGranted by remember {
        mutableStateOf(permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        })
    }

    if (permissionsGranted) {
        onPermissionResult(true)
    } else {
        val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            permissionsGranted = result.values.all { it }
        }

        SideEffect {
            launcher.launch(permissions)
        }
    }
}

@Composable
fun PermissionDeniedScreen() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize().background(Color.Red)
    ) {
        Text("Permissions Denied. Please grant camera and microphone access.")
    }
}