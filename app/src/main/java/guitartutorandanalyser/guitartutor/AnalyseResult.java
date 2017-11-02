package guitartutorandanalyser.guitartutor;

import android.icu.util.Calendar;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class AnalyseResult extends AppCompatActivity {

    ProgressBar progressbar;
    TextView resultTextGratulate, resultTextName, resultTextScore, resultTextFeedback;
    int progressBarStatus;

    DatabaseHelper dbHelper ;
    HomeWork currentHomework;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyse_result);

        dbHelper = new DatabaseHelper(this);

        String result = getIntent().getStringExtra("result");
        String _id = getIntent().getStringExtra("homeworkId");

        this.currentHomework = dbHelper.fetchHomeworkById(Integer.valueOf(_id));

        resultTextGratulate = (TextView) findViewById(R.id.resultTextGratulate);
        resultTextName = (TextView) findViewById(R.id.resultTextName);
        resultTextScore = (TextView) findViewById(R.id.resultTextScore);
        resultTextFeedback = (TextView) findViewById(R.id.resultTextFeedback);
        progressbar = (ProgressBar) findViewById(R.id.progressBarResult);

        updateDataBase(Integer.parseInt(result));

        showProgress(Integer.parseInt(result)).start();

    }

    private Thread showProgress(final int result) {

        final Handler handler = new Handler();
        return new Thread(new Runnable() {
            @Override
            public void run() {


                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        resultTextName.setText("Feladat: " + currentHomework.getName());
                        resultTextScore.setText("Legjobb pontszám: " + String.valueOf(currentHomework.getRecordpoint()));
                    }
                });

                for (int i = 0; i <= 100; i++) {
                    progressBarStatus++;
                    android.os.SystemClock.sleep(15);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressbar.setProgress(progressBarStatus);
                        }
                    });
                }

                for (int j = 100; j >= result; j--) {
                    progressBarStatus--;
                    android.os.SystemClock.sleep(15);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressbar.setProgress(progressBarStatus);
                        }
                    });
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        resultTextGratulate.setText("Gratulálunk, pontszámod: " + result + "/100");
                    }
                });


                for (int i = 0; i < 3; i++) {
                    android.os.SystemClock.sleep(700);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            resultTextFeedback.setText("");
                        }
                    });

                    android.os.SystemClock.sleep(700);

                    if (result >= 70) {
                        if (result > currentHomework.getRecordpoint()) {

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    resultTextFeedback.setText("Feladat teljesítve! ***ÚJ CSÚCS!***");
                                }
                            });

                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    resultTextFeedback.setText("Feladat teljesítve!");
                                }
                            });
                        }
                    }
                }

            }
        });

    }

    private void updateDataBase(int result) {

        boolean updateHomework = false;

        Log.d("homework", String.valueOf( currentHomework.getRecordpoint()) + "   ...  "+String.valueOf(currentHomework.getCompleted()));

        if (result > currentHomework.getRecordpoint()) {
            currentHomework.setRecordpoint(result);
            updateHomework = true;

            java.util.Date d = new java.util.Date();

            d.getTime();
            try {
                Log.d("datum", d.toString() + "   ...  " + (new SimpleDateFormat("yyyy-MM-dd")).parse(d.toString()));
            }catch (Exception e){}

            currentHomework.setRecordDate(d.toString());
        }



        if (result >= 70 && currentHomework.getCompleted() == 0) {
            currentHomework.setCompleted(1);
            updateHomework = true;
        }

        if (updateHomework) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    //  dbHelper.updateDatabase(currentHomework);
                }
            }).start();
        }

    }

}
