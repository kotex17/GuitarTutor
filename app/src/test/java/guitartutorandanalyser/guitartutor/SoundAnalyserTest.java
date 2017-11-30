package guitartutorandanalyser.guitartutor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.runners.MockitoJUnitRunner;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;

import be.tarsos.dsp.io.android.AndroidFFMPEGLocator;
import be.tarsos.dsp.util.FFMPEGDownloader;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SoundAnalyserTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();
    @Mock
    Context context;
    @Mock
    AndroidFFMPEGLocator ffmpeg;

    HomeWork hw = null;

    SoundAnalyser analyser;

    @Before
    public void setUp() throws Exception {

        hw = HomeWork.homeWorkCreator(1, "song", "1", 1, 1, 1, "2000.10.10.", 1, "550;440;440.56;413.65;", 26000, 25000);
        analyser = new SoundAnalyser(hw,context,44100,"/GuitarRec.wav");
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void analyseRecord() throws Exception {
        when((new FFMPEGDownloader())).thenReturn(null);
        analyser.analyseRecord();
    }

}

/*
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

}*/