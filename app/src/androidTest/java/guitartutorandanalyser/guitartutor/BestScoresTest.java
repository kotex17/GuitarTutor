package guitartutorandanalyser.guitartutor;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class BestScoresTest {


    @Rule
    public ActivityTestRule<BestScores> recordsTestRule = new ActivityTestRule<>(BestScores.class);

    Activity records = null;

    @Before
    public void setUp() throws Exception {

        records = recordsTestRule.getActivity();
    }

    @Test
    public void testView() throws Exception {

        TableRow tr = records.findViewById(R.id.table_row_default);
        assertNotNull(tr);
        TextView tv = tr.findViewById(R.id.textView1Name);
        assertNotNull(tv.getText());
    }

}