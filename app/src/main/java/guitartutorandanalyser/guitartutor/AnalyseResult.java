package guitartutorandanalyser.guitartutor;

import android.content.Intent;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.text.format.DateFormat;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Date;

public class AnalyseResult extends AppCompatActivity {

    ProgressBar progressbar;
    TextView resultTextGratulate, resultTextName, resultTextScore, resultTextFeedback, resultTextNewRecord;
    int progressBarStatus;
    String feedback;

    DatabaseHelper dbHelper;
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
        resultTextNewRecord = (TextView) findViewById(R.id.resultTextNewRecord);

        progressbar = (ProgressBar) findViewById(R.id.progressBarResult);


        Thread progress = showProgress(Integer.parseInt(result));
        progress.start();

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
                        resultTextScore.setText("Eddigi legjobb pontszám: " + String.valueOf(currentHomework.getRecordpoint()));
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


                if (result < 50) {

                    feedback ="Próbáld újra! Gyakorlás teszi a mestert, ne add fel!";

                } else if (result >= 50 && result < 70) {

                    feedback = "Nagyon közel! Még egy kis türelem és menni fog!";

                } else if (result >= 70) {

                    feedback = "Feladat teljesítve!";
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        android.os.SystemClock.sleep(700);
                        resultTextFeedback.setText(feedback);
                    }
                });

                if (result > currentHomework.getRecordpoint()) {

                    for (int i = 0; i < 4; i++) {

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                resultTextNewRecord.setText("");
                            }
                        });


                        android.os.SystemClock.sleep(700);

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                resultTextNewRecord.setText("*** ÚJ CSÚCS ***");
                            }
                        });

                        android.os.SystemClock.sleep(700);
                    }
                }

                updateHomeWork(result);
            }
        });

    }

    private void updateHomeWork(int result) {

        boolean updateNeeded = false;

        if (result > currentHomework.getRecordpoint()) {

            currentHomework.setRecordpoint(result);

            CharSequence date = DateFormat.format("yyyy.MM.dd.", new Date().getTime());
            currentHomework.setRecordDate(date.toString());

            updateNeeded = true;
        }


        if (result >= 70 && currentHomework.getCompleted() == 0) {

            currentHomework.setCompleted(1);
            updateNeeded = true;
        }

        if (updateNeeded) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    dbHelper.updateDatabaseRecord(currentHomework);
                }
            }).start();
        }

    }

    public void backToStartMenu(View v) {

        Intent intent = new Intent(this, GuitarTutorMain.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void tryAgain(View v) {

        finish();
    }

}
