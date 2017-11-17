package guitartutorandanalyser.guitartutor;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;


public class Tutor extends AppCompatActivity {

    final int SAMPLE_RATE = 44100;
    final String PATH_NAME = Environment.getExternalStorageDirectory() + "/GuitarTutorRec.wav";//

    boolean isSoundPlaying, isRecording, isRecordPlaying, isPracticingMetronme, analyseEnabled, reachedEndOfRecordTime;

    Button playStopButton, recStopButton, playRecStopRecButton;
    MediaPlayer mediaPlayer;
    RadioButton metronome;
    TextView metronomeCounter;

    SoundRecorder soundRecorder;
    SoundAnalyser soundAnalyser;
    HomeWork homework;
    ProgressBar progressBar;

    Thread practiceMetronome;
    Thread practiceClick;

    Thread recordingWithMetronome;


    Tick tick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor);

        String parameter = getIntent().getStringExtra("homeWorkId");

        this.homework = (new DatabaseHelper(this)).fetchHomeworkById(Integer.parseInt(parameter));

        tick = new Tick(homework.getBpm());


        metronome = (RadioButton) findViewById(R.id.metronomeButton);
        playStopButton = (Button) findViewById(R.id.button_play_stop);
        recStopButton = (Button) findViewById(R.id.button_rec_stop);
        playRecStopRecButton = (Button) findViewById(R.id.button_playrec_stoprec);
        metronomeCounter = (TextView) findViewById(R.id.metronomeCounter);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);


        ((ImageView) findViewById(R.id.imageView_tab)).setImageResource(homework.getTabId());
        ((TextView) findViewById(R.id.textView_title)).setText(homework.getName());

        File file = new File(PATH_NAME);
        if (file.exists())
            file.delete();
    }

    @Override
    protected void onResume() {
        super.onResume();

        playStopButton.setText("Meghallgatom");
        recStopButton.setText("Rec");
        playRecStopRecButton.setText("Visszajátszás");
        metronomeCounter.setText("4/4");

    }

    @Override
    protected void onPause() {

        super.onPause();

        try {

            if (isRecording)
                stopRecording();

            if (isSoundPlaying)
                stopSoundPlay();

            if (isRecordPlaying)
                stopRecordPlay();

            if (isPracticingMetronme)
                stopPracticeMetronome();

            progressBar.setVisibility(View.INVISIBLE);
        } catch (Exception e) {

        }
    }

    public void onButtonPlayClick(View v) {

        if (!isSoundPlaying && !isRecordPlaying && !isRecording) {
            startSoundPlay();
            playStopButton.setText("Stop");

        } else if (!isRecordPlaying) {
            stopSoundPlay();
            playStopButton.setText("Meghallgatom");

        }
    }

    public void onButtonAnalyseClick(View v) {

        if (analyseEnabled && !isRecording && !isPracticingMetronme && !isSoundPlaying && !isRecordPlaying)
            analyseRecord();
        else
            Toast.makeText(this, "A felvétel nem elég hosszú", Toast.LENGTH_LONG).show();
    }

    public void onButtonRecClick(View v) {

        if (soundRecorder != null && isRecording) {

            recStopButton.setText("Rec");
            stopRecording();
        } else if (!isSoundPlaying && !isRecordPlaying && !isPracticingMetronme) {

            recStopButton.setText("Stop rec");
            startRecording();
        }

    }

    public void onButtonPlayRecStopRecClick(View v) {


        if (!isRecordPlaying && !isSoundPlaying && !isPracticingMetronme && !isRecording && (new File(PATH_NAME)).exists()) {

            playRecStopRecButton.setText("Stop");
            startRecordPlay();

        } else if (!isSoundPlaying && isRecordPlaying) {

            playRecStopRecButton.setText("Visszajátszás");

            stopRecordPlay();
        }
    }

    public void onStartStopPracticingMetronome(View v) {

        if (!isPracticingMetronme && !isRecording && !isSoundPlaying && !isRecordPlaying) {

            isPracticingMetronme = true;
            practiceMetronome();

        } else if (isPracticingMetronme) {

            isPracticingMetronme = false;
            stopPracticeMetronome();
        }
    }


    private void startRecordPlay() {

        mediaPlayer = MediaPlayer.create(this, Uri.parse(PATH_NAME));
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                playRecStopRecButton.setText("Visszajátszás");
                isRecordPlaying = false;
                mediaPlayer.release();
            }

        });

        mediaPlayer.start();
        isRecordPlaying = true;

    }

    private void stopRecordPlay() {

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            isRecordPlaying = false;
        }
    }

    private void startSoundPlay() {

        mediaPlayer = MediaPlayer.create(this, homework.getSoundId());
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                playStopButton.setText("Meghallgatom");
                isSoundPlaying = false;
                mediaPlayer.release();
            }

        });

        mediaPlayer.start();
        isSoundPlaying = true;


    }

    private void stopSoundPlay() {

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            isSoundPlaying = false;
        }
    }

    private void startRecording() { // creates new audio recorder instance, start recording thread and metronome

        soundRecorder = new SoundRecorder();

        isRecording = true;

        Thread recordAudioThread = soundRecorder.startRecording();
        recordingWithMetronome = initializeMetronome(true);
        Thread playPreCount = playClick(4);


        playPreCount.start();

        try { // waits for 4 clicks, then start recording
            playPreCount.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        recordAudioThread.start();
        recordingWithMetronome.start();

    }

    private void stopRecording() {

        if (soundRecorder != null && isRecording && recordingWithMetronome != null && recordingWithMetronome.isAlive() && !reachedEndOfRecordTime) {

            Log.d("sstop1", "stopRecording: ");
            isRecording = false;
            recordingWithMetronome.interrupt();
            soundRecorder.stopRecording();
        }


        if (soundRecorder != null && isRecording && reachedEndOfRecordTime) {

            Log.d("sstop2", "stopRecording: ");
            isRecording = false;
            analyseEnabled = true;
            reachedEndOfRecordTime = false;

            soundRecorder.stopRecording();
        }

    }

    private Thread playClick(final int beats) {

        final SoundPool soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        final int soundId = soundPool.load(this, R.raw.metronome_click, 1);

        return new Thread(new Runnable() {
            public void run() {

                for (int i = 0; i < beats; i++) {

                    soundPool.play(soundId, 1, 1, 1, 1, 1);
                    try {
                        Thread.currentThread().sleep(tick.milliSeconds, tick.nanoSeconds);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        }, "Click Thread");

    }

    private Thread initializeMetronome(final boolean withRecording) {

        final Handler handler = new Handler(getApplicationContext().getMainLooper());

        return new Thread(new Runnable() {

            public void run() {

                for (int i = 0; i < homework.getBeats() ; i++) {

                    final int counter = i;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            metronome.setChecked(!metronome.isChecked());
                            metronomeCounter.setText("4/" + String.valueOf((counter % 4) + 1));
                        }
                    });
                    try {
                        Thread.currentThread().sleep(tick.milliSeconds, tick.nanoSeconds);
                    } catch (InterruptedException e) {
                        break;
                    }

                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        metronomeCounter.setText("4/4");
                    }
                });

                if (withRecording) {
                    reachedEndOfRecordTime = true;
                    stopRecording();
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        recStopButton.setText("Rec");
                    }
                });

            }
        }, "Metronome Thread");

    }

    private void practiceMetronome() {

        practiceMetronome = initializeMetronome(false);
        practiceClick = playClick(homework.getBeats());

        practiceMetronome.start();
        practiceClick.start();
    }

    private void stopPracticeMetronome() {

        if (this.practiceMetronome != null && this.practiceClick != null) {
            practiceMetronome.interrupt();
            practiceClick.interrupt();
        }
    }

    private void analyseRecord() {

        soundAnalyser = new SoundAnalyser(homework, this, SAMPLE_RATE, PATH_NAME);
        soundAnalyser.analyseRecord();

        progressBar.setVisibility(View.VISIBLE);
    }

    class Tick {

        float tick; //tempo means beats per minute, calculate milliseconds between ticks
        long milliSeconds; // Thread.sleep() 2 params: milliseconds long, nanoseconds int
        int nanoSeconds; // milli = 10^-3, nano = 10^-9, difference is 10^6 = 1000000

        public Tick(int bpm) {
            tick = (60f / bpm) * 1000;
            milliSeconds = (long) tick;
            nanoSeconds = (int) ((tick - (int) tick) * 1000000);
        }

    }
}
