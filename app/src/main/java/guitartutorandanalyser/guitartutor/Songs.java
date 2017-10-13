package guitartutorandanalyser.guitartutor;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class Songs extends AppCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs);

        listView = (ListView) findViewById(R.id.songsListView);

        populateListView();
        onListItemClick();
    }

    private void populateListView() {

        ArrayMap<String, String> idToSongName = fetchDatabase();

        String[] items = new String[idToSongName.keySet().size()];
        int i = 0;
        for (String item : idToSongName.values()) {
            items[i] = item;
            i++;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,                   //context
                R.layout.list_songs,    //layout
                items               //items
        );

        listView.setAdapter(adapter);
    }

    private ArrayMap<String, String> fetchDatabase() {

        DatabaseHelper db = new DatabaseHelper(this);
        try {
            db.openDataBase();
        } catch (Exception e) {

        }

        Cursor cursor = db.myDataBase.query("homeworks", new String[]{"_id", "type", "name"}, "type = ?", new String[]{"song"}, null, null, null);

        ArrayMap<String, String> idToSongName = new ArrayMap<String, String>();

        while (cursor.moveToNext()) {
            idToSongName.put(String.valueOf(cursor.getInt(0)), cursor.getString(2));
        }

        cursor.close();
        db.close();

        return idToSongName;
    }


    private void onListItemClick() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                TextView textView = (TextView) viewClicked;

                startActivity(new Intent("guitartutorandanalyser.guitartutor.Tutor"));
            }
        });
    }
}
