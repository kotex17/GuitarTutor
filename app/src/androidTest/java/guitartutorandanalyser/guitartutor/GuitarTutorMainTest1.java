package guitartutorandanalyser.guitartutor;

import android.app.Activity;
import android.app.Instrumentation;
import android.os.Looper;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.Button;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class GuitarTutorMainTest1 {

    @Rule
    public ActivityTestRule<GuitarTutorMain> mainTestRule = new ActivityTestRule<GuitarTutorMain>(GuitarTutorMain.class);

    Instrumentation.ActivityMonitor lessonMonitor = getInstrumentation().addMonitor(Lessons.class.getName(),null,false);
    Instrumentation.ActivityMonitor songMonitor = getInstrumentation().addMonitor(Songs.class.getName(),null,false);
    Instrumentation.ActivityMonitor recordMonitor = getInstrumentation().addMonitor(BestScores.class.getName(),null,false);
    Instrumentation.ActivityMonitor helpMonitor = getInstrumentation().addMonitor(UserGuide.class.getName(),null,false);

    GuitarTutorMain mainActivity = null;


    @Before
    public void setUp() throws Exception {
        mainActivity = mainTestRule.getActivity();
    }

    @After
    public void tearDown() throws Exception {
        mainActivity = null;
    }

    @Test
    public void onCreate() throws Exception {
        assertNotNull(mainActivity.dbh);
    }

    @Test
    public void onButtonSongsClick() throws Exception {

        Button songButton = (Button) mainActivity.findViewById(R.id.main_button_songs);

        assertNotNull(songButton);

        songButton.callOnClick();


        Activity songActivity = getInstrumentation().waitForMonitorWithTimeout(songMonitor,3000);

        assertNotNull(songActivity);

        songActivity.finish();
    }

    @Test
    public void onButtonLessonsClick() throws Exception {

        Button lessonButton = (Button) mainActivity.findViewById(R.id.main_button_lessons);

        assertNotNull(lessonButton);

        lessonButton.callOnClick();


        Activity lessonActivity = getInstrumentation().waitForMonitorWithTimeout(lessonMonitor,3000);

        assertNotNull(lessonActivity);

        lessonActivity.finish();
    }

    @Test
    public void onButtonRecordsClick() throws Exception {
        Button recordButton = (Button) mainActivity.findViewById(R.id.main_button_records);

        assertNotNull(recordButton);

        recordButton.callOnClick();


        Activity recordActivity = getInstrumentation().waitForMonitorWithTimeout(recordMonitor,3000);

        assertNotNull(recordActivity);

        recordActivity.finish();
    }

    @Test
    public void onButtonHelpClick() throws Exception {
        Button helpButton = (Button) mainActivity.findViewById(R.id.main_button_help);

        assertNotNull(helpButton);

        helpButton.callOnClick();


        Activity helpActivity = getInstrumentation().waitForMonitorWithTimeout(helpMonitor,3000);

        assertNotNull(helpActivity);

        helpActivity.finish();
    }

    @Test
    public void onBackPressed() throws Exception {
        Looper.prepare();
        mainActivity.onBackPressed();
    }

}