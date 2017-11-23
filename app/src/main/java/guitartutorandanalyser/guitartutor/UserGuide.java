package guitartutorandanalyser.guitartutor;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;


public class UserGuide extends AppCompatActivity {

    TextView record, game, analyse, homework, help;
    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_guide);


        record = (TextView) findViewById(R.id.userGuide_Records_TextView);
        game = (TextView) findViewById(R.id.userGuide_Game_TextView);
        analyse = (TextView) findViewById(R.id.userGuide_Analyse_TextView);
        homework = (TextView) findViewById(R.id.userGuide_Lessons_TextView);
        homework.setText("Dalok & Leckék");

        scrollView =   (ScrollView) findViewById(R.id.userGuide_scrollView);
        help = (TextView) findViewById(R.id.userGuide_Help_textView);

        onTouch();

    }


    public void onTouch() {

        homework.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                scrollView.setScrollY(170);
                return true;
            }
        });
        game.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                scrollView.setScrollY(300);
                return true;
            }
        });
        analyse.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                scrollView.setScrollY(1000);
                return true;
            }
        });
        record.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                scrollView.setScrollY(1000);
                return true;
            }
        });

    }

    public void backToStartMenu(View v) {

        Intent intent = new Intent(this, GuitarTutorMain.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
