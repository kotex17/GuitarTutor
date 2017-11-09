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


public class Tutor extends AppCompatActivity {


    final int SAMPLE_RATE = 44100;
    final String PATH_NAME = Environment.getExternalStorageDirectory() + "/GuitarTutorRec.wav";//

    boolean isSoundPlaying, isRecording, isRecordPlaying;

    Button playStopButton, recStopButton, playRecStopRecButton;
    MediaPlayer mediaPlayer;
    RadioButton metronome;

    SoundRecorder soundRecorder;
    SoundAnalyser soundAnalyser;
    HomeWork homework;
    ProgressBar progressBar;

    Tick tick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor);

        String parameter = getIntent().getStringExtra("homeWorkId");

        this.homework = (new DatabaseHelper(this)).fetchHomeworkById(Integer.parseInt(parameter)); //fetchCurrentHomework(Integer.parseInt(s));

        tick = new Tick(homework.getBpm());

        metronome = (RadioButton) findViewById(R.id.metronomeButton);
        playStopButton = (Button) findViewById(R.id.button_play_stop);
        recStopButton = (Button) findViewById(R.id.button_rec_stop);
        playRecStopRecButton = (Button) findViewById(R.id.button_playrec_stoprec);

        ((ImageView) findViewById(R.id.imageView_tab)).setImageResource(homework.getTabId());
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    public void onButtonPlayClick(View v) {

        if ( !isSoundPlaying && !isRecordPlaying) {
            startSoundPlay();
        } else if (!isRecordPlaying) {
            stopSoundPlay();
        }
    }

    public void onButtonAnalyseClick(View v) {
        analyseRecord();
    }

    public void onButtonRecClick(View v) {

        if (soundRecorder != null && isRecording) {

            recStopButton.setText("Rec");
            stopRecording();
        } else {

            recStopButton.setText("Stop rec");
            startRecording();
        }

    }

    public void onButtonPlayRecStopRecClick(View v) {

        if ( !isRecordPlaying && !isSoundPlaying ) {
            startRecordPlay();
        } else if (!isSoundPlaying) {
            stopRecordPlay();
        }
    }

    private void startRecordPlay() {

        mediaPlayer = MediaPlayer.create(this, Uri.parse(PATH_NAME));
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                playRecStopRecButton.setText("Play Rec");
                isRecordPlaying = false;
                mediaPlayer.release();
            }

        });

        mediaPlayer.start();
        isRecordPlaying = true;
        playRecStopRecButton.setText("Stop");
    }

    private void stopRecordPlay() {

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            isRecordPlaying = false;
            playRecStopRecButton.setText("Play Rec");
        }
    }

    private void startSoundPlay() {

        mediaPlayer = MediaPlayer.create(this, homework.getSoundId());
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                playStopButton.setText("Play Song");
                isSoundPlaying = false;
                mediaPlayer.release();
            }

        });

        mediaPlayer.start();
        isSoundPlaying = true;
        playStopButton.setText("Stop");

    }

    private void stopSoundPlay() {

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            isSoundPlaying = false;
            playStopButton.setText("Play Song");
        }
    }

    private void startRecording() { // creates new audio recorder instance, start it, new recording thread and new metronome

        soundRecorder = new SoundRecorder();

        isRecording = true;


        Thread recordAudioThread = soundRecorder.startRecording();
        Thread startMetronome = initializeMetronome();
        Thread playPreCount = playPreCount();


        playPreCount.start();

        try {
            playPreCount.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        recordAudioThread.start();
        startMetronome.start();
    }

    private void stopRecording() {

        if (soundRecorder != null) {

            soundRecorder.stopRecording();
            isRecording = false;
        }
    }

    private Thread playPreCount() {

        final SoundPool soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0); // 1?????????
        final int soundId = soundPool.load(this, R.raw.metronome_click, 1);


        return new Thread(new Runnable() {
            public void run() {

                for (int i = 0; i < 4; i++) {

                    soundPool.play(soundId, 1, 1, 1, 1, 1);
                    try {
                        Thread.currentThread().sleep(tick.milliSeconds, tick.nanoSeconds);
                    } catch (InterruptedException e) {
                        Log.d("PreCount thread error", e.getMessage());
                    }
                }
            }
        }, "PreCount Thread");
    }

    private Thread initializeMetronome() {

        final Handler handler = new Handler(getApplicationContext().getMainLooper());

        return new Thread(new Runnable() {

            public void run() {

                for (int i = 0; i < homework.getBeats() && soundRecorder.isSoundRecording(); i++) {

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            metronome.setChecked(!metronome.isChecked());
                        }
                    });
                    try {
                        Thread.currentThread().sleep(tick.milliSeconds, tick.nanoSeconds);
                    } catch (InterruptedException e) {
                        Log.d("Metronome thread error", e.getMessage());
                    }

                }
                stopRecording();
            }
        }, "Metronome Thread");

    }

    private void analyseRecord() {

        soundAnalyser = new SoundAnalyser(homework, this, SAMPLE_RATE, PATH_NAME);
        soundAnalyser.analyseRecord();

        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPause() {

        super.onPause();
        stopRecording();
        stopSoundPlay();
        stopRecordPlay();
        progressBar.setVisibility(View.INVISIBLE);
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
