package guitartutorandanalyser.guitartutor;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class Songs extends AppCompatActivity {

    private ListView listView;
    ArrayMap<String, String> idToSongName = new ArrayMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs);

        listView = (ListView) findViewById(R.id.songsListView);

        idToSongName = populateListView();
        onListItemClick();
    }

    private ArrayMap<String, String> populateListView() {

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

        return idToSongName;
    }

    private ArrayMap<String, String> fetchDatabase() {

        DatabaseHelper db = new DatabaseHelper(this);
        try {
            db.openDataBase();
        } catch (Exception e) {

        }

        Cursor cursor = db.myDataBase.query(DatabaseHelper.TABLE_HOMEWORKS, new String[]{DatabaseHelper.Column.ID, DatabaseHelper.Column.TYPE, DatabaseHelper.Column.NAME, DatabaseHelper.Column.COMPLETED}, DatabaseHelper.Column.TYPE + " = ?", new String[]{"song"}, null, null, null);

        ArrayMap<String, String> idToSongName = new ArrayMap<String, String>();

        while (cursor.moveToNext()) {

            String isCompleted = "";

            if (cursor.getInt(3) == 1)
                isCompleted = "✓";

            idToSongName.put(String.valueOf(cursor.getInt(0)), cursor.getString(2) + " \t\t" + isCompleted);
        }

        cursor.close();
        db.close();

        return idToSongName;
    }

    private void onListItemClick() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {

                Intent tutorIntent = new Intent("guitartutorandanalyser.guitartutor.Tutor");
                tutorIntent.putExtra("homeWorkId", idToSongName.keyAt(position));
                startActivity(tutorIntent);
            }
        });
    }
}
