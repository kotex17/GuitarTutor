package guitartutorandanalyser.guitartutor;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.sql.SQLException;

public class GuitarTutorMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guitar_tutor_main);

        // on first run a db is created on the system for songs and lessons, method checks first if database already exists
        try {
           DatabaseHelper dbh = new DatabaseHelper(this);
            dbh.createDataBase();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onButtonSongsClick(View v){
        startActivity(new Intent("guitartutorandanalyser.guitartutor.Songs"));
    }

    public void onButtonLessonsClick(View v){
        startActivity(new Intent("guitartutorandanalyser.guitartutor.Lessons"));
    }

    public void onButtonRecordsClick(View v){

    }

    public void onButtonHelpClick(View v){

    }
}
