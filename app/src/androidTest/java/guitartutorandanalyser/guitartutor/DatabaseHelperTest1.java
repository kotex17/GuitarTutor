package guitartutorandanalyser.guitartutor;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class DatabaseHelperTest1 {


    @Rule
    public ActivityTestRule<GuitarTutorMain> mainTestRule = new ActivityTestRule<GuitarTutorMain>(GuitarTutorMain.class);

    Activity main = null;

    DatabaseHelper dbh = null;

    @Before
    public void setUp() throws Exception {


        main = mainTestRule.getActivity();
        assertNotNull(main);

        dbh = new DatabaseHelper(main);
    }


    @After
    public void tearDown() throws Exception {
        dbh.close();
        dbh = null;
    }

    @Test
    public void createDataBase() throws Exception {
        assertNull(dbh.myDataBase);

        dbh.createDataBase();
        dbh.openDataBase();

        assertNotNull(dbh.myDataBase);
        assertTrue(dbh.checkDataBase());
    }

}