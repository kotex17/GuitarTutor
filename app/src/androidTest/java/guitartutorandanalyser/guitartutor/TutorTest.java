package guitartutorandanalyser.guitartutor;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Environment;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import android.widget.Button;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.io.File;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;
import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
public class TutorTest {

    @Rule
    public ActivityTestRule<Tutor> testRule = new ActivityTestRule<>(Tutor.class,
            false,    // initialTouchMode
            false);  // launchActivity. False to set intent per test);

    Instrumentation.ActivityMonitor analyseMonitor = getInstrumentation().addMonitor(AnalyseResult.class.getName(), null, false);

    Activity tutor = null;

    @Before
    public void setUp() throws Exception {
        Intent tempIntent = new Intent();
        tempIntent.putExtra("homeWorkId", "1");
        testRule.launchActivity(tempIntent);

        tutor = testRule.getActivity();
        assertNotNull(tutor);
    }


    @After
    public void tearDown() throws Exception {
        tutor = null;
    }

    @Test
    public void a_onButtonPlayClick() throws Exception {
        //start play
        onView(withId(R.id.button_play_stop)).perform(click());
        //stop play
        onView(withId(R.id.button_play_stop)).perform(click());

    }


    @Test
    public void c_onButtonRecClick() throws Exception {

        //start rec
        onView(withId(R.id.button_rec_stop)).perform(click());
        //stop rec
        onView(withId(R.id.button_rec_stop)).perform(click());

        File f = new File(Environment.getExternalStorageDirectory().getPath() + "/GuitarTutorRec.wav");
        assertTrue(f.exists());

    }

    @Test
    public void d_onButtonPlayRecStopRecClick() throws Exception {
        //start play
        onView(withId(R.id.button_playrec_stoprec)).perform(click());

        //stop play
        onView(withId(R.id.button_playrec_stoprec)).perform(click());
    }

    @Test
    public void b_onStartStopPracticingMetronome() throws Exception {
        //start practice metronome
        onView(withId(R.id.button_practice_metronome)).perform(click());

        //stop practice metronome
        onView(withId(R.id.button_practice_metronome)).perform(click());
    }

    @Test
    public void e_AnalyseTooShortRecord() throws Exception {

        onView(withId(R.id.button_analyse)).perform(click());

        Activity analyseActivity = getInstrumentation().waitForMonitorWithTimeout(analyseMonitor, 10000);

        assertNull(analyseActivity);

    }

    @Test
    public void z_AnalyseLongEnoughtRecord() throws Exception {

        //recording until the end of the homework
        onView(withId(R.id.button_rec_stop)).perform(click());

        //wait for recording to finish
        Thread.currentThread().sleep(60000);

        onView(withId(R.id.button_analyse)).perform(click());

        Activity analyseActivity = getInstrumentation().waitForMonitorWithTimeout(analyseMonitor, 10000);

        assertNotNull(analyseActivity);

        analyseActivity.finish();

    }

}