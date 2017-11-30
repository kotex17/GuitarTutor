package guitartutorandanalyser.guitartutor;

import android.app.Activity;
import android.app.Instrumentation;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import android.widget.ScrollView;

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
public class UserGuideTest {


    @Rule
    public ActivityTestRule<UserGuide> uGuideTestRule = new ActivityTestRule<>(UserGuide.class);

    Instrumentation.ActivityMonitor mainMonitor = getInstrumentation().addMonitor(GuitarTutorMain.class.getName(),null,false);


    Activity userGuide = null;

    @Before
    public void setUp() throws Exception {
        userGuide = uGuideTestRule.getActivity();
    }

    @After
    public void tearDown() throws Exception {
        userGuide = null;
    }

    @Test
    public void onTouchTest() throws Exception {

        ScrollView scrollView = (ScrollView) userGuide.findViewById(R.id.userGuide_scrollView);


        int startY = scrollView.getScrollY();
        onView(withId(R.id.userGuide_Game_TextView)).perform(click());
        assertNotEquals( startY, scrollView.getScrollY());


        scrollView.setScrollY(startY);
        onView(withId(R.id.userGuide_Analyse_TextView)).perform(click());
        assertNotEquals( startY, scrollView.getScrollY());


        scrollView.setScrollY(startY);
        onView(withId(R.id.userGuide_Lessons_TextView)).perform(click());
        assertNotEquals( startY, scrollView.getScrollY());

        scrollView.setScrollY(startY);
        onView(withId(R.id.userGuide_Records_TextView)).perform(click());
        assertNotEquals( startY, scrollView.getScrollY());

    }

    @Test
    public void backToStartMenu() throws Exception {

        onView(withId(R.id.userGuide_back_button)).perform(click());

        Activity mainActivity = getInstrumentation().waitForMonitorWithTimeout(mainMonitor,3000);

        assertNotNull(mainActivity);

        mainActivity.finish();
    }

}