package guitartutorandanalyser.guitartutor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class Lessons extends AppCompatActivity {
    ListView listViewKezdo;
    ListView listViewHalado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lessons);

        listViewKezdo = (ListView)findViewById(R.id.listViewLessonsKezdo);
        listViewHalado = (ListView)findViewById(R.id.listViewLessonsHalado);
        populateListView();
        onListItemClick();
    }

    private void populateListView() {

        String[] items = {"Artist1 - Song1", "Artist2 - Song2"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,                   //context
                R.layout.list_lessons,    //layout
                items                   //items
        );
        listViewKezdo.setAdapter(adapter);

        String[] items2 = {"Artist3 - Song3", "Artist4 - Song4"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(
                this,                   //context
                R.layout.list_lessons,    //layout
                items2                   //items
        );

        listViewHalado.setAdapter(adapter2);
    }

    private void onListItemClick(){

        listViewKezdo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                TextView textView = (TextView)viewClicked;

                startActivity(new Intent("guitartutorandanalyser.guitartutor.Tutor"));
            }
        });

        listViewHalado.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                TextView textView = (TextView)viewClicked;

                startActivity(new Intent("guitartutorandanalyser.guitartutor.Tutor"));
            }
        });
    }
}
