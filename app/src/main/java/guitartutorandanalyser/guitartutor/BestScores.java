package guitartutorandanalyser.guitartutor;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.sql.SQLException;
import java.util.ArrayList;

public class BestScores extends AppCompatActivity {

    private ListView listView;
    private DatabaseHelper dbh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_best_scores);

        dbh = new DatabaseHelper(this);

        listView = (ListView) findViewById(R.id.bestScoresListView);



    }

    @Override
    protected void onResume() {
        super.onResume();

        populateListView();

        onListItemClick();
    }

    private void populateListView() {

        ArrayList<String[]> listHomeworks = new ArrayList<>();


        try {
            dbh.openDataBase();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Cursor cursor = dbh.myDataBase.query(DatabaseHelper.TABLE_HOMEWORKS,
                new String[]{DatabaseHelper.Column.ID,
                        DatabaseHelper.Column.NAME,
                        DatabaseHelper.Column.RECORDPOINT,
                        DatabaseHelper.Column.RECORDDATE,
                        DatabaseHelper.Column.COMPLETED},
                null, null, null, null, null);

        Log.d("cursor count",String .valueOf(cursor.getCount()));

        String[] items = new String[cursor.getCount()];
        int i = 0;


        while (cursor.moveToNext()) {
         //   listHomeworks.add(new String[]{String.valueOf(cursor.getInt(0)), cursor.getString(1), String.valueOf(cursor.getInt(2)), cursor.getString(3), String.valueOf(cursor.getInt(4))});
            items[i] = cursor.getString(1) + " \t\t\t " + String.valueOf(cursor.getInt(2)) + " \t\t\t " +cursor.getString(3) + " \t\t\t " + String.valueOf(cursor.getInt(4));
            i++;
        }



        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,                   //context
                R.layout.list_songs,    //layout
                items               //items
        );

        listView.setAdapter(adapter);

        dbh.close();
        cursor.close();


    }

    private void onListItemClick() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {

               /* Intent tutorIntent = new Intent("guitartutorandanalyser.guitartutor.AnalyseResult");
                tutorIntent.putExtra("homeWorkId",idToSongName.keyAt(position));
                startActivity(tutorIntent);
*/
            }
        });
    }
}
