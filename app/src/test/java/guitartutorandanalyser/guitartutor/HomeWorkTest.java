package guitartutorandanalyser.guitartutor;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Marcus on 2017. 11. 27..
 */
public class HomeWorkTest {

    int _id = 1;
    String type = "song";
    String name = "test name";

    int bpm = 97;
    int beats = 24;
    int recordPoint = 77;
    String recordDate = "2011.11.11";
    int completed = 1;
    String map = "440.44;440.76;440.40;440.02;";
    int tabId = 2672564;
    int soundId = 265235;


    static HomeWork testHomework;


    @Test
    public void homeWorkCreator() throws Exception {
        this.testHomework = HomeWork.homeWorkCreator(_id, type, name, bpm, beats, recordPoint, recordDate, completed, map, tabId, soundId);
        Assert.assertNotNull(testHomework);
    }

    @Test
    public void getSetRecordpoint() throws Exception {
        Assert.assertEquals(recordPoint, testHomework.getRecordpoint());

        testHomework.setRecordpoint(66);
        Assert.assertEquals(66, testHomework.getRecordpoint());
    }

    @Test
    public void getBpm() throws Exception {
        Assert.assertEquals(bpm, testHomework.getBpm());
    }

    @Test
    public void getBeats() throws Exception {
        Assert.assertEquals(beats, testHomework.getBeats());
    }

    @Test
    public void getTabId() throws Exception {
        Assert.assertEquals(tabId, testHomework.getTabId());
    }

    @Test
    public void getSoundId() throws Exception {
        Assert.assertEquals(soundId, testHomework.getSoundId());
    }

    @Test
    public void get_id() throws Exception {
        Assert.assertEquals(_id, testHomework.get_id());
    }


    @Test
    public void getCompleted() throws Exception {
        Assert.assertEquals(completed, testHomework.getCompleted());
    }

    @Test
    public void getRecordDate() throws Exception {
        Assert.assertEquals(recordDate, testHomework.getRecordDate());
    }

    @Test
    public void getType() throws Exception {
        Assert.assertEquals(type, testHomework.getType());
    }

    @Test
    public void getMap() throws Exception {
        Assert.assertEquals(map, testHomework.getMap());
    }

    @Test
    public void getName() throws Exception {
        Assert.assertEquals(name, testHomework.getName());
    }


    @Test
    public void setCompleted() throws Exception {
        testHomework.setCompleted(0);
        Assert.assertEquals(0, testHomework.getCompleted());
    }

    @Test
    public void setRecordDate() throws Exception {

        testHomework.setRecordDate("2033.11.11.");
        Assert.assertEquals("2033.11.11.", testHomework.getRecordDate());
    }

}