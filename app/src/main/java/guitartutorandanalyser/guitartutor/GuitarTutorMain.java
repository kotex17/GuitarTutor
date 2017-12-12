package guitartutorandanalyser.guitartutor;


import android.content.DialogInterface;
import android.content.Intent;

import android.os.Environment;
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
