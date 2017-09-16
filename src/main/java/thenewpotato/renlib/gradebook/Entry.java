package thenewpotato.renlib.gradebook;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

public class Entry{

    public static final String DATE_PATTERN = "MM/dd";

    public String name;
    public Float points;
    public Float maxPoints;
    public Float average;
    public String status;
    public LocalDate date;
    public Float curve;
    public Float bonus;
    public Float weight;
    public String note;

    public Entry(String name, String points, String maxPts, String average, String status, String date, String curve, String bonus, String weight, String note) {
        this.name = name;
        this.points = Float.parseFloat(points);
        this.maxPoints = Float.parseFloat(maxPts);
        this.average = Float.parseFloat(average);
        this.status = status;
        this.date = DateTimeFormat.forPattern(DATE_PATTERN).parseLocalDate(date);
        this.curve = Float.parseFloat(curve);
        this.bonus = Float.parseFloat(bonus);
        this.weight = Float.parseFloat(weight);
        this.note = note;
    }

    public Entry() {
        this.name = null;
        this.points = null;
        this.maxPoints = null;
        this.average = null;
        this.status = null;
        this.date = null;
        this.curve = null;
        this.bonus = null;
        this.weight = null;
        this.note = null;
    }

}
