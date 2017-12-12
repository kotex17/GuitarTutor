package guitartutorandanalyser.guitartutor;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class AnalyseResultTest {

    @Rule
    public ActivityTestRule<AnalyseResult> analyseTestRule = new ActivityTestRule<>(AnalyseResult.class, false, false);


    Activity analyseActivity = null;

    @Before
    public void setUp() throws Exception {

        Intent tempIntent = new Intent();
        tempIntent.putExtra("homeworkId", "1");
        tempIntent.putExtra("result", "77");
        analyseTestRule.launchActivity(tempIntent);

        analyseActivity = analyseTestRule.getActivity();
        assertNotNull(analyseActivity);
    }

    @After
    public void tearDown() throws Exception {
        analyseActivity = null;
    }

    @Test
    public void checkUiTest() throws Exception {

        Thread.currentThread().sleep(5000);

        ProgressBar pb = (ProgressBar) analyseActivity.findViewById(R.id.progressBarResult);
        assertEquals("77", String.valueOf(pb.getProgress()));//.contains("Feladat teljesítve!")

        TextView tv = (TextView) analyseActivity.findViewById(R.id.resultTextFeedback);
        assertTrue(tv.getText().toString().contains("Feladat teljesítve!"));

    }


}