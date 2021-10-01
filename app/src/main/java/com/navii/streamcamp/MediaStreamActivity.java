package com.navii.streamcamp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

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

import com.navii.streamcamp.databinding.ActivitySamplePeerConnectionBinding;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;


public class MediaStreamActivity extends AppCompatActivity {
    private static final String TAG = "SamplePeerConnectionAct";
    public static final int VIDEO_RESOLUTION_WIDTH = 1280;
    public static final int VIDEO_RESOLUTION_HEIGHT = 720;
    public static final int FPS = 30;
    private static final String DTLS_SRTP_KEY_AGREEMENT_CONSTRAINT = "DtlsSrtpKeyAgreement";

    private ActivitySamplePeerConnectionBinding binding;
    private EglBase rootEglBase;
    private VideoTrack videoTrackFromCamera;
    private PeerConnectionFactory factory;
    private PeerConnection localPeerConnection, remotePeerConnection;

    private WebSocket webSocket;
    private  String SERVER_PATH ="ws://34.70.130.106:8000/";
    private String userName = "reneborigas";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_sample_peer_connection );

        initializeSurfaceViews();
        initiateSocketConnection();

        initializePeerConnectionFactory();
        createVideoTrackFromCameraAndShowIt();
//        initializePeerConnectionFactory();
//
//        createVideoTrackFromCameraAndShowIt();
//
//
//        startStreamingVideo();
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
    private void initiateSocketConnection() {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(SERVER_PATH).build();
        webSocket =   client.newWebSocket(request,new MediaStreamActivity.SocketListener());
    }
    private  class SocketListener extends WebSocketListener {

        @Override
        public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
            super.onOpen(webSocket, response);

            runOnUiThread(() ->{
                JSONObject msgContent =new JSONObject();
                try{
                    msgContent.put("message","");
                }catch (JSONException e){
                    e.printStackTrace();
                }

                sendSignal("new-peer",msgContent);
                Toast.makeText(MediaStreamActivity.this,"Socket Connected",Toast.LENGTH_SHORT);
//                initializeView();

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
                if(peerUsername == userName){
                    return;
                }

                try {
                    parsedData =new JSONObject(text);
                    JSONObject messageObj =  parsedData.getJSONObject("message");
                    receiver_channel_name = (String) messageObj.get("receiver_channel_name");
                    Log.d("message", receiver_channel_name);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                initializePeerConnections(receiver_channel_name);
                if(action.equals("new-peer")){
                    createOfferer(peerUsername,receiver_channel_name);
                    Log.d("actiom",action);
                    return;

                }

                if(action.equals("new-offer")){
                    Log.d("new-offer","offering");
                    try {
                        JSONObject messageObj =  parsedData.getJSONObject("message");
                        offer =     messageObj.getString("sdp");
                        Log.d("offer", offer.toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new SessionDescription(SessionDescription.Type.OFFER, offer);
                    createAnswer( new SessionDescription(SessionDescription.Type.OFFER, offer),peerUsername,receiver_channel_name);
//                    initializePeerConnectionFactory();
//                    createVideoTrackFromCameraAndShowIt();
//
////                    connectToOtherPeer();
//                    startStreamingVideo();
                    Log.d("actiom",action);
                    return;

                }

//                if(action == 'new-answer'){
//                    var answer = parsedData['message']['sdp'];
//                    var peer = mapPeers[peerUsername][0];
//                    peer.setRemoteDescription(answer);
//                    return;
//                }

            });

        }
    }

    private void initializePeerConnectionFactory() {
        PeerConnectionFactory.initializeAndroidGlobals(this, true, true, true);
        factory = new PeerConnectionFactory(null);
        factory.setVideoHwAccelerationOptions(rootEglBase.getEglBaseContext(), rootEglBase.getEglBaseContext());
    }
    public static final String VIDEO_TRACK_ID = "ARDAMSv0";
    private void createVideoTrackFromCameraAndShowIt() {
        VideoCapturer videoCapturer = createVideoCapturer();
        VideoSource videoSource = factory.createVideoSource(videoCapturer);
        videoCapturer.startCapture(VIDEO_RESOLUTION_WIDTH, VIDEO_RESOLUTION_HEIGHT, FPS);

        videoTrackFromCamera = factory.createVideoTrack(VIDEO_TRACK_ID, videoSource);
        videoTrackFromCamera.setEnabled(true);
        videoTrackFromCamera.addRenderer(new VideoRenderer(binding.surfaceView));
    }

    private void initializePeerConnections(String receiver_channel_name) {
        localPeerConnection = createPeerConnection(factory, true,receiver_channel_name);
        remotePeerConnection = createPeerConnection(factory, false,receiver_channel_name);
    }


    private void createOfferer(String peerUsername,String receiver_channel_name){
//        localPeerConnection = createPeerConnection(factory, true,receiver_channel_name);
        DataChannel localDataChannel;
        localDataChannel = localPeerConnection.createDataChannel("channel", new DataChannel.Init());

        localDataChannel.registerObserver(new DataChannel.Observer() {
            @Override
            public void onBufferedAmountChange(long l) {

            }
            @Override
            public void onStateChange() {
                Log.d("TaG", "onStateChange: " + localDataChannel.state().toString());
                runOnUiThread(() -> {

                });
            }

            @Override
            public void onMessage(DataChannel.Buffer buffer) {
                //Message
            }
        });
        MediaStream mediaStream = factory.createLocalMediaStream("ARDAMS");
        mediaStream.addTrack(videoTrackFromCamera);
        localPeerConnection.addStream(mediaStream);

        MediaConstraints sdpMediaConstraints = new MediaConstraints();

        localPeerConnection.createOffer(new SimpleSdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                Log.d(TAG, "onCreateSuccess: ");
                localPeerConnection.setLocalDescription(new SimpleSdpObserver(), sessionDescription);
                remotePeerConnection.setRemoteDescription(new SimpleSdpObserver(), sessionDescription);
                remotePeerConnection.createAnswer(new SimpleSdpObserver() {
                    @Override
                    public void onCreateSuccess(SessionDescription sessionDescription) {
                        localPeerConnection.setRemoteDescription(new SimpleSdpObserver(), sessionDescription);
                        remotePeerConnection.setLocalDescription(new SimpleSdpObserver(), sessionDescription);

//                        remotePeerConnection.setLocalDescription(new SimpleSdpObserver(), new SessionDescription(SessionDescription.Type.OFFER, localPeerConnection.getLocalDescription().description));
                    }
                    @Override
                    public void onCreateFailure(String s) {
                        super.onCreateFailure(s);
                        Log.e("failure",s);
                    }
                }, sdpMediaConstraints);

            }
        }, sdpMediaConstraints);

        localPeerConnection.addStream(mediaStream);

    }

    private void createAnswer(SessionDescription offer,String peerUsername,String receiver_channel_name){
        Log.d("new-offer","offered");
//        remotePeerConnection = createPeerConnection(factory, false,receiver_channel_name);
        DataChannel localDataChannel;
        localDataChannel = remotePeerConnection.createDataChannel("channel", new DataChannel.Init());

        localDataChannel.registerObserver(new DataChannel.Observer() {
            @Override
            public void onBufferedAmountChange(long l) {

            }
            @Override
            public void onStateChange() {
                Log.d("TaG", "onStateChange: " + localDataChannel.state().toString());
                runOnUiThread(() -> {

                });
            }

            @Override
            public void onMessage(DataChannel.Buffer buffer) {
                //Message
            }
        });




//        MediaStream mediaStream = factory.createLocalMediaStream("ARDAMS");
//        mediaStream.addTrack(videoTrackFromCamera);
//        remotePeerConnection.addStream(mediaStream);
//
//        MediaConstraints sdpMediaConstraints = new MediaConstraints();
//        remotePeerConnection.setRemoteDescription(new SimpleSdpObserver(), offer);
//        remotePeerConnection.createAnswer(new SimpleSdpObserver() {
//            @Override
//            public void onCreateSuccess(SessionDescription sessionDescription) {
//                Log.d(TAG, "onCreateSuccess: ");
//                remotePeerConnection.setLocalDescription(new SimpleSdpObserver(), offer);
//            }
//
//            @Override
//            public void onCreateFailure(String s) {
//                super.onCreateFailure(s);
//                Log.e("failure",s);
//            }
//        }, sdpMediaConstraints);

    }



    private void startStreamingVideo() {
        MediaStream mediaStream = factory.createLocalMediaStream("ARDAMS");
        mediaStream.addTrack(videoTrackFromCamera);
        localPeerConnection.addStream(mediaStream);

        MediaConstraints sdpMediaConstraints = new MediaConstraints();

        localPeerConnection.createOffer(new SimpleSdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                Log.d(TAG, "onCreateSuccess: ");
                localPeerConnection.setLocalDescription(new SimpleSdpObserver(), sessionDescription);
//                remotePeerConnection.setRemoteDescription(new SimpleSdpObserver(), sessionDescription);
//                Log.d("SSESH",sessionDescription.description);
//                remotePeerConnection.createAnswer(new SimpleSdpObserver() {
//                    @Override
//                    public void onCreateSuccess(SessionDescription sessionDescription) {
//                        localPeerConnection.setRemoteDescription(new SimpleSdpObserver(), sessionDescription);
//                        remotePeerConnection.setLocalDescription(new SimpleSdpObserver(), sessionDescription);
//                    }
//                }, sdpMediaConstraints);
            }
        }, sdpMediaConstraints);
    }
    Boolean isSentOffer=false;
    Boolean isSentAnswer=false;
    private PeerConnection createPeerConnection(PeerConnectionFactory factory, boolean isOffer,String receiver_channel_name) {
        PeerConnection.RTCConfiguration rtcConfig = new PeerConnection.RTCConfiguration(new ArrayList<>());
        MediaConstraints pcConstraints = new MediaConstraints();
        Log.d(TAG, "isOffer root: " + isOffer);


        PeerConnection.Observer pcObserver = new PeerConnection.Observer() {
            @Override
            public void onSignalingChange(PeerConnection.SignalingState signalingState) {
                Log.d(TAG, "onSignalingChange: ");
            }

            @Override
            public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
                Log.d(TAG, "onIceConnectionChange: ");
            }

            @Override
            public void onIceConnectionReceivingChange(boolean b) {
                Log.d(TAG, "onIceConnectionReceivingChange: ");
            }

            @Override
            public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
                Log.d(TAG, "onIceGatheringChange: ");
            }

            @Override
            public void onIceCandidate(IceCandidate iceCandidate) {

                if (isOffer) {
                    Log.d(TAG, "added: " + remotePeerConnection.iceConnectionState());
                    remotePeerConnection.addIceCandidate(iceCandidate);



                } else {
                    Log.d(TAG, "local added: " + localPeerConnection.iceConnectionState());
                    localPeerConnection.addIceCandidate(iceCandidate);
                }

                JSONObject msgContent =new JSONObject();
                JSONObject sdp = new JSONObject();
                Log.e("signal" ,"asd");
                if (!isSentOffer){
                    if(isOffer){
                        try{
                            sdp.put("type","offer");
                            sdp.put("sdp",localPeerConnection.getLocalDescription().description);
                            msgContent.put("sdp", sdp );
                            msgContent.put("receiver_channel_name",receiver_channel_name);

                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                        sendSignal("new-offer",msgContent);
                        isSentOffer = true;
                    }

                }
                if (!isSentAnswer){
                    if(!isOffer){
                        Log.e("msgxontent answer","here");

                        try{
                            sdp.put("type","answer");
                            sdp.put("sdp",remotePeerConnection.getLocalDescription().description);
                            msgContent.put("sdp", sdp );
                            msgContent.put("receiver_channel_name",receiver_channel_name);

                        }catch (JSONException e){
                            e.printStackTrace();

                        }

                        Log.e("msgxontent answer",msgContent.toString());
//                    Log.e("msgxontent", String.valueOf(localPeerConnection.getLocalDescription()));
                        sendSignal("new-answer",msgContent);
                        isSentAnswer = true;
                    }

                }

            }

            @Override
            public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {
                Log.d(TAG, "onIceCandidatesRemoved: ");
            }

            @Override
            public void onAddStream(MediaStream mediaStream) {
                Log.d(TAG, "onAddStream: " + mediaStream.videoTracks.size());

                Log.d(TAG,mediaStream.videoTracks.toString());
                VideoTrack remoteVideoTrack = mediaStream.videoTracks.get(0);
                remoteVideoTrack.setEnabled(true);
                remoteVideoTrack.addRenderer(new VideoRenderer(binding.surfaceView2));

            }

            @Override
            public void onRemoveStream(MediaStream mediaStream) {
                Log.d(TAG, "onRemoveStream: ");
            }

            @Override
            public void onDataChannel(DataChannel dataChannel) {
                Log.d(TAG, "onDataChannel: ");
            }

            @Override
            public void onRenegotiationNeeded() {
                Log.d(TAG, "onRenegotiationNeeded: ");
            }
        };

        return factory.createPeerConnection(rtcConfig, pcConstraints, pcObserver);
    }
    private  void sendSignal(String action, JSONObject message){

        JSONObject jsonObject =new JSONObject();

        try{
            jsonObject.put("peer",userName);
            jsonObject.put("action",action);
            jsonObject.put("message",message);

        }catch (JSONException e){
            e.printStackTrace();
        }
        Log.e("send signal",jsonObject.toString());
        webSocket.send(jsonObject.toString());
    }


    @Override
    public void onStop () {
        // Do your stuff here

        webSocket.close(1000,"bye");
        super.onStop();
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

        // First, try to find front facing camera
        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        // Front facing camera not found, try something else
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

    /*
     * Read more about Camera2 here
     * https://developer.android.com/reference/android/hardware/camera2/package-summary.html
     * */
    private boolean useCamera2() {
        return Camera2Enumerator.isSupported(this);
    }


}