package guitartutorandanalyser.guitartutor;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;

/**
 * Created by Marcus on 2017. 10. 16..
 */

public class SoundAnalysator {

    private final String MAP_FILE_NAME = "/guitarTutorMap.txt";
    File mapFile = new File(Environment.getExternalStorageDirectory() + MAP_FILE_NAME);
    private HomeWork currentHomeWork;

    public SoundAnalysator(HomeWork homework){
        this.currentHomeWork = homework;
    }

    public void analyseRecord(int SAMPLE_RATE, int BUFFER_SIZE, String PATH_NAME) {

        //new AndroidFFMPEGLocator(this);


        AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT, BUFFER_SIZE);

        final AudioDispatcher dispatcher = AudioDispatcherFactory.fromPipe(PATH_NAME, SAMPLE_RATE, BUFFER_SIZE / 2, 0);

        if (mapFile.exists())
            mapFile.delete();

        dispatcher.addAudioProcessor(new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, SAMPLE_RATE, BUFFER_SIZE / 2, new PitchDetectionHandler() {

            @Override
            public void handlePitch(PitchDetectionResult pitchDetectionResult,
                                    AudioEvent audioEvent) {
                final float pitchInHz = pitchDetectionResult.getPitch();

                try {

                    FileWriter fw = new FileWriter(Environment.getExternalStorageDirectory() + MAP_FILE_NAME, true);
                    fw.write(String.valueOf(pitchInHz) + ";");
                    fw.close();
                    Log.d("classban is muxik?", String.valueOf(pitchInHz));

                } catch (Exception e) {

                }
            }
        }));

        Thread analyseThread = new Thread(dispatcher, "Audio Dispatcher");
        analyseThread.start();

        /*while (analyseThread.getState() != Thread.State.TERMINATED) {

        }*/
    }

    public void compareResult() {
        // compare currentHomework.map with the created mapFile :)

    }


}
