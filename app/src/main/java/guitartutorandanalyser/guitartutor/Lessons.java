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

public class Lessons extends AppCompatActivity {
    ListView listViewBeginner;
    ListView listViewExpert;

    ArrayMap<String, String> idToLessonName_beg = new ArrayMap<String, String>(); // list of beginner lessons, maped to their database ID
    ArrayMap<String, String> idToLessonName_exp = new ArrayMap<String, String>(); // list of expert lessons, maped to their database ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lessons);

        listViewBeginner = (ListView)findViewById(R.id.listViewLessonsKezdo);
        idToLessonName_beg = populateListView(listViewBeginner,"lesson_beg");

        listViewExpert = (ListView)findViewById(R.id.listViewLessonsHalado);
        idToLessonName_exp = populateListView(listViewExpert,"lesson_exp");

        onListItemClick();
    }

    private ArrayMap<String, String> populateListView(ListView listView, String type) {

        ArrayMap<String, String> idToLessonName = fetchDatabase(type);

        String[] items = new String[idToLessonName.keySet().size()];
        int i = 0;
        for (String item : idToLessonName.values()) {
            items[i] = item;
            i++;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,                   //context
                R.layout.list_lessons,    //layout
                items                   //items
        );
        listView.setAdapter(adapter);

        return idToLessonName;
    }


    private ArrayMap<String, String> fetchDatabase(String type) {

        DatabaseHelper db = new DatabaseHelper(this);
        try {
            db.openDataBase();
        } catch (Exception e) {

        }

        Cursor cursor = db.myDataBase.query(DatabaseHelper.TABLE_HOMEWORKS, new String[]{DatabaseHelper.Column.ID, DatabaseHelper.Column.TYPE, DatabaseHelper.Column.NAME}, DatabaseHelper.Column.TYPE + " = ?", new String[]{type}, null, null, null);

        ArrayMap<String, String> idToLessonName = new ArrayMap<String, String>();

        while (cursor.moveToNext()) {
            idToLessonName.put(String.valueOf(cursor.getInt(0)), cursor.getString(2));
        }

        cursor.close();
        db.close();

        return idToLessonName;
    }

    // start new tutor activity and pass the lesson's id to the new activity
    private void onListItemClick(){

        listViewBeginner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {

                Intent tutorIntent = new Intent("guitartutorandanalyser.guitartutor.Tutor");
                tutorIntent.putExtra("homeWorkId", idToLessonName_beg.keyAt(position));
                startActivity(tutorIntent);
            }
        });

        listViewExpert.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {

                Intent tutorIntent = new Intent("guitartutorandanalyser.guitartutor.Tutor");
                tutorIntent.putExtra("homeWorkId", idToLessonName_exp.keyAt(position));
                startActivity(tutorIntent);
            }
        });
    }
}
