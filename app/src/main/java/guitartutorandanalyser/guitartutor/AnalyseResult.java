package guitartutorandanalyser.guitartutor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class AnalyseResult extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyse_result);

        String result = getIntent().getStringExtra("result");
        String _id = getIntent().getStringExtra("homeworkId");

        ((TextView) findViewById(R.id.result_text)).setText("gratulálok, a pontszámod: "+result +"/100");

    }
}
