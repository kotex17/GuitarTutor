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
public class SoundRecorderTest1 {

    @Rule
    public ActivityTestRule<Tutor> testRule = new ActivityTestRule<Tutor>(Tutor.class, false, false);

    Instrumentation.ActivityMonitor analyseMonitor = getInstrumentation().addMonitor(AnalyseResult.class.getName(), null, false);

    Activity tutor = null;

    SoundRecorder recorder = null;

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
        tutor.finish();
    }

    @Test
    public void testRecording() throws Exception {

        recorder = new SoundRecorder();

        recorder.startRecording();

        Thread.currentThread().sleep(2000);

        assertTrue(recorder.isSoundRecording);

        recorder.stopRecording();

        assertFalse(recorder.isSoundRecording);
    }

}