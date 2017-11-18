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
import android.widget.Toast;

public class Lessons extends AppCompatActivity {
    ListView listViewBeginner;
    ListView listViewExpert;

    ArrayMap<String, String> idToLessonName_beg = new ArrayMap<String, String>(); // list of beginner lessons, maped to their database ID
    ArrayMap<String, String> idToLessonName_exp = new ArrayMap<String, String>(); // list of expert lessons, maped to their database ID

    ArrayMap<String, Boolean> idIsLearable ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lessons);

        listViewBeginner = (ListView) findViewById(R.id.listViewLessonsKezdo);
        idToLessonName_beg = populateListView(listViewBeginner, "lesson_beg");

        listViewExpert = (ListView) findViewById(R.id.listViewLessonsHalado);
        idToLessonName_exp = populateListView(listViewExpert, "lesson_exp");


        idIsLearable = getLearnableHomeworkMap();
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

        Cursor cursor = db.myDataBase.query(DatabaseHelper.TABLE_HOMEWORKS, new String[]{DatabaseHelper.Column.ID, DatabaseHelper.Column.TYPE, DatabaseHelper.Column.NAME, DatabaseHelper.Column.COMPLETED}, DatabaseHelper.Column.TYPE + " = ?", new String[]{type}, null, null, DatabaseHelper.Column.ID);

        ArrayMap<String, String> idToLessonName = new ArrayMap<String, String>();

        while (cursor.moveToNext()) {

            String isCompleted = "";

            if (cursor.getInt(3) == 1)
                isCompleted = "✓";

            idToLessonName.put(String.valueOf(cursor.getInt(0)), cursor.getString(2) + " \t\t" + isCompleted);
        }

        cursor.close();
        db.close();

        return idToLessonName;
    }

    // start new tutor activity and pass the lesson's id to the new activity
    private void onListItemClick() {

        listViewBeginner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {

                if(idIsLearable.get(idToLessonName_beg.keyAt(position))) {
                    Intent tutorIntent = new Intent("guitartutorandanalyser.guitartutor.Tutor");
                    tutorIntent.putExtra("homeWorkId", idToLessonName_beg.keyAt(position));
                    startActivity(tutorIntent);
                }
            }
        });

        listViewExpert.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {

                if(idIsLearable.get(idToLessonName_exp.keyAt(position))) {
                    Intent tutorIntent = new Intent("guitartutorandanalyser.guitartutor.Tutor");
                    tutorIntent.putExtra("homeWorkId", idToLessonName_exp.keyAt(position));
                    startActivity(tutorIntent);
                }
            }
        });
    }


    private ArrayMap<Integer, Integer> avaibleConditionMap() {

        ArrayMap<Integer, Integer> avaibleConditionMap = new ArrayMap<>();
        avaibleConditionMap.put(2, 4); //minor expert, minor beginner

        return avaibleConditionMap;
    }

    private ArrayMap<String, Boolean> getLearnableHomeworkMap() {

        DatabaseHelper db = new DatabaseHelper(this);
        try {
            db.openDataBase();
        } catch (Exception e) {

        }

        ArrayMap<String, Boolean> tempIdIsLearable = new ArrayMap<>();

        Cursor cursor = db.myDataBase.query(DatabaseHelper.TABLE_HOMEWORKS, new String[]{DatabaseHelper.Column.ID, DatabaseHelper.Column.TYPE, DatabaseHelper.Column.NAME, DatabaseHelper.Column.COMPLETED}, null, null, null, null, DatabaseHelper.Column.ID);

        ArrayMap<Integer, Integer> avaibleConditionMap = avaibleConditionMap();


        int cursorPosition = 0;
        while (cursor.moveToPosition(cursorPosition)) {

            int currentLessonId = cursor.getInt(0);
            if (avaibleConditionMap.containsKey( currentLessonId )) {

                cursor.moveToPosition( avaibleConditionMap.get(currentLessonId)-1 ); // cursor is ordered by id, id start from 1, index start from 0

                //cursor stand on the Conditional Lesson now,  if completed = 1
                if (cursor.getInt(3) == 1)
                    tempIdIsLearable.put(String.valueOf(currentLessonId), true);
                else
                    tempIdIsLearable.put(String.valueOf(currentLessonId), false);

            } else {

                // no condition -->  make lesson avaible
                tempIdIsLearable.put(String.valueOf(currentLessonId), true);
            }
            cursorPosition++;
        }

        cursor.close();
        db.close();

        return  tempIdIsLearable;
    }
}
