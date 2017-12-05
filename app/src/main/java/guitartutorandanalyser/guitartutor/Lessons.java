package guitartutorandanalyser.guitartutor;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class Lessons extends AppCompatActivity {

    ListView listViewBeginner;
    ListView listViewExpert;

    ArrayMap<Integer, String> positionToId_beg = new  ArrayMap<Integer, String>(); // list of beginner lessons, maped to their database ID
    ArrayMap<Integer, String> positionToId_exp = new  ArrayMap<Integer, String>(); // list of expert lessons, maped to their database ID

    ArrayMap<String, Boolean> idIsLearnable;

    int sortingPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lessons);

        listViewBeginner = (ListView) findViewById(R.id.listViewLessonsKezdo);
        listViewExpert = (ListView) findViewById(R.id.listViewLessonsHalado);
    }

    @Override
    protected void onResume() {

        super.onResume();

        idIsLearnable = getLearnableHomeworkMap();
        positionToId_beg = populateListView(listViewBeginner, "lesson_beg");
        positionToId_exp = populateListView(listViewExpert, "lesson_exp");

        onListItemClick();
    }

    private ArrayMap<Integer, String> populateListView(ListView listView, String type) {

        ArrayMap<String, String> idToLessonName = sortMapByAvailability(fetchDatabase(type));


        ArrayMap<Integer, String> positionToId = new ArrayMap<>();
        String[] items = new String[idToLessonName.keySet().size()];
        int itemIndex = 0;

        for (int i = idToLessonName.size() - 1; i >= 0; i--) {

            items[itemIndex] = idToLessonName.get(idToLessonName.keyAt(i));
            positionToId.put(itemIndex, idToLessonName.keyAt(i));
            itemIndex++;
        }

        final int sortingSeparator = sortingPosition;

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,                   //context
                R.layout.list_lessons,    //layout
                items                   //items
        ) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                // Get the current item from ListView
                View view = super.getView(position, convertView, parent);
                TextView textItem = (TextView) view;

                if (position > sortingSeparator)
                    textItem.setTextColor(Color.rgb(232, 62, 62));
                else
                    textItem.setTextColor(Color.LTGRAY);

                return view;
            }
        };

        listView.setAdapter(adapter);

        return positionToId;
    }

    // separates list items(at this point a map), available to learn first, not avalable ones last
    private ArrayMap<String, String> sortMapByAvailability(ArrayMap<String, String> idToLessonName) {

        ArrayMap<String, String> sortedMap = new ArrayMap<>();

        sortingPosition = -1;

        for (String key : idToLessonName.keySet()) {

            if (idIsLearnable.get(key)) {

                sortedMap.put(key, idToLessonName.get(key));
                sortingPosition++;
            }
        }

        for (String key : idToLessonName.keySet()) {

            if (!idIsLearnable.get(key))
                sortedMap.put(key, idToLessonName.get(key));
        }

        return sortedMap;
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

                if (idIsLearnable.get(positionToId_beg.get(position))) {
                    Intent tutorIntent = new Intent("guitartutorandanalyser.guitartutor.Tutor");
                    tutorIntent.putExtra("homeWorkId", positionToId_beg.get(position));
                    startActivity(tutorIntent);
                }
            }
        });

        listViewExpert.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {

                if (idIsLearnable.get(positionToId_exp.get(position))) {
                    Intent tutorIntent = new Intent("guitartutorandanalyser.guitartutor.Tutor");
                    tutorIntent.putExtra("homeWorkId", positionToId_exp.get(position));
                    startActivity(tutorIntent);
                }
            }
        });
    }

    // describes which lesson must be completed to play specific lessons
    private ArrayMap<Integer, Integer> avaibleConditionMap() {

        ArrayMap<Integer, Integer> avaibleConditionMap = new ArrayMap<>();
        avaibleConditionMap.put(2, 4); //minor expert, condition: minor beginner
        avaibleConditionMap.put(6, 5); // blues, condtion: pentaton scale

        avaibleConditionMap.put(9, 5); // pentaton: pentaton cliche1

        avaibleConditionMap.put(8, 1); // pentatoc_cliche_2: pendaton cliche_1




        return avaibleConditionMap;
    }

    // describes which lessons can be played by user,  based on already completed lessons
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
            if (avaibleConditionMap.containsKey(currentLessonId)) {

                cursor.moveToPosition(avaibleConditionMap.get(currentLessonId) - 1); // cursor is ordered by id, id start from 1, index start from 0

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

        return tempIdIsLearable;
    }
}
