package guitartutorandanalyser.guitartutor;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;



public class GuitarTutorMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guitar_tutor_main);

        // on first run valami db is created on the system for songs and lessons, method checks first if database already exists
        try {
            DatabaseHelper dbh = new DatabaseHelper(this);
            dbh.createDataBase();

          //  Log.d("cromatic sound id", String.valueOf( this.getResources().getIdentifier("song_chromatic_scale_a_90", "raw", this.getPackageName())));

            dbh.UPDATE_DB_toDelete(); // delete this line

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
        // ide egy lista kéne az elérhető songokról és lessonokról, aztán betölteni egy uj nézete, ahol már valami legjobb pont dátum?
    }

    public void onButtonHelpClick(View v) {
        startActivity(new Intent("guitartutorandanalyser.guitartutor.UserGuide"));
    }

}
