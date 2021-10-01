package com.navii.streamcamp;

import static org.webrtc.SessionDescription.Type.ANSWER;
import static org.webrtc.SessionDescription.Type.OFFER;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.navii.streamcamp.databinding.ActivitySamplePeerConnectionBinding;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.DataChannel;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;

import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SessionDescription;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.ArrayList;
import java.util.UUID;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;


public class LiveActivity extends AppCompatActivity {

    private WebSocket webSocket;
    private  String SERVER_PATH ="ws://34.70.130.106:8000/";
    private String userName = UUID.randomUUID().toString();;
    private static final String VIDEO_TRACK_ID = "ARDAMSv0";
    private ActivitySamplePeerConnectionBinding binding;
    private EglBase rootEglBase;
    private VideoTrack videoTrackFromCamera;
    public static final int VIDEO_RESOLUTION_WIDTH = 1280;
    public static final int VIDEO_RESOLUTION_HEIGHT = 720;
    public static final int FPS = 30;
    private boolean isInitiator;
    private boolean isChannelReady;
    private boolean isStarted;

    private PeerConnection peerConnection;
    private PeerConnectionFactory factory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sample_peer_connection);

        initializeSurfaceViews();
        initiateSocketConnection();

        initializePeerConnectionFactory();

        createVideoTrackFromCameraAndShowIt();

        initializePeerConnections();

    }

    private void initiateSocketConnection() {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(SERVER_PATH).build();
        webSocket =   client.newWebSocket(request,new SocketListener());
    }
    private  class SocketListener extends WebSocketListener{

        @Override
        public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
            super.onOpen(webSocket, response);

            runOnUiThread(() ->{


                startStreamingVideo();
            });
        }

        @Override
        public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
            super.onClosed(webSocket, code, reason);
        }

        @Override
        public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
            super.onMessage(webSocket, text);

            runOnUiThread(() ->{
                String peerUsername = "";
                String receiver_channel_name="";
                String action="";
                String offer = null;
                JSONObject parsedData = null;
                try{
                    parsedData =new JSONObject(text);
                    peerUsername = (String) parsedData.get("peer");
                    action = (String) parsedData.get("action");
                    Log.d("peer", peerUsername);
                }catch (JSONException e){
                    e.printStackTrace();
                }
                if(action =="new-candidate"){
                    int label =0;
                    String id = "";
                    String scandidate = "";
                    try {

                        JSONObject messageObj =  parsedData.getJSONObject("message");
                        id =   messageObj.getString("id");
                        label =   messageObj.getInt("label");
                        scandidate =   messageObj.getString("candidate");
                        Log.d("offer", offer.toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("TAG", "connectToSignallingServer: receiving candidates");
                    IceCandidate candidate = new IceCandidate(id, label, scandidate);
                    peerConnection.addIceCandidate(candidate);

                }
                Log.d("peerUsername", peerUsername);
                if(peerUsername.equals(userName)){
                    return;
                }
                Log.d("proceed", peerUsername);
                Log.d("proceed action", action);
                try {
                    parsedData =new JSONObject(text);
                    JSONObject messageObj =  parsedData.getJSONObject("message");
                    receiver_channel_name = (String) messageObj.get("receiver_channel_name");
                    Log.d("message", receiver_channel_name);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(action.equals("new-peer")){
                    doCall(receiver_channel_name);

                }
                if(action.equals("new-offer")){

                    try {

                        JSONObject messageObj =  parsedData.getJSONObject("message");
                          offer =   messageObj.getJSONObject("sdp").getString("sdp");


                        Log.d("offer", offer.toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("Test Offer", offer);
                    peerConnection.setRemoteDescription(new SimpleSdpObserver(), new SessionDescription(OFFER,offer));

                    doAnswer(receiver_channel_name,offer);
                    return;

                }

                if(action == "new-answer"){
                    try {

                        JSONObject messageObj =  parsedData.getJSONObject("message");
                        offer =   messageObj.getJSONObject("sdp").getString("sdp");
                        Log.d("offer", offer.toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    peerConnection.setRemoteDescription(new SimpleSdpObserver(), new SessionDescription(ANSWER, offer));


                    return;
                }




            });

        }
    }

    private void doCall(String receiver_channel_name) {
        MediaConstraints sdpMediaConstraints = new MediaConstraints();
        Log.d("  call", "calling");
        peerConnection.createOffer(new SimpleSdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                Log.d("TAG", "onCreateSuccess: ");
                peerConnection.setLocalDescription(new SimpleSdpObserver(), sessionDescription);

                JSONObject sdp = new JSONObject();
                JSONObject msgContent =new JSONObject();
                try{
                    sdp.put("type","offer");
                    sdp.put("sdp",sessionDescription.description);
                    msgContent.put("sdp", sdp );
                    msgContent.put("receiver_channel_name",receiver_channel_name);

                }catch (JSONException e){
                    e.printStackTrace();
                }
                sendSignal("new-offer",msgContent);


            }

            @Override
            public void onCreateFailure(String s) {
                super.onCreateFailure(s);
                Log.d("failure call", s);
            }
        }, sdpMediaConstraints);
    }

    private void doAnswer(String receiver_channel_name, String offer) {
        MediaStream mediaStream = factory.createLocalMediaStream("ARDAMS");
        mediaStream.addTrack(videoTrackFromCamera);
        Log.e("tracks", String.valueOf(mediaStream.videoTracks.size()));

        Log.wtf("PEER",peerConnection.iceConnectionState().toString());
        MediaConstraints sdpMediaConstraints = new MediaConstraints();

        runOnUiThread(() ->{
            Log.d("nandito", "ako");
            peerConnection.createAnswer(new SimpleSdpObserver() {
                @Override
                public void onCreateSuccess(SessionDescription sessionDescription) {
                    peerConnection.setLocalDescription(new SimpleSdpObserver(), sessionDescription);
                    peerConnection.addStream(mediaStream);
                    JSONObject sdp = new JSONObject();
                    JSONObject msgContent =new JSONObject();
                    try{
                        sdp.put("type","answer");
                        sdp.put("sdp",sessionDescription.description);
                        msgContent.put("sdp", sdp );
                        msgContent.put("receiver_channel_name",receiver_channel_name);

                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    Log.d("answer",sessionDescription.description);
                    sendSignal("new-answer",msgContent);
                }

                @Override
                public void onCreateFailure(String s) {
                    super.onCreateFailure(s);
                    Log.e("failed",s);
                }
            }, sdpMediaConstraints);

        });





    }
    private void initializeView() {


    }

    @Override
    public void onStop () {
        // Do your stuff here

        webSocket.close(1000,"bye");
        super.onStop();
    }

    private  void sendSignal(String action, JSONObject message){

        JSONObject jsonObject =new JSONObject();

        try{
            jsonObject.put("peer",userName);
            jsonObject.put("action",action);
            jsonObject.put("message",message).toString();

        }catch (JSONException e){
            e.printStackTrace();
        }
        webSocket.send(jsonObject.toString());
    }

    private void initializeSurfaceViews() {
        rootEglBase = EglBase.create();
        binding.surfaceView.init(rootEglBase.getEglBaseContext(), null);
        binding.surfaceView.setEnableHardwareScaler(true);
        binding.surfaceView.setMirror(true);

        binding.surfaceView2.init(rootEglBase.getEglBaseContext(), null);
        binding.surfaceView2.setEnableHardwareScaler(true);
        binding.surfaceView2.setMirror(true);
    }

    private void initializePeerConnectionFactory() {
        PeerConnectionFactory.initializeAndroidGlobals(this, true, true, true);
        factory = new PeerConnectionFactory(null);
        factory.setVideoHwAccelerationOptions(rootEglBase.getEglBaseContext(), rootEglBase.getEglBaseContext());
    }

    private void createVideoTrackFromCameraAndShowIt() {
        VideoCapturer videoCapturer = createVideoCapturer();
        VideoSource videoSource = factory.createVideoSource(videoCapturer);
        videoCapturer.startCapture(VIDEO_RESOLUTION_WIDTH, VIDEO_RESOLUTION_HEIGHT, FPS);

        videoTrackFromCamera = factory.createVideoTrack(VIDEO_TRACK_ID, videoSource);
        videoTrackFromCamera.setEnabled(true);
        videoTrackFromCamera.addRenderer(new VideoRenderer(binding.surfaceView));
    }
    private DataChannel localDataChannel;
    private void initializePeerConnections() {
        peerConnection = createPeerConnection(factory);

        localDataChannel = peerConnection.createDataChannel("channel", new DataChannel.Init());
        localDataChannel.registerObserver(new DataChannel.Observer() {
            @Override
            public void onBufferedAmountChange(long l) {

            }

            @Override
            public void onStateChange() {
                Log.d("TAG", "onStateChange: " + localDataChannel.state().toString());
                runOnUiThread(() -> {
//                    if (localDataChannel.state() == DataChannel.State.OPEN) {
//                        binding.sendButton.setEnabled(true);
//                    } else {
//                        binding.sendButton.setEnabled(false);
//                    }
                });
            }

            @Override
            public void onMessage(DataChannel.Buffer buffer) {

            }
        });
    }

    private void startStreamingVideo() {
        MediaStream mediaStream = factory.createLocalMediaStream("ARDAMS");
        mediaStream.addTrack(videoTrackFromCamera);
        peerConnection.addStream(mediaStream);
        JSONObject msgContent =new JSONObject();
        try{
            msgContent.put("message","");
        }catch (JSONException e){
            e.printStackTrace();
        }

        sendSignal("new-peer",msgContent);
        Toast.makeText(LiveActivity.this,"Socket Connected",Toast.LENGTH_SHORT);
        initializeView();
//        sendMessage("got user media");
    }

    private PeerConnection createPeerConnection(PeerConnectionFactory factory) {
        ArrayList<PeerConnection.IceServer> iceServers = new ArrayList<>();
        iceServers.add(new PeerConnection.IceServer("stun:stun.l.google.com:19302"));

        PeerConnection.RTCConfiguration rtcConfig = new PeerConnection.RTCConfiguration(iceServers);
        MediaConstraints pcConstraints = new MediaConstraints();

        PeerConnection.Observer pcObserver = new PeerConnection.Observer() {
            @Override
            public void onSignalingChange(PeerConnection.SignalingState signalingState) {
                Log.d("TAG", "onSignalingChange: ");
            }

            @Override
            public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
                Log.d("TAG", "onIceConnectionChange: ");
            }

            @Override
            public void onIceConnectionReceivingChange(boolean b) {
                Log.d("TAG", "onIceConnectionReceivingChange: ");
            }

            @Override
            public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
                Log.d("TAG", "onIceGatheringChange: ");
            }

            @Override
            public void onIceCandidate(IceCandidate iceCandidate) {
                Log.d("TAG", "onIceCandidate: ");


                JSONObject sdp = new JSONObject();
                JSONObject msgContent =new JSONObject();
                try{
                    sdp.put("type","candidate");
                    sdp.put("id",iceCandidate.sdpMid);
                    sdp.put("label",iceCandidate.sdpMLineIndex);
                    sdp.put("candidate",iceCandidate.sdp);
                    msgContent.put("sdp", sdp );

                }catch (JSONException e){
                    e.printStackTrace();
                }
                sendSignal("new-candidate",msgContent);
            }

            @Override
            public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {
                Log.d("TAG", "onIceCandidatesRemoved: ");
            }

            @Override
            public void onAddStream(MediaStream mediaStream) {
                Log.d("TAG", "onAddStream: " + mediaStream.videoTracks.size());
                VideoTrack remoteVideoTrack = mediaStream.videoTracks.get(0);
                remoteVideoTrack.setEnabled(true);
                remoteVideoTrack.addRenderer(new VideoRenderer(binding.surfaceView2));


            }

            @Override
            public void onRemoveStream(MediaStream mediaStream) {
                Log.d("TAG", "onRemoveStream: ");
            }

            @Override
            public void onDataChannel(DataChannel dataChannel) {
                Log.d("TAG", "onDataChannel: ");
            }

            @Override
            public void onRenegotiationNeeded() {
                Log.d("TAG", "onRenegotiationNeeded: ");
            }
        };

        return factory.createPeerConnection(rtcConfig, pcConstraints, pcObserver);
    }

    private VideoCapturer createVideoCapturer() {
        VideoCapturer videoCapturer;
        if (useCamera2()) {
            videoCapturer = createCameraCapturer(new Camera2Enumerator(this));
        } else {
            videoCapturer = createCameraCapturer(new Camera1Enumerator(true));
        }
        return videoCapturer;
    }

    private VideoCapturer createCameraCapturer(CameraEnumerator enumerator) {
        final String[] deviceNames = enumerator.getDeviceNames();

        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        return null;
    }

    private boolean useCamera2() {
        return Camera2Enumerator.isSupported(this);
    }



}