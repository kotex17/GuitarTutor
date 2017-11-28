package guitartutorandanalyser.guitartutor;


import android.content.DialogInterface;
import android.content.Intent;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class GuitarTutorMain extends AppCompatActivity {

    DatabaseHelper dbh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guitar_tutor_main);



        // on first run valami db is created on the system for songs and lessons, method checks first if database already exists
        try {
            dbh = new DatabaseHelper(this);
            dbh.createDataBase();

/*
            Log.d("c sound id", String.valueOf( this.getResources().getIdentifier("lesson_beg_chromatic_scale_a_90", "raw", this.getPackageName())));
            Log.d("c tab id", String.valueOf( this.getResources().getIdentifier("tab_chromatic_scale_a_90", "drawable", this.getPackageName())));
            Log.d("s sound id", String.valueOf( this.getResources().getIdentifier("song_starwars_theme_102", "raw", this.getPackageName())));
            Log.d("s tab id", String.valueOf( this.getResources().getIdentifier("tab_starwars_theme_102", "drawable", this.getPackageName())));


            Log.d("m1 sound id", String.valueOf( this.getResources().getIdentifier("lesson_beg_minor_scale_g_60", "raw", this.getPackageName())));
            Log.d("m1 tab id", String.valueOf( this.getResources().getIdentifier("tab_minor_scale_g_60", "drawable", this.getPackageName())));
            Log.d("m2 sound id", String.valueOf( this.getResources().getIdentifier("lesson_exp_minor_scale_g_90", "raw", this.getPackageName())));
            Log.d("m2 tab id", String.valueOf( this.getResources().getIdentifier("tab_minor_scale_g_90", "drawable", this.getPackageName())));


            Log.d("p sound id", String.valueOf( this.getResources().getIdentifier("lesson_beg_pentaton_scale_f_75", "raw", this.getPackageName())));
            Log.d("p tab id", String.valueOf( this.getResources().getIdentifier("tab_pentaton_scale_f_75", "drawable", this.getPackageName())));
            Log.d("b sound id", String.valueOf( this.getResources().getIdentifier("lesson_exp_blues_scale_f_75", "raw", this.getPackageName())));
            Log.d("b tab id", String.valueOf( this.getResources().getIdentifier("tab_blues_scale_f_75", "drawable", this.getPackageName())));
*/

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

        startActivity(new Intent("guitartutorandanalyser.guitartutor.BestScores"));
    }

    public void onButtonHelpClick(View v) {

        startActivity(new Intent("guitartutorandanalyser.guitartutor.UserGuide"));
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Elhagyod a Gitár Oktatót?")
                .setCancelable(false)
                .setPositiveButton("Igen", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        GuitarTutorMain.this.finish();
                    }
                })
                .setNegativeButton("Mégse", null)
                .show();
    }

}
