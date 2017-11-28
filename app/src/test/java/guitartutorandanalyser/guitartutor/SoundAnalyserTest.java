package guitartutorandanalyser.guitartutor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;

import static org.junit.Assert.*;


@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class SoundAnalyserTest {

    Activity activity;

    SoundAnalyser soundAnalyser;

    @Before
    public void setUp() throws Exception {
        Intent tutorIntent = new Intent(ShadowApplication.getInstance().getApplicationContext(), Tutor.class);
        tutorIntent.putExtra("homeWorkId", "1");

        activity = Robolectric.buildActivity(Tutor.class).withIntent(tutorIntent).create().get();





        HomeWork homeWork = new HomeWork();

        soundAnalyser = new SoundAnalyser(homeWork,activity,44100, Environment.getExternalStorageDirectory()+"/GuitarTutorRec.wav");
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void analyseRecord() throws Exception {
        soundAnalyser.analyseRecord();
    }

}