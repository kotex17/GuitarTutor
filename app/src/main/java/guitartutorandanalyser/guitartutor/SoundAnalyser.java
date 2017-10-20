package guitartutorandanalyser.guitartutor;


import android.content.Context;
import android.content.ContextWrapper;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.io.android.AndroidFFMPEGLocator;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;


public class SoundAnalyser {


    private HomeWork currentHomeWork;
    private String map;

    public SoundAnalyser(HomeWork homework) {
        this.currentHomeWork = homework;
    }

    int a = 0;

    public void analyseRecord(int SAMPLE_RATE, int BUFFER_SIZE, String PATH_NAME, Context ctx) {
        map = "";
        new AndroidFFMPEGLocator(ctx);

        final AudioDispatcher dispatcher = AudioDispatcherFactory.fromPipe(PATH_NAME, SAMPLE_RATE, 2048, 0);  //   BUFFER_SIZE / 2 is ineccessary?? need some test!!!


  /*      File mapFile = new File(Environment.getExternalStorageDirectory() + "/chromatic_scale_a_90bpmMAP3.txt");
        if (mapFile.exists())
           mapFile.delete();
*/
        // audiofactory(from pipe, wav header is ignored, pcm samples captured) ---> audiodispathcer(plays a file and sends float arrays to registered AudioProcessor )
        // AudioProcessor = PitchProcessor, PitchdetectionHandler = Interface( handlePitch())


        dispatcher.addAudioProcessor(new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, SAMPLE_RATE, 2048, new PitchDetectionHandler() {

            @Override
            public void handlePitch(PitchDetectionResult pitchDetectionResult,
                                    AudioEvent audioEvent) {
                final int pitchInHz = (int) pitchDetectionResult.getPitch();
                // int is enough, because guitar has freats, so you cant go wrong(its not a violing or an instrument without freats)
                try {

    /*                FileWriter fw = new FileWriter(Environment.getExternalStorageDirectory() + "/chromatic_scale_a_90bpmMAP3.txt", true);
                    fw.write(String.valueOf(pitchInHz) + ";");
                    fw.close();
*/
                    map = map + String.valueOf(pitchInHz) + ';';
                    a++;

                } catch (Exception e) {

                }
            }
        }));


        Thread audioDispathcerThread = new Thread(dispatcher, "Audio Dispatcher");
        audioDispathcerThread.start();

        compareResult(audioDispathcerThread);

    }

    public void compareResult(Thread audioDispathcerThread) {

        final Thread threadToWaitFor = audioDispathcerThread;

        Log.d("map length?", String.valueOf(a) + "  -   " + String.valueOf(currentHomeWork.getMap().length()) + "  -  " + String.valueOf(map.length()));

        Thread compareThread = new Thread(new Runnable() {

            public void run() {

                try {
                    threadToWaitFor.join();

                    int[] intPitchMapHomeWork = getIntPitchMap(currentHomeWork.getMap());
                    int[] intPitchMapRecord = getIntPitchMap(map);

                    Log.d("hosszak: ", intPitchMapHomeWork.length + "  -  " + intPitchMapRecord.length);
                    int result = compareIntPitchMaps(intPitchMapHomeWork, intPitchMapRecord);
                    Log.d("eredmeny: ", String.valueOf(result) + "%");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (Exception e) {

                }
            }
        }, "Compare Thread");
        compareThread.start();

    }

    private int[] getIntPitchMap(String map) throws Exception {


        int countDetectedPitches = map.length() - map.replace(";", "").length();

        Log.d("DETECTED pitches: ", String.valueOf(countDetectedPitches));

        int[] intPitchMap = new int[countDetectedPitches];

        int pitchMapIndex = 0;
        String onePitch = "";

        for (int i = 0; i < map.length(); i++) {

            char c = map.charAt(i);

            if (c != ';') {
                onePitch += c;
            } else {

                //   Log.d(" mennyi intben? ", onePitch);
                intPitchMap[pitchMapIndex] = (int) Double.parseDouble(onePitch);
                pitchMapIndex++;
                onePitch = "";
            }
        }

        return intPitchMap;
    }

    private int compareIntPitchMaps(int[] homework, int[] recorded) {
        int result;

        int correct = 0;
        int missed = 0;

        int i = 0;
        while (homework.length > i && recorded.length > i) {
            int difference = homework[i] - recorded[i];

            Log.d("na mit jelenz?", String.valueOf(homework[i]) + "  " + String.valueOf(recorded[i]) + "   " + String.valueOf(difference));
            if (5 > difference && difference > -5) {
                correct++;
            } else {
                missed++;
            }
            i++;
        }

        double dResult = (double) ((correct * 100) / (correct + missed));

        return (int) dResult;
    }


    public String getMap() {
        return map;
    }
}
