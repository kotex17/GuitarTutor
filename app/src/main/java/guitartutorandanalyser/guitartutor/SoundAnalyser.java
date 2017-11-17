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

import java.math.BigDecimal;
import java.math.RoundingMode;


public class SoundAnalyser {

    HomeWork currentHomeWork;
    String recordedAudioMap;
    Context context;
    final int BUFFER_SIZE = 2048;
    final int SAMPLE_RATE;
    final String PATH_NAME;


    public SoundAnalyser(HomeWork homework, Context context, int SAMPLE_RATE, String PATH_NAME) {
        this.currentHomeWork = homework;
        this.context = context;
        this.SAMPLE_RATE = SAMPLE_RATE;
        this.PATH_NAME = PATH_NAME;
    }


    public void analyseRecord() {

        recordedAudioMap = "";

        new AndroidFFMPEGLocator(context);

        final AudioDispatcher dispatcher = AudioDispatcherFactory.fromPipe(PATH_NAME, SAMPLE_RATE, BUFFER_SIZE, 0);

        // audiofactory(from pipe, wav header is ignored, pcm samples captured) ---> audiodispathcer(plays a file and sends float arrays to registered AudioProcessor )
        // AudioProcessor = PitchProcessor, PitchdetectionHandler = Interface( handlePitch())


        dispatcher.addAudioProcessor(new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, SAMPLE_RATE, BUFFER_SIZE, new PitchDetectionHandler() {

            @Override
            public void handlePitch(PitchDetectionResult pitchDetectionResult,
                                    AudioEvent audioEvent) {
                final float pitchInHz = roundFloat2Decimal( pitchDetectionResult.getPitch()) ;

                Log.d("pitc AAA", String.valueOf(pitchInHz));
                // store in string, float[] array length is not known at this point
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

    private float roundFloat2Decimal(float number) {

       // BigDecimal bd = new BigDecimal(number, new MathContext(4))).floatValue();


        BigDecimal bd = new BigDecimal(number);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.floatValue();
    }

    private void compareResult(Thread audioDispathcerThread) {

        final Thread threadToWaitFor = audioDispathcerThread;

        Thread compareThread = new Thread(new Runnable() {

            public void run() {

                try {
                    threadToWaitFor.join();

                    float[] intPitchMapHomeWork = getFloatPitchMap(currentHomeWork.getMap());
                    float[] intPitchMapRecord = getFloatPitchMap(recordedAudioMap);

                    Log.d("maps hw: ", currentHomeWork.getMap());
                    Log.d("maps re: ", recordedAudioMap);
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

    private float[] getFloatPitchMap(String map) throws Exception {

        int countDetectedPitches = map.length() - map.replace(";", "").length();

        float[] floatPitchMap = new float[countDetectedPitches];

        int pitchMapIndex = 0;
        String onePitch = "";

        for (int i = 0; i < map.length(); i++) {

            char c = map.charAt(i);

            if (c != ';') {
                onePitch += c;
            } else {

                floatPitchMap[pitchMapIndex] = Float.parseFloat(onePitch);
                pitchMapIndex++;
                onePitch = "";
            }
        }

        return floatPitchMap;
    }

    private int compareIntPitchMaps(float[] homework, float[] recorded) {

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

                boolean firstMinusOneError = true;

                while (homework.length > homeworkIndex && recorded.length > recordedIndex) {
                    // because of overtone we have to check multiple values, moreover guitar is an imperfect instrument we give 5 Hz threshold to each note

                    float bigger = recorded[recordedIndex];
                    float smaller = homework[homeworkIndex];

                    if (smaller > bigger) {
                        bigger = homework[homeworkIndex];
                        smaller = recorded[recordedIndex];
                    }


                    if (bigger == smaller) {

                        correct++;
                        firstMinusOneError = true;
                    } else if (smaller == -1 || bigger == -1) {

                        // one = -1, other != -1 ==> mistake ( just consecutive -1 means error in guitar play, single -1 means FFT algorithm imperfect)
                        if (!firstMinusOneError)
                            missed++;
                        else
                            firstMinusOneError = false;

                    } else {

                        // bigger != smaller ( can be: tiny gap between notes, overtone, wrong note)
                        float difference = Float.MIN_VALUE;
                        float nextDifference = bigger;

                        while (nextDifference >= -4) {

                            nextDifference -= smaller;

                            if (nextDifference < -4) {
                                // examination starts
                                if (difference >= -4 && difference <= 4)
                                    correct++;
                                else
                                    missed++;

                                break;
                            }
                            difference = nextDifference;
                        }
                        firstMinusOneError = true;
                    }


                    //   Log.d("na mit jelez: " + String.valueOf(j), "  hi " + String.valueOf(homework[homeworkIndex]) + "  ri " + String.valueOf(recorded[recordedIndex]) +"  b "+String.valueOf(bigger)+"  s "+String.valueOf(smaller)+"   c " + String.valueOf(correct) + "  m " + String.valueOf(missed));

                    homeworkIndex++;
                    recordedIndex++;
                }

                float tempResult = ((float) correct * 100) / (correct + missed);

                if (tempResult > bestResult) {
                    bestResult = tempResult;
                    Log.d("best match", String.valueOf(j) + "   rt" + String.valueOf(runTwice) + "  c: " + String.valueOf(correct) + "  m:" + String.valueOf(missed));
                }

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
