package thenewpotato.renlib;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.joda.time.format.DateTimeFormat;
import thenewpotato.renlib.gradebook.Category;
import thenewpotato.renlib.gradebook.Course;
import thenewpotato.renlib.gradebook.Entry;
import thenewpotato.renlib.gradebook.Gradebook;
import thenewpotato.renlib.homework.Assignment;
import thenewpotato.renlib.homework.Day;
import thenewpotato.renlib.homework.Week;

import java.io.FileWriter;
import java.io.PrintWriter;

public class Test {
    public static void main(String[] args) {
        Credentials credentials = new Credentials(
                "TWS-TN",
                "thenewpotato",
                Credentials.tempPassword,
                "PARENTSWEB-STUDENT");

        Gradebook gradebook = new Gradebook(credentials);
        for (Course course : gradebook.getCourses()) {
            for (Category category : course.getCategories()) {
                for (Entry entry : category.entries()) {
                    System.out.println("In assignment " + entry.name +
                            " from " + category.name() +
                            " in " + course.getCourseName() +
                            " you got a " + entry.average);
                }
            }
        }

        /*// schedule
        Schedule schedule = new Schedule(credentials);
        for (Course course : schedule.get(Schedule.TUESDAY)) {
            System.out.println("In " + course.location + " for " + course.name + " at " + course.startTime + "-" + course.endTime);
        }
        */

        /*// homework
        Week week = new Week(
                credentials,
                DateTimeFormat.forPattern("MM/dd/yyyy").parseLocalDate("09/02/2017"));
        for(Assignment assignment : week.getDays().get(Week.WEDNESDAY).assignments) {
            System.out.println("For " + assignment.courseName + " do " + assignment.assignment);
        }
        */
    }
}
