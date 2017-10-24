package guitartutorandanalyser.guitartutor;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AnalyseResult extends AppCompatActivity {

    ProgressBar progressbar;
    TextView textView;
    int progressBarStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyse_result);

        String result = getIntent().getStringExtra("result");
        String _id = getIntent().getStringExtra("homeworkId");

        textView = (TextView) findViewById(R.id.result_text);
        progressbar = (ProgressBar) findViewById(R.id.progressBarResult);

        showProgress(Integer.parseInt(result)).start();

    }

    private Thread showProgress(final int result) {

        final Handler handler = new Handler();
        return new Thread(new Runnable() {
            @Override
            public void run() {

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

                for (int j = 100; j >= result; j--){
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
                        textView.setText("Gratulálunk, pontszámod: "+result+"/100");
                    }
                });

            }
        });

    }

}
