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


    HomeWork currentHomeWork;
    String recordedAudioMap;
    Context context;
    int processBufferSize = 2048;
    int SAMPLE_RATE;
    String PATH_NAME;


    public SoundAnalyser(HomeWork homework, Context context, int SAMPLE_RATE, String PATH_NAME) {
        this.currentHomeWork = homework;
        this.context = context;
        this.SAMPLE_RATE = SAMPLE_RATE;
        this.PATH_NAME = PATH_NAME;
    }


    public void analyseRecord() {

        recordedAudioMap = "";

        new AndroidFFMPEGLocator(context);

        final AudioDispatcher dispatcher = AudioDispatcherFactory.fromPipe(PATH_NAME, SAMPLE_RATE, processBufferSize, 0);

        // audiofactory(from pipe, wav header is ignored, pcm samples captured) ---> audiodispathcer(plays a file and sends float arrays to registered AudioProcessor )
        // AudioProcessor = PitchProcessor, PitchdetectionHandler = Interface( handlePitch())


        dispatcher.addAudioProcessor(new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, SAMPLE_RATE, processBufferSize, new PitchDetectionHandler() {

            @Override
            public void handlePitch(PitchDetectionResult pitchDetectionResult,
                                    AudioEvent audioEvent) {
                final int pitchInHz = (int) pitchDetectionResult.getPitch();
                // int is enough, because guitar has freats, so you cant go wrong(its not a violin or an instrument without freats)
                try {
                    recordedAudioMap += String.valueOf(pitchInHz) + ';';

                } catch (Exception e) {
                }
            }
        }));


        Thread audioDispathcerThread = new Thread(dispatcher, "Audio Dispatcher");
        audioDispathcerThread.setPriority(Thread.MAX_PRIORITY);
        audioDispathcerThread.start();

        compareResult(audioDispathcerThread);

    }

    private void compareResult(Thread audioDispathcerThread) {

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

        int correct;
        int missed;

        float bestResult = 0;

        // shift the recorded map to left and right compared to the homework's map to find the best match
        for (int runTwice = 0; runTwice < 2; runTwice++) {

            int recordedIndex;
            int homeworkIndex;


            for (int j = 0; j < 22; j++) { //44100 / 2048 = 21,5, so up to 1 sec the app corrigate the recorded map ( 22 samples)

                correct = 0;
                missed = 0;

                if (runTwice == 0) { // first run shift one way
                    recordedIndex = 0;
                    homeworkIndex = j;
                } else { // second time shift other way
                    recordedIndex = j;
                    homeworkIndex = 0;
                }

                while (homework.length > homeworkIndex && recorded.length > recordedIndex) {
                    // because of overtone we have to check multiple values, moreover guitar is an imperfect instrument we give 5 Hz threshold to each note

                    float bigger;
                    int smaller;
                    if (homework[homeworkIndex] > recorded[recordedIndex]) {
                        bigger = homework[homeworkIndex];
                        smaller = recorded[recordedIndex];
                    } else {
                        smaller = homework[homeworkIndex];
                        bigger = recorded[recordedIndex];
                    }

                    float ratio = bigger / smaller;
                    //Log.d("na mit jelez: " + String.valueOf(j)," hi "+ String.valueOf(homework[homeworkIndex]) + "  ri " + String.valueOf(recorded[recordedIndex]) + "   b " + String.valueOf(bigger)+"   s "+String.valueOf(smaller) + "  rat "+String.valueOf(ratio));

                    if (0.95f <= ratio && ratio > 0.05f) {
                        correct++;
                    } else {
                        missed++;
                    }
                    homeworkIndex++;
                    recordedIndex++;
                }

                float tempResult = ((float) correct * 100) / (correct + missed);

                if (tempResult > bestResult)
                    bestResult = tempResult;

            }
        }

        return Math.round(bestResult);
    }

    private void startAnalyseResultActivity(int result) {

        Intent analyseResultIntent = new Intent("guitartutorandanalyser.guitartutor.AnalyseResult");
        analyseResultIntent.putExtra("result", String.valueOf(result));
        analyseResultIntent.putExtra("homeworkId", String.valueOf(currentHomeWork.get_id()));
        context.startActivity(analyseResultIntent);

    }

}
