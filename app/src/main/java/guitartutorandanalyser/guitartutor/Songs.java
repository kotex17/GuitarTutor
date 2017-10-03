package guitartutorandanalyser.guitartutor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Songs extends AppCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs);

        listView = (ListView)findViewById(R.id.songsListView);

        populateListView();
        onListItemClick();
    }

    private void populateListView() {

        String[] items = {"Artist1 - Song1", "Artist2 - Song2"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,                   //context
                R.layout.list_songs,    //layout
                items                   //items
        );

        listView.setAdapter(adapter);
    }

    private void onListItemClick(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                TextView textView = (TextView)viewClicked;

                startActivity(new Intent("guitartutorandanalyser.guitartutor.Tutor"));
            }
        });
    }
}
