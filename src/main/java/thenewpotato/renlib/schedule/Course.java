package thenewpotato.renlib.schedule;

import org.joda.time.LocalTime;

public class Course {

    public String name;
    public String code;
    public String location;
    public String instructor;
    public LocalTime startTime;
    public LocalTime endTime;

    public Course(String name, String code, String location, String instructor) {
        this.name = name;
        this.code = code;
        this.location = location;
        this.instructor = instructor;
    }

    public Course(){
        this.name = null;
        this.code = null;
        this.location = null;
        this.instructor = null;
    }
}
