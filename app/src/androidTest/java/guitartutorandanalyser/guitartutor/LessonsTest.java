package guitartutorandanalyser.guitartutor;

import android.app.Activity;
import android.app.Instrumentation;
import android.os.Handler;
import android.os.Looper;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.ListView;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class LessonsTest {

    @Rule
    public ActivityTestRule<Lessons> lessonsTestRule = new ActivityTestRule<>(Lessons.class);

    Instrumentation.ActivityMonitor tutorMonitor = getInstrumentation().addMonitor(Tutor.class.getName(), null, false);

    Lessons lessonsActivity = null;


    @Before
    public void setUp() throws Exception {
        lessonsActivity = lessonsTestRule.getActivity();
        assertNotNull(lessonsActivity);
    }

    @After
    public void tearDown() throws Exception {
        lessonsActivity = null;
    }

    @Test
    public void testBeginnerLessons() throws Exception {

        final ListView listView = (ListView) lessonsActivity.findViewById(R.id.listViewLessonsKezdo);
        assertNotNull(listView);
        //  Thread.sleep(5000);


        onData(anything()).inAdapterView(withId(R.id.listViewLessonsKezdo)).atPosition(0).perform(click());


        Activity tutorActivity = getInstrumentation().waitForMonitorWithTimeout(tutorMonitor, 10000);

        assertNotNull(tutorActivity);
        tutorActivity.finish();
    }

    @Test
    public void testExpertLessons() throws Exception {
        final ListView listView = (ListView) lessonsActivity.findViewById(R.id.listViewLessonsHalado);
        assertNotNull(listView);
        //  Thread.sleep(5000);


        onData(anything()).inAdapterView(withId(R.id.listViewLessonsHalado)).atPosition(0).perform(click());

        Activity tutorActivity = getInstrumentation().waitForMonitorWithTimeout(tutorMonitor, 10000);

        assertNotNull(tutorActivity);
        tutorActivity.finish();
    }

}