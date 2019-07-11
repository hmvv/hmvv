package hmvv.model;
import hmvv.gui.GUICommonTools;
import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Database {

    private String name;
    private String version;
    private String release;
    private int age;

    public Database(){ }

    public void setName(String name) {
        this.name = name;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getRelease() {
        return release;
    }

    public int getAge() {
        return age;
    }

    public void updateAge() throws ParseException {
        Date release = GUICommonTools.shortDateFormat.parse(this.release);
        Date today = new Date();
        long diffInMillies = Math.abs(today.getTime() - release.getTime());
        this.age = (int)(TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS))/30;
    }
}
