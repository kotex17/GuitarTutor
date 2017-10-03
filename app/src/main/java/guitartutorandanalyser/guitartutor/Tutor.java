package guitartutorandanalyser.guitartutor;

import android.content.res.Resources;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.io.android.AndroidFFMPEGLocator;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;

public class Tutor extends AppCompatActivity {

    SoundPool soundPool;
    boolean isSoundPoolPlaying;
    int currentSoundPoolId;

    String PATH_NAME =
            "file://android_asset/songsaudio/testacd.wav";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor);
    }

    public void onButtonPlayClick(View v) {

        if (!isSoundPoolPlaying)
            startPlay();
        else
            stopPlay();
    }

    public void onButtonAnalyseClick(View v) {

        analyseRecord();
    }

    private void stopPlay() {

        soundPool.stop(currentSoundPoolId);
        isSoundPoolPlaying = false;
    }

    private void startPlay() {

        Toast.makeText(this, "playingSoundPool", Toast.LENGTH_LONG).show();
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0); //maximum streams, stream type, sound quality
        currentSoundPoolId = soundPool.load(this, R.raw.test_acd, 1);

        soundPool.play(currentSoundPoolId, 1, 1, 1, 0, 1);
        isSoundPoolPlaying = true;

    }

    private void analyseRecord() {
        Log.d("ELINDULT", "elindultam");
        new AndroidFFMPEGLocator(this);

        int SAMPLE_RATE = 44100;

        int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);


        AudioRecord record =
                new AudioRecord(MediaRecorder.AudioSource.MIC,
                        SAMPLE_RATE,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT, bufferSize);

        AudioDispatcher dispatcher = AudioDispatcherFactory.fromPipe(PATH_NAME, SAMPLE_RATE, bufferSize / 2, 0);

        dispatcher.addAudioProcessor(new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.DYNAMIC_WAVELET, SAMPLE_RATE, bufferSize / 2, new PitchDetectionHandler() {

            @Override
            public void handlePitch(PitchDetectionResult pitchDetectionResult,
                                    AudioEvent audioEvent) {
                final float pitchInHz = pitchDetectionResult.getPitch();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("pitch detected: ", String.valueOf(pitchInHz));
                    }
                });

            }
        }));
        new Thread(dispatcher, "Audio Dispatcher").start();
    }
}
