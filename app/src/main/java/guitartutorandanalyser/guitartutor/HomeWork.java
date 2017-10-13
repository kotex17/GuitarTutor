package guitartutorandanalyser.guitartutor;


public class HomeWork {

    private int bpm;
    private int beats;
    private int tabId;
    private int soundId;
    private String name;




    public HomeWork(int soundId){
        this.soundId = soundId;
    }

    public int getBpm() {
        return bpm;
    }

    public int getBeats() {
        return beats;
    }

    public int getImageId() {
        return tabId;
    }

    public int getSoundId() {
        return soundId;
    }

    public String getName(){return  name;}

}
