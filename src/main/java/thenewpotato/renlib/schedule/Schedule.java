package thenewpotato.renlib.schedule;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import thenewpotato.renlib.Credentials;

import java.util.ArrayList;
import java.util.Arrays;

public class Schedule {

    public static final int KEY_NAME = 0;
    public static final int KEY_LOCATION = 1;
    public static final int KEY_INSTRUCTOR = 2;
    public static final int MONDAY = 2;
    public static final int TUESDAY = 3;
    public static final int WEDNESDAY = 4;
    public static final int THURSDAY = 5;
    public static final int FRIDAY = 6;
    public static final int SATURDAY = 7;
    public static final int SUNDAY = 8;

    private Document scheduleDocument = null;
    private ArrayList<Course> courseListings;
    private DateTimeFormatter renwebTimeFormatter = DateTimeFormat.forPattern("hh:mm a");

    public Schedule(Credentials credentials) {
        // get schedule directory page
        HttpGet httpGet = new HttpGet("https://tws-tn.client.renweb.com/pw/student/schedules.cfm");
        HttpResponse httpResponse = null;
        try {
            httpResponse = credentials.httpClient.execute(httpGet, credentials.httpContext);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        System.out.println("get (directory page): " + httpResponse.getStatusLine());

        // parse out schedule url
        Document document = null;
        try {
            document = Jsoup.parse(EntityUtils.toString(httpResponse.getEntity()));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        Element a = document.select(".graybutton").first();
        String scheduleUrl = "https://tws-tn.client.renweb.com" + a.attr("href");
        System.out.println("schedule URL: " + scheduleUrl);

        // get schedule
        httpGet = new HttpGet(scheduleUrl);
        try {
            httpResponse = credentials.httpClient.execute(httpGet, credentials.httpContext);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        System.out.println("get (schedule): " + httpResponse.getStatusLine());
        try {
            scheduleDocument = Jsoup.parse(EntityUtils.toString(httpResponse.getEntity()));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        // parse schedule
        courseListings = new ArrayList<>();
        Elements courseEntries = scheduleDocument.select(
                "body > table:nth-child(2) > tbody:nth-child(2) > tr");
        for (int i = 2; i < courseEntries.size() - 1; i++) {
            Elements courseSects = courseEntries.get(i).select("td");
            // empty thenewpotato.renlib.schedule.Course index holder
            courseListings.add(new Course());
            for (int l = 0; l < courseSects.size(); l++) {
                switch (l) {
                    case 0:
                        courseListings.get(i - 2).name = courseSects.get(l).text();
                        break;
                    case 1:
                        courseListings.get(i - 2).code = courseSects.get(l).text();
                        break;
                    case 2:
                        courseListings.get(i - 2).location = courseSects.get(l).text();
                        break;
                    case 3:
                        courseListings.get(i - 2).instructor = courseSects.get(l).text();
                        break;
                }
            }
        }
    }

    public Course getCourseByCode(String courseCode) {
        for (Course course : courseListings) {
            if (course.code.equals(courseCode)) {
                return course;
            }
        }
        return null;
    }

    public ArrayList<Course> getCourseListings() {
        return courseListings;
    }

    /**
     * Processes String time in the RenWeb format to two LocalTime values. This method uses a global String aa
     * to determine the time of day; reset aa to " AM" every time for each "day" (done automatically now).
     * @param in Format: "hh:mm-hh:mm" in 12 hr formats, hh also abbreviated to h if possible
     * @return LocalTime[startTime, endTime]
     */
    private static String aa = " AM";
    private LocalTime[] processTime(String in) {
        String[] times = in.split("-");

        if (times[0].split(":")[0].equals("12")) {
            aa = " PM";
        }
        // checks if hour contains 2 chars, it needs 2 chars to be properly parsed
        if (times[0].length() == 7) {
            times[0] = "0" + times[0];
        }
        times[0] = times[0] + aa;

        if (times[1].split(":")[0].equals("12")) {
            aa = " PM";
        }
        // checks if hour contains 2 chars, it needs 2 chars to be properly parsed
        if (times[1].length() == 7) {
            times[1] = "0" + times[1];
        }
        times[1] = times[1] + aa;
        return new LocalTime[]{renwebTimeFormatter.parseLocalTime(times[0]), renwebTimeFormatter.parseLocalTime(times[1])};
    }

    /**
     * gets a list of thenewpotato.renlib.schedule.Course objects for the specific day of the week. *Currently does not support schedules
     * containing Saturday and Sunday events
     *
     * @param dayOfWeek constant variables Monday through Friday*
     * @return ArrayList of thenewpotato.renlib.schedule.Course objects of the specified day of the week
     */
    public ArrayList<Course> get(int dayOfWeek) {
        aa = " AM";
        ArrayList<Course> result = new ArrayList<>();
        Elements scheduleEntries = scheduleDocument.select(
                "#AutoNumber2 > tbody:nth-child(1) > tr");
        for (int a = 1; a < scheduleEntries.size(); a++) {
            Element entry = scheduleEntries.get(a).select("td:nth-child(" + dayOfWeek + ")").first();
            switch (a % 3) {
                case 0:
                    // location block
                    break;
                case 1:
                    // name block
                    // a block without a name still counts as an empty block of time
                    if (entry.text().equals("")) {
                        result.add(new Course());
                    } else {
                        result.add(getCourseByCode(entry.text()));
                    }
                    break;
                case 2:
                    // time block
                    // if the time block is empty, then the previously added empty Course entry is invalid, thus delete
                    if (entry.text().equals("")) {
                        result.remove(result.size() - 1);
                    } else {
                        LocalTime[] times = processTime(entry.text());
                        result.get(result.size() - 1).startTime =
                                times[0];
                        result.get(result.size() - 1).endTime =
                                times[1];
                    }
                    break;
            }
            /*
            String entryText = entry.text();
            if (!entryText.equals("")) {
                if(a % 3 == 0) {
                    // location block
                } else if ((a + 1) % 3 == 0) {
                    // time block

                    String[] times = entry.text().split("-");

                    if (times[0].split(":")[0].equals("12")) {
                        aa = " PM";
                    }
                    // checks if hour contains 2 chars, it needs 2 chars to be properly parsed
                    if (times[0].length() == 7) {
                        times[0] = "0" + times[0];
                    }
                    times[0] = times[0] + aa;

                    if (times[1].split(":")[0].equals("12")) {
                        aa = " PM";
                    }
                    // checks if hour contains 2 chars, it needs 2 chars to be properly parsed
                    if (times[1].length() == 7) {
                        times[1] = "0" + times[1];
                    }
                    times[1] = times[1] + aa;
                    result.get(result.size() - 1).startTime = formatter.parseLocalTime(times[0]);
                    result.get(result.size() - 1).endTime = formatter.parseLocalTime(times[1]);
                } else {
                    // code name block
                    result.add(getCourseByCode(entry.text()));
                }
            } else {
                // skip over the anticipated blank blocks to prevent time reassignment
                // removal results in time misassignment on blocks before one without a code name but has a time
                a+=2;
            }
            */
        }
        return result;
    }
}
