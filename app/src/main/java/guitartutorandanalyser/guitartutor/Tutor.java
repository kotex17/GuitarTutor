package guitartutorandanalyser.guitartutor;

import android.database.Cursor;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class Tutor extends AppCompatActivity {


    final int SAMPLE_RATE = 44100;
    final String PATH_NAME = Environment.getExternalStorageDirectory() + "/GuitarTutorRec.wav";   // "/chromatic_scale_a_90bpm.wav";
    final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT) / 2;

    boolean isSoundPlaying;


    Button recButton;
    MediaPlayer mediaPlayer;

    RadioButton metronome;
    HomeWork homework;

    SoundRecorder soundRecorder;
    SoundAnalyser soundAnalyser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor);

        String s = getIntent().getStringExtra("lessonId");
        fetchCurrentHomework(Integer.parseInt(s));

        Log.d("HOMEWORK DATA ", homework.getName() + " " + homework.getType() + " " + homework.getBpm());

        metronome = (RadioButton) findViewById(R.id.metronomeButton);
        recButton = (Button) findViewById(R.id.button_play_stop);
        ImageView imageView = (ImageView) findViewById(R.id.imageView_tab);
        imageView.setImageResource(homework.getTabId());
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

    public void onButtonRecClick(View v) {
        startRecording();
    }

    public void onButtonStopClick(View v) {
        stopRecording();
    }


    private void stopPlay() {

        mediaPlayer.stop();
        mediaPlayer.release();
        isSoundPlaying = false;
        recButton.setText("Play");

    }

    private void startPlay() {

        mediaPlayer = MediaPlayer.create(this, homework.getSoundId());
        mediaPlayer.start();
        isSoundPlaying = true;
        recButton.setText("Stop");

    }

    private void startRecording() { // creates new audio recorder instance, start it, new recording thread and new metronome


        soundRecorder = new SoundRecorder();
        Thread recordAudioThread = soundRecorder.startRecording();
        Thread startMetronome = initializeMetronome();

        playPreCount();

        recordAudioThread.start();
        startMetronome.start();
    }

    private void stopRecording() {
        if (soundRecorder != null)
            soundRecorder.stopRecording();

    }

    private void playPreCount() {

        SoundPool soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0); // 1?????????
        int soundId = soundPool.load(this, R.raw.metronome_click,1);

        float tick = (60f / homework.getBpm()) * 1000; // calculate milliseconds between beats

        for (int i = 0; i < 4; i++) {

            long time1 = SystemClock.elapsedRealtime();
            soundPool.play(soundId, 1, 1, 1, 1, 1);

            while (SystemClock.elapsedRealtime() - time1 <= tick) {
                // waiting ...
            }
        }
    }

    private Thread initializeMetronome() {

        float tick = (60f / homework.getBpm()) * 1000; //tempo means beats per minute, calculate milliseconds between ticks
        final long tickMilliSeconds = (long) tick;  // Thread.sleep() 2 params: milliseconds  long, nanoseconds  int
        final int tickNanoSeconds = (int) ((tick - (int) tick) * 1000000); // milli = 10^-3, nano = 10^-9, difference is 10^6 = 1000000

        final Handler handler = new Handler(getApplicationContext().getMainLooper());

        return new Thread(new Runnable() {

            public void run() {

                for(int i = 0; i < homework.getBeats() && soundRecorder.isSoundRecording(); i++)  {

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            metronome.setChecked(!metronome.isChecked());
                        }
                    });
                    try {
                        Thread.currentThread().sleep(tickMilliSeconds, tickNanoSeconds);
                    } catch (InterruptedException e) {
                        Log.d("Metronome thread error", e.getMessage());
                    }

                }
                stopRecording();
            }
        }, "Metronome Thread");

    }

    private void analyseRecord() {

        soundAnalyser = new SoundAnalyser(homework);
        soundAnalyser.analyseRecord(SAMPLE_RATE, BUFFER_SIZE, PATH_NAME);

        ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar);
        pb.setVisibility(View.VISIBLE);

        Log.d("SOUND ANALYSER STRING",soundAnalyser.getMap());

       // soundAnalyser.compareResult(); // not implemented ye

//////////// created clas to do this work 2017.10.16.
      /*  Toast.makeText(this, "Analyse started", Toast.LENGTH_SHORT).show();

        //new AndroidFFMPEGLocator(this);

        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT, BUFFER_SIZE);

        final AudioDispatcher dispatcher = AudioDispatcherFactory.fromPipe(PATH_NAME, SAMPLE_RATE, BUFFER_SIZE / 2, 0);

     /*   File f = new File(Environment.getExternalStorageDirectory() + "/test.txt");

        if (f.exists())
            f.delete();*/


/*


        dispatcher.addAudioProcessor(new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, SAMPLE_RATE, BUFFER_SIZE / 2, new PitchDetectionHandler() {

            @Override
            public void handlePitch(PitchDetectionResult pitchDetectionResult,
                                    AudioEvent audioEvent) {
                final float pitchInHz = pitchDetectionResult.getPitch();

                try {

                    /*FileWriter fw = new FileWriter(Environment.getExternalStorageDirectory() + "/test.txt", true);
                    fw.write(String.valueOf(pitchInHz) + ";");
                    fw.close();*/
   /*                 Log.d("szerintem igy is muxik", String.valueOf(pitchInHz));

                } catch (Exception e) {

                }
            }
        }));

        Thread analyseThread = new Thread(dispatcher, "Audio Dispatcher");
        analyseThread.start();

        ProgressBar pb  = (ProgressBar)findViewById(R.id.progressBar);

        pb.setVisibility(View.VISIBLE);*/
       /* while (analyseThread.getState() != Thread.State.TERMINATED) {

        }*/
        //   Toast.makeText(this, "Anayse finished", Toast.LENGTH_SHORT).show();


        //  getAnalyseResult();


    }

    private void getAnalyseResult() {
        Log.d("COMPAREAA ", " ß ");
        try {
            BufferedReader br1 = new BufferedReader(new FileReader(Environment.getExternalStorageDirectory() + "/map1.txt"));


            List<String> lines1 = new ArrayList<String>();


            String line1 = "";

            while (((line1 = br1.readLine()) != null)) {
                lines1.add(line1);
                //  lines2.add(line2);
            }


            //  && ((line2 = br2.readLine()) != null))
            br1.close();
            Log.d("COMPAREAA ", " ¤ß$ ");
            BufferedReader br2 = new BufferedReader(new FileReader(Environment.getExternalStorageDirectory() + "/map2.txt"));

            List<String> lines2 = new ArrayList<String>();
            String line2 = "";

            while (((line2 = br2.readLine()) != null)) {
                //lines1.add(line1);
                lines2.add(line2);
            }

            br2.close();

            char[] line1Array = line1.toCharArray();
            char[] line2Array = line2.toCharArray();

            int equal = 0;
            int error = 0;

            // lenght of the two arrays are the same at this point
            for (int i = 0; i < line1Array.length; i++) {

                if (line1Array[i] == line2Array[i]) {
                    equal++;
                } else {
                    error++;
                }
                Log.d("COMPAREAA ", line1Array[i] + " ß " + line2Array[i]);
            }

            Toast.makeText(this, "equal: " + String.valueOf(equal) + " ß " + "error: " + String.valueOf(error), Toast.LENGTH_LONG);
        } catch (Exception e) {
            Log.d("COMPAREAA ", "ERROR");
            Log.d("COMPAREAA", e.getMessage());
        }

    }

    private void fetchCurrentHomework(int id) {

        DatabaseHelper db = new DatabaseHelper(this);
        try {
            db.openDataBase();
        } catch (Exception e) {

        }

        Cursor cursor = db.myDataBase.query(
                DatabaseHelper.TABLE_HOMEWORKS,
                new String[]{DatabaseHelper.Column.ID,
                        DatabaseHelper.Column.TYPE,
                        DatabaseHelper.Column.NAME,
                        DatabaseHelper.Column.BPM,
                        DatabaseHelper.Column.BEATS,
                        DatabaseHelper.Column.RECORDPOINT,
                        DatabaseHelper.Column.RECORDDATE,
                        DatabaseHelper.Column.COMPLETED,
                        DatabaseHelper.Column.MAP,
                        DatabaseHelper.Column.TABID,
                        DatabaseHelper.Column.SONGID},
                DatabaseHelper.Column.ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null, "1"); // LIMIT 1

        if (cursor.moveToFirst()) {
            this.homework = HomeWork.homeWorkCreator(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getInt(3),
                    cursor.getInt(4),
                    cursor.getInt(5),
                    cursor.getString(6),
                    cursor.getInt(7),
                    cursor.getString(8),
                    cursor.getInt(9),
                    cursor.getInt(10));
        }

        cursor.close();
        db.close();

    }
}
