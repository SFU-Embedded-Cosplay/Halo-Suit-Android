package com.haloproject.projectspartanv2;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;

import java.util.concurrent.atomic.AtomicBoolean;

public class MicrophoneHandler
{
    private boolean isMicOn;
    private AudioTrack player;
    private AudioRecord recorder;

    private int sampleRate = 8000;
    private int channelConfig = AudioFormat.CHANNEL_IN_MONO;
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    int minBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);

    static MicrophoneHandler mMicrophoneHandler = null;

    static public MicrophoneHandler getInstance() {
        if (mMicrophoneHandler != null) {
            return mMicrophoneHandler;
        } else {
            mMicrophoneHandler = new MicrophoneHandler(true);
            return mMicrophoneHandler;
        }
    }

    private MicrophoneHandler(boolean isMicOn)
    {
        this.isMicOn = isMicOn;
        new Thread(new MicrophoneHandlerRunnable()).start();
    }

    private class MicrophoneHandlerRunnable implements Runnable {
        @Override
        public void run() {
            byte[] buffer = new byte[minBufSize];
            recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, channelConfig, audioFormat, minBufSize);
            recorder.startRecording();
            player = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate, AudioFormat.CHANNEL_OUT_MONO, audioFormat, minBufSize, AudioTrack.MODE_STREAM);
            player.setVolume(AudioTrack.getMaxVolume());
            player.play();

            while (true) {
                minBufSize = recorder.read(buffer, 0, buffer.length);
                int l = buffer.length;
                if (isMicOn) {
                    player.write(buffer, 0, buffer.length);
                }
            }
        }
    }

    public boolean isMicOn() {
        return isMicOn;
    }

    public void turnMicOn() {
        isMicOn = true;
    }

    public void turnMicOff() {
        isMicOn = false;
    }
}
