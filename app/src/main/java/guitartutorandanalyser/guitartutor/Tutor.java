package guitartutorandanalyser.guitartutor;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.io.android.AndroidFFMPEGLocator;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;

public class Tutor extends AppCompatActivity {


    final int SAMPLE_RATE = 44100;

    final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT);

    AudioRecord recorder;

    boolean isSoundRecording;
    boolean isSoundPlaying;
    Button recButton;

    String PATH_NAME = Environment.getExternalStorageDirectory().getAbsolutePath() + "GuitarTutorRec";
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor);

        recButton = (Button) findViewById(R.id.button_play_stop);
    }

    public void onButtonPlayClick(View v) {

        if (isSoundPlaying) {
            stopPlay();
        } else {
            startPlay();
        }
    }

    public void onButtonAnalyseClick(View v) {

        analyseRecord();
    }

    private void stopPlay() {

        mediaPlayer.stop();
        mediaPlayer.release();
        isSoundPlaying = false;
        recButton.setText("Play");

    }

    private void startPlay() {

        mediaPlayer = MediaPlayer.create(this, R.raw.testacd);
        mediaPlayer.start();
        isSoundPlaying = true;
        recButton.setText("Stop");

    }


    private void recPCM() {

        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT, BUFFER_SIZE);

        recorder.startRecording();
        isSoundRecording = true;

        System.out.println("rec started");

        Thread recordAudioThread = new Thread(new Runnable() {

            public void run() {

                System.out.println("thread running");
                writeAudioDataToFile();
            }
        }, "AudioRecorder Thread");
        recordAudioThread.start();
    }

    private void writeAudioDataToFile() {
        // Read audio data into short buffer, Write the output audio in byte

        short sData[] = new short[BUFFER_SIZE];

        try {
            FileOutputStream os = new FileOutputStream(PATH_NAME);

            while (isSoundRecording) {
                // gets the voice output from microphone to byte format
                recorder.read(sData, 0, BUFFER_SIZE);

                // writes the data to file from buffer stores the voice buffer
                byte bData[] = short2byte(sData);
                os.write(bData, 0, BUFFER_SIZE * 2);
            }

            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

       /* FileOutputStream os;
        try {
            os = new FileOutputStream(PATH_NAME);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (isSoundRecording) {
            // gets the voice output from microphone to byte format
            recorder.read(sData, 0, BUFFER_SIZE);

            try {
                // writes the data to file from buffer stores the voice buffer
                byte bData[] = short2byte(sData);
                os.write(bData, 0, BUFFER_SIZE * 2);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    private byte[] short2byte(short[] sData) {
        // all android supports little endianness, Android ARM systems are bi endian, by deafault littleendian (manualy can be swithced to big endian)
        //android audio format PCM16bit record in the default device native endian
        byte[] bytes = new byte[sData.length * 2];

        for (int i = 0; i < sData.length; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x00FF); //hexadecimal 00 FF = 0000 0000 1111 1111 masking
            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8); // shift 8 right,
        }
        return bytes;
    }

    private void stopRecording() {
        // stops the recording activity
        if (recorder != null) {

            recorder.stop();
            recorder.release();
            recorder = null;

            isSoundRecording = false;
        }


        try {

            //  audioDispatcherPitch();
        } catch (Exception e) {

        }
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
