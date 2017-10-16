package guitartutorandanalyser.guitartutor;

import android.app.ProgressDialog;
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
import android.support.v7.view.menu.ExpandedMenuView;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.io.android.AndroidFFMPEGLocator;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;

public class Tutor extends AppCompatActivity {


    final int SAMPLE_RATE = 44100;
    final String PATH_NAME = Environment.getExternalStorageDirectory() + "/GuitarTutorRec.wav";   // "/chromatic_scale_a_90bpm.wav";
    final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT) / 2;


    int recSizeInByte;
    boolean isSoundRecording;
    boolean isSoundPlaying;

    Button recButton;
    AudioRecord recorder;
    MediaPlayer mediaPlayer;
    Handler handler;

    RadioButton metronome;

    int tempTempo = 90;

    SoundAnalysator soundAnalysator;

    HomeWork homework;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor);

        String s = getIntent().getStringExtra("lessonId");
        fetchCurrentHomework(Integer.parseInt(s));

        Log.d("HOMEWORK DATA ", homework.getName() + " " + homework.getType() + " " + homework.getBpm());
        Log.d("chromatic scale soindid", String.valueOf(R.raw.chromatic_scale_a_90bpm));

        metronome = (RadioButton) findViewById(R.id.metronomeButton);
        recButton = (Button) findViewById(R.id.button_play_stop);
        ImageView imageView = (ImageView) findViewById(R.id.imageView_tab);
        imageView.setImageResource(R.drawable.orarend);
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

        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT, BUFFER_SIZE);

        recorder.startRecording();
        isSoundRecording = true;

        Thread recordAudioThread = new Thread(new Runnable() {

            public void run() {
                writeAudioDataToFile();
            }
        }, "AudioRecorder Thread");


        playPreCount();

        recordAudioThread.start();

        startMetronome();
    }

    private void playPreCount() {

        SoundPool soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0); // 4, AudioManager.STREAM_MUSIC, 0);
        int soundId = soundPool.load(getApplicationContext(), R.raw.metronome_click, 1);

        float tick = (60f / tempTempo) * 1000;

        for (int i = 0; i < 4; i++) {

            long time1 = SystemClock.elapsedRealtime();
            soundPool.play(soundId, 0.5f, 0.5f, 1, 1, 1);

            while (SystemClock.elapsedRealtime() - time1 <= tick) {
                // waiting ...
            }
        }
    }

    private void startMetronome() {

        final float tick2 = (60f / tempTempo) * 1000; //tempo means beats per minute, calculate milliseconds between ticks

        handler = new Handler(getApplicationContext().getMainLooper());

        Thread startMetronome = new Thread(new Runnable() {

            public void run() {

                long tickMilliSeconds = (long) tick2;  // Thread.sleep() 2 params: milliseconds  long, nanoseconds  int
                float calculateNanoseconds = (tick2 - (int) tick2) * 1000000;  // milli = 10^-3, nano = 10^-9, difference is 10^6 = 1000000
                int tickNanoSeconds = (int) calculateNanoseconds;

                while (isSoundRecording) {
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

            }
        }, "Metronome Thread");

        startMetronome.start();
    }

    private void writeAudioDataToFile() {
        // Read audio data into short buffer, Write the output audio in byte

        short sData[] = new short[BUFFER_SIZE];

        try {
            // first create a file with dummy wav header

            FileOutputStream outStream = new FileOutputStream(PATH_NAME);
            outStream.write(createWavHeader());

            while (isSoundRecording) {
                // read audio from microphone, short format
                recorder.read(sData, 0, BUFFER_SIZE);

                // writes audio data into file from buffer
                byte bData[] = short2byte(sData);
                outStream.write(bData, 0, BUFFER_SIZE * 2);
                recSizeInByte += bData.length;
            }

            outStream.close();
        } catch (IOException e) {
            Log.d("Audio record error", e.getMessage().toString());
        }
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

    private byte[] createWavHeader() {
        // The default byte ordering assumed for WAVE data files is little-endian. Files written using the big-endian byte ordering scheme have the identifier RIFX instead of RIFF.

        short channels = 1; //mono
        short bitsPerSample = 16; // pcm 16bit

        //   int subchunk2Size = (int) recordedAudioLength; // int enough, recorded audio is max a few minutes
        //   int chunkSize = (int) (recordedAudioLength + 36); //wave file header is 44 bytes, first 4 is ChunkId, second 4 bytes are ChunkSize: 44 - 8 = 36 bytes

        byte[] headerBytes = ByteBuffer
                .allocate(14)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putShort(channels)
                .putInt(SAMPLE_RATE)
                .putInt(SAMPLE_RATE * channels * (bitsPerSample / 8))
                .putShort((short) (channels * (bitsPerSample / 8)))
                .putShort(bitsPerSample)
                .array();

        byte[] header = new byte[]{
                'R', 'I', 'F', 'F', // ChunkID
                0, 0, 0, 0, // ChunkSize
                'W', 'A', 'V', 'E', // Format
                'f', 'm', 't', ' ', // Subchunk1ID
                16, 0, 0, 0, // Subchunk1Size
                1, 0, // audioformat: 1=PCM
                headerBytes[0], headerBytes[1], // No. of channels 1= mono, 2 = stereo
                headerBytes[2], headerBytes[3], headerBytes[4], headerBytes[5], // SampleRate
                headerBytes[6], headerBytes[7], headerBytes[8], headerBytes[9], // ByteRate
                headerBytes[10], headerBytes[11], // BlockAlign
                headerBytes[12], headerBytes[13], // BitsPerSample
                'd', 'a', 't', 'a', // Subchunk2ID
                0, 0, 0, 0 // Subchunk2Size
        };

        recSizeInByte += header.length;

        return header;
    }


    private void stopRecording() {

        if (recorder != null) {

            recorder.stop();
            recorder.release();
            recorder = null;
            isSoundRecording = false;
        }

        updateWavHeader();
    }

    private void updateWavHeader() {

        byte[] bytesToUpdate = ByteBuffer
                .allocate(8)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putInt(recSizeInByte - 8) // ChunkSize
                .putInt(recSizeInByte - 44) // Subchunk2Size
                .array();

        try {
            RandomAccessFile randomAccesFile = new RandomAccessFile(PATH_NAME, "rw");

            randomAccesFile.seek(4);
            randomAccesFile.write(bytesToUpdate, 0, 4);

            randomAccesFile.seek(40);
            randomAccesFile.write(bytesToUpdate, 4, 4);

            randomAccesFile.close();

        } catch (IOException e) {

        }


    }

    private void analyseRecord() {

        soundAnalysator = new SoundAnalysator(homework);
        soundAnalysator.analyseRecord(SAMPLE_RATE, BUFFER_SIZE, PATH_NAME);

        ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar);
        pb.setVisibility(View.VISIBLE);

        soundAnalysator.compareResult(); // not implemented ye

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
