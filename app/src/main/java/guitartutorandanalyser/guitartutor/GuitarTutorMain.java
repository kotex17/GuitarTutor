package guitartutorandanalyser.guitartutor;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.sql.SQLException;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.io.android.AndroidFFMPEGLocator;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;

public class GuitarTutorMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guitar_tutor_main);

        // on first run a db is created on the system for songs and lessons, method checks first if database already exists
        try {
            DatabaseHelper dbh = new DatabaseHelper(this);
            dbh.createDataBase();

           // dbh.UPDATE_DB_toDelete(); // delete this line

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onButtonSongsClick(View v) {
        startActivity(new Intent("guitartutorandanalyser.guitartutor.Songs"));
    }

    public void onButtonLessonsClick(View v) {
        startActivity(new Intent("guitartutorandanalyser.guitartutor.Lessons"));
    }

    public void onButtonRecordsClick(View v) {
        // ide egy lista kéne az elérhető songokról és lessonokról, aztán betölteni egy uj nézete, ahol már a legjobb pont dátum?
    }

    public void onButtonHelpClick(View v) {
        startActivity(new Intent("guitartutorandanalyser.guitartutor.UserGuide"));
    }


    String map = "";

    private boolean creatMapUpdateDBrow_toDelete(int _id, String PATH_NAME) {
        int SAMPLE_RATE = 44100;


        new AndroidFFMPEGLocator(this);

        final AudioDispatcher dispatcher = AudioDispatcherFactory.fromPipe(PATH_NAME, SAMPLE_RATE, 8192, 0);  //   BUFFER_SIZE / 2 is ineccessary?? need some test!!!

        /*      File mapFile = new File(Environment.getExternalStorageDirectory() + "/chromatic_scale_a_90bpmMAP3.txt");
        if (mapFile.exists())
           mapFile.delete();

            */
        // audiofactory(from pipe, wav header is ignored, pcm samples captured) ---> audiodispathcer(plays a file and sends float arrays to registered AudioProcessor )
        // AudioProcessor = PitchProcessor, PitchdetectionHandler = Interface( handlePitch())

        dispatcher.addAudioProcessor(new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, SAMPLE_RATE, 8192, new PitchDetectionHandler() {

            @Override
            public void handlePitch(PitchDetectionResult pitchDetectionResult,
                                    AudioEvent audioEvent) {
                final int pitchInHz = (int) pitchDetectionResult.getPitch();
                // int is enough, because guitar has freats, so you cant go wrong(its not a violing or an instrument without freats)
                try {
                    map += String.valueOf(pitchInHz) + ';';
                    // Log.d("creating new map", String.valueOf(pitchInHz));

                     /*                FileWriter fw = new FileWriter(Environment.getExternalStorageDirectory() + "/chromatic_scale_a_90bpmMAP3.txt", true);
                    fw.write(String.valueOf(pitchInHz) + ";");
                    fw.close();
*/

                } catch (Exception e) {

                }
            }
        }));


        Thread audioDispathcerThread = new Thread(dispatcher, "Audio Dispatcher");

        audioDispathcerThread.start();
        long a = System.nanoTime();

        while (audioDispathcerThread.getState() != Thread.State.TERMINATED) {

        }

        long b = System.nanoTime() - a;
        Log.d("ido ido ido ", String.valueOf(b));

        Log.d("map map map", map);

      /*  try {

            DatabaseHelper dbh = new DatabaseHelper(this);

            dbh.openDataBase();


            Cursor cursor = dbh.myDataBase.query(
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
                    new String[]{String.valueOf(_id)},
                    null, null, null, "1"); // LIMIT 1

            HomeWork homeWork = null;

            if (cursor.moveToFirst()) {
                homeWork = HomeWork.homeWorkCreator(
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


            ContentValues contentValues = new ContentValues();
            contentValues.put("_id", homeWork.get_id());
            contentValues.put("type", homeWork.getType());
            contentValues.put("name", homeWork.getName());
            contentValues.put("bpm", homeWork.getBpm());
            contentValues.put("beats", homeWork.getBeats());
            contentValues.put("recordpoint", homeWork.getRecordpoint());
            contentValues.put("recorddate", homeWork.getRecordDate());
            contentValues.put("completed", homeWork.getCompleted());
            contentValues.put("map", map);
            contentValues.put("tabid", homeWork.getTabId());
            contentValues.put("songid", homeWork.getSoundId());


            dbh.close();
          //  dbh.myDataBase.update("homeworks", contentValues, "_id = ?", new String[]{String.valueOf(_id)});

        } catch (Exception e) {
            return false;
        }*/

        return true;
    }
}
