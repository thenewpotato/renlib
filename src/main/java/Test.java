import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Test {
    public static void main(String[] args) {
        Credentials credentials = new Credentials(
                "TWS-TN",
                "thenewpotato",
                "***REMOVED***",
                "PARENTSWEB-STUDENT");

        Schedule schedule = new Schedule(credentials);
        for (Course course : schedule.get(Schedule.FRIDAY)) {
            System.out.println("In " + course.location + " for " + course.name + " at " + course.startTime + "-" + course.endTime);
        }

        // grade book

        // homework
    }
}
