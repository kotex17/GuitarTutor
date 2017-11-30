package guitartutorandanalyser.guitartutor;


import android.content.Context;
import android.os.Environment;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SoundRecorderTest {


    @Mock
    Environment environment;

    @InjectMocks
    SoundRecorder recorder;



    @Before
    public void setUp() throws Exception {

/*
        MockitoAnnotations.initMocks(this);

        String s = "/storage/emulated/0/GuitarTutorRec.wav";
        Mockito.when(Environment.getExternalStorageDirectory()).thenReturn("");
        recorder = new SoundRecorder();
    */}

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void startRecording() throws Exception {
        recorder.startRecording();
    }

    @Test
    public void stopRecording() throws Exception {
        recorder.stopRecording();
    }

}