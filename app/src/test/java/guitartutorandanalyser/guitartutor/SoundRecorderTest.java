package guitartutorandanalyser.guitartutor;



import org.junit.After;
import org.junit.Before;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class SoundRecorderTest {

    SoundRecorder recorder = null;

    @Before
    public void setUp() throws Exception {

        recorder = new SoundRecorder();
    }

    @After
    public void tearDown() throws Exception {
        recorder = null;
    }

    @Test
    public void startRecording() throws Exception {
        recorder.startRecording().start();
    }

    @Test
    public void stopRecording() throws Exception {
        recorder.stopRecording();
    }

    @Test
    public void wavHeaderTest() throws Exception {

        byte[] wavHeader = recorder.createWavHeader();
        byte expected = 0;

        //wavHeader[43], the size of the sound data is stored on the last 4 bytes
        assertEquals(expected, wavHeader[43]);
        assertEquals(expected, wavHeader[42]);
        assertEquals(expected, wavHeader[41]);
        assertEquals(expected, wavHeader[40]);

        recorder.updateWavHeader();
        assertEquals(expected, wavHeader[43]);

    }

    @Test
    public void short2byteTest() throws Exception {

        short[] s = new short[]{1};
        byte[] b = recorder.short2byte(s);
        assertEquals(1, b[0]);

        s = new short[]{256};
        b = recorder.short2byte(s);
        assertEquals(0, b[0]);
        assertEquals(1, b[1]);
    }

}