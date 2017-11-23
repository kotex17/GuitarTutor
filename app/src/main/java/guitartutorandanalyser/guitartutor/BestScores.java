package guitartutorandanalyser.guitartutor;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.ArrayList;

public class BestScores extends AppCompatActivity {

    private DatabaseHelper dbh;
    private TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_best_scores);

        dbh = new DatabaseHelper(this);

        tableLayout = (TableLayout) findViewById(R.id.table_layout);
    }

    @Override
    protected void onResume() {

        super.onResume();

        populateListView();
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
                        DatabaseHelper.Column.COMPLETED,},
                null, null, null, null, DatabaseHelper.Column.NAME);

        while (cursor.moveToNext()) {

            listHomeworks.add(new String[]{String.valueOf(cursor.getInt(0)), cursor.getString(1), String.valueOf(cursor.getInt(2)), cursor.getString(3), String.valueOf(cursor.getInt(4))});
        }

        fillTable(listHomeworks);
    }

    public void fillTable(ArrayList<String[]> listHomeworks) {

        //Header

        tableLayout.setColumnStretchable(0, true);
        tableLayout.setColumnStretchable(1, true);
        tableLayout.setColumnStretchable(2, true);
        tableLayout.setColumnStretchable(3, true);

        TableRow tr = new TableRow(this);
        TextView tv = new TextView(this);

        tv.setText("Feladat");
        tv.setTextSize(15);
        tr.addView(tv);


        tv = new TextView(this);
        tv.setText("Legjobb pont");
        tv.setTextSize(15);
        tr.addView(tv);

        tv = new TextView(this);
        tv.setText("Teljesítve");
        tv.setTextSize(15);
        tr.addView(tv);

        tv = new TextView(this);
        tv.setText("Dátum");
        tv.setTextSize(15);
        tr.addView(tv);

        tableLayout.addView(tr);


        //Data

        for (String[] item : listHomeworks) {


            tr = new TableRow(this);

            //name
            tv = new TextView(this);
            tv.setText(item[1]);
            tv.setTextSize(12);
            tr.addView(tv);

            //recordpoint
            tv = new TextView(this);
            tv.setText(item[2]);
            tv.setTextSize(12);
            tr.addView(tv);

            //completed
            tv = new TextView(this);
            if (item[4] == "0") {
                tv.setText("ø");
            } else {
                tv.setText("✓");
            }
            tv.setTextSize(12);
            tr.addView(tv);

            //recorddate
            tv = new TextView(this);
            tv.setText(item[3]);
            tv.setTextSize(12);
            tr.addView(tv);

            tableLayout.addView(tr);
        }
    }

}
