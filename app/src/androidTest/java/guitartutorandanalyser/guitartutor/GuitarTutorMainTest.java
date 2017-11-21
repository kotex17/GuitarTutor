package guitartutorandanalyser.guitartutor;

import android.app.Activity;
import android.app.Instrumentation;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class GuitarTutorMainTest {

    @Rule
    public ActivityTestRule<GuitarTutorMain> guitarTutorMainTestRule = new ActivityTestRule<GuitarTutorMain>(GuitarTutorMain.class);

      GuitarTutorMain guitarTutorMain = null;
     Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor(Lessons.class.getName(), null, false);


    @Before
    public void setUp() throws Exception {
        guitarTutorMain = guitarTutorMainTestRule.getActivity();
    }


    @Test
    public void testLessonActivityOnClick() {

        testLaunchActivity(R.id.main_button_lessons);


    }

    @Test
    public void testRecordsActivityOnClick() {

        testLaunchActivity(R.id.main_button_records);
    }


    @Test
    public void testSongsActivityOnClick() {
        testLaunchActivity(R.id.main_button_songs);

    }
    @Test
    public void testHelpActivityOnClick() {

        testLaunchActivity(R.id._main_button_help);

    }


    public  void testLaunchActivity(int buttonId) {


        assertNotNull(guitarTutorMain.findViewById(buttonId));

        onView(withId(buttonId)).perform(click());
        Activity launchedActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 5000);
        assertNotNull(launchedActivity);

launchedActivity.finish();

    }


    @After
    public void tearDown() throws Exception {

        guitarTutorMain = null;
    }

}