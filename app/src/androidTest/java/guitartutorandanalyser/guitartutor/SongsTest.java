package guitartutorandanalyser.guitartutor;

import android.app.Activity;
import android.app.Instrumentation;
import android.support.test.rule.ActivityTestRule;
import android.widget.ListView;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;
import static org.junit.Assert.*;


public class SongsTest {

    @Rule
    public ActivityTestRule<Songs> songsTestRule = new ActivityTestRule<>(Songs.class);

    Instrumentation.ActivityMonitor tutorMonitor = getInstrumentation().addMonitor(Tutor.class.getName(), null, false);

    Songs songsActivity = null;

    @Before
    public void setUp() throws Exception {
        songsActivity = songsTestRule.getActivity();
        assertNotNull(songsActivity);
    }

    @After
    public void tearDown() throws Exception {
        songsActivity = null;
    }

    @Test
    public void testTouchSong() throws Exception {
        final ListView listView = (ListView) songsActivity.findViewById(R.id.songsListView);
        assertNotNull(listView);
        //  Thread.sleep(5000);


        onData(anything()).inAdapterView(withId(R.id.songsListView)).atPosition(0).perform(click());


        Activity tutorActivity = getInstrumentation().waitForMonitorWithTimeout(tutorMonitor, 10000);

        assertNotNull(tutorActivity);
        tutorActivity.finish();
    }

}