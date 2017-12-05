package guitartutorandanalyser.guitartutor;

import android.content.Context;
import android.test.AndroidTestCase;
import android.test.mock.MockContext;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SoundAnalyserTest extends AndroidTestCase {


    //db path /storage/emulated/0/GuitarTutorRec.wav

    HomeWork hw = null;

    SoundAnalyser analyser = null;

    float[] pitchMap1 = new float[]{123f, 234f, 345f, 456f, 567f, 678f, 789f, 890f, 901f, 123f, 234f, 345f, 456f, 567f, 678f, 789f, 890f, 901f, 123f, 234f, 345f, 456f, 567f, 678f, 789f, 890f, 901f, 123f, 234f, 345f, 456f, 567f, 678f, 789f, 890f, 901f, 123f, 234f, 345f, 456f, 567f, 678f, 789f, 890f, 901f};
    float[] pitchMap2 = new float[]{123f, 234f, 345f, 456f, 567f, 678f, 789f, 890f, 901f, 123f, 234f, 345f, 456f, 567f, 678f, 789f, 890f, 901f, 123f, 234f, 345f, 456f, 567f, 678f, 789f, 890f, 901f, 123f, 234f, 345f, 456f, 567f, 678f, 789f, 890f, 901f, 123f, 234f, 345f, 456f, 567f, 678f, 789f, 890f, 901f};
    String pitchMapHw = "123;234;345;456;567;678;789;890;901;123;234;345;456;567;678;789;890;901;123;234;345;456;567;678;789;890;901;123;234;345;456;567;678;789;890;901;123;234;345;456;567;678;789;890;901";

    @Before
    public void setUp() throws Exception {

        hw = HomeWork.homeWorkCreator(1, "song", "1", 1, 1, 1, "2000.10.10.", 1, pitchMapHw, 26000, 25000);
        analyser = new SoundAnalyser(hw, getContext(), 44100, "/storage/emulated/0/GuitarTutorRec.wav");
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void roundFloat2Decimal() throws Exception {
        float result = analyser.roundFloat2Decimal(666.1111f);
        float expected = 666.11f;
        assertEquals(expected, result);
    }

    @Test
    public void compareResult() throws Exception {

        analyser.recordedAudioMap = pitchMapHw;
        analyser.compareResult(new Thread());
    }

    @Test
    public void compareIntPitchMaps() throws Exception {

        int result1 = analyser.comparePitchMaps(pitchMap1, pitchMap2);
        assertEquals(100, result1);
    }

    @Test
    public void getFloatPitchMap() throws Exception {
        String str = "123;123.02;123.23;65;65.83;65.99;999;999.55;";
        float[] expected = new float[]{123f, 123.02f, 123.23f, 65f, 65.83f, 65.99f, 999f, 999.55f};

        float[] result = analyser.getFloatPitchMap(str);

        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], result[i]);
        }
    }
}
