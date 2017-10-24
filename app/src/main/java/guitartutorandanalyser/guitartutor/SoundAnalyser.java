package guitartutorandanalyser.guitartutor;


import android.content.Context;
import android.content.Intent;
import android.util.Log;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.io.android.AndroidFFMPEGLocator;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;


public class SoundAnalyser {


    private HomeWork currentHomeWork;
    private String recordedAudioMap;
    Context context;



    public SoundAnalyser(HomeWork homework, Context context) {
        this.currentHomeWork = homework;
        this.context = context;
    }

    int a = 0;

    public void analyseRecord(int SAMPLE_RATE, String PATH_NAME, Context ctx) {
        recordedAudioMap = "";
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
                // int is enough, because guitar has freats, so you cant go wrong(its not a violin or an instrument without freats)
                try {

    /*                FileWriter fw = new FileWriter(Environment.getExternalStorageDirectory() + "/chromatic_scale_a_90bpmMAP3.txt", true);
                    fw.write(String.valueOf(pitchInHz) + ";");
                    fw.close();
*/
                    recordedAudioMap += String.valueOf(pitchInHz) + ';';
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

        Thread compareThread = new Thread(new Runnable() {

            public void run() {

                try {
                    threadToWaitFor.join();

                    int[] intPitchMapHomeWork = getIntPitchMap(currentHomeWork.getMap());
                    int[] intPitchMapRecord = getIntPitchMap(recordedAudioMap);

                    Log.d("hosszak: ", intPitchMapHomeWork.length + "  -  " + intPitchMapRecord.length);

                    int result = compareIntPitchMaps(intPitchMapHomeWork, intPitchMapRecord);

                    Log.d("eredmeny: ", String.valueOf(result) + "%");

                    startAnalyseResultActivity(result);

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

        int[] intPitchMap = new int[countDetectedPitches];

        int pitchMapIndex = 0;
        String onePitch = "";

        for (int i = 0; i < map.length(); i++) {

            char c = map.charAt(i);

            if (c != ';') {
                onePitch += c;
            } else {

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

    private void startAnalyseResultActivity(int result) {

        Intent analyseResultIntent = new Intent("guitartutorandanalyser.guitartutor.AnalyseResult");
        analyseResultIntent.putExtra("result", String.valueOf(result));
        analyseResultIntent.putExtra("homeworkId", String.valueOf(currentHomeWork.get_id()));
        context.startActivity(analyseResultIntent);

    }

}
