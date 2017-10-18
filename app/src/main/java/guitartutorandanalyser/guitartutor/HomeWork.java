package guitartutorandanalyser.guitartutor;

public class HomeWork {

    private int bpm;
    private int beats;
    private int tabId;
    private int soundId;
    private int _id;
    private int recordpoint;
    private int completed;

    private String recordDate;
    private String type;
    private String map;
    private String name;


    public HomeWork(){

    }

    public int getBpm() {
        return bpm;
    }

    public int getBeats() {
        return beats;
    }

    public int getTabId() {
        return tabId;
    }

    public int getSoundId() {
        return soundId;
    }

    public int get_id() {
        return _id;
    }

    public int getRecordpoint() {
        return recordpoint;
    }

    public int getCompleted() {
        return completed;
    }

    public String getRecordDate() {
        return recordDate;
    }

    public String getType() {
        return type;
    }

    public String getMap() {
        return map;
    }

    public String getName() {
        return name;
    }

    public void setRecordpoint(int recordpoint) {
        this.recordpoint = recordpoint;
    }

    public void setCompleted(int completed) {
        this.completed = completed;
    }

    public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }

    public static HomeWork homeWorkCreator(int id, String type, String name, int bpm, int beats, int recordPoint, String recordDate, int completed, String map, int tabId, int soundId){
        HomeWork homework = new HomeWork() ;

        homework._id= id;
        homework.type = type;
        homework.name = name;
        homework.bpm = bpm;
        homework.beats = beats;
        homework.recordpoint = recordPoint;
        homework.recordDate = recordDate;
        homework.completed = completed;
        homework.map = map;
        homework.tabId = tabId;
        homework.soundId = soundId;

        return homework;
    }

}
