package guitartutorandanalyser.guitartutor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class GuitarTutorMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guitar_tutor_main);
    }

    public void onButtonSongsClick(View v){
        startActivity(new Intent("guitartutorandanalyser.guitartutor.Songs"));
    }

    public void onButtonLessonsClick(View v){
asd
    }

    public void onButtonRecordsClick(View v){

    }

    public void onButtonHelpClick(View v){

    }
}
