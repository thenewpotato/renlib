package thenewpotato.renlib;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import thenewpotato.renlib.gradebook.Category;

import thenewpotato.renlib.schedule.Course;
import thenewpotato.renlib.schedule.Schedule;

import java.io.FileWriter;
import java.io.PrintWriter;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.client.util.DateTime;

import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class Test {

    /** Application name. */
    private static final String APPLICATION_NAME =
            "Google Calendar API Java Quickstart";

    /** Directory to store user credentials for this application. */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
            System.getProperty("user.home"), ".credentials/calendar-java-quickstart");

    /** Global instance of the {@link FileDataStoreFactory}. */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY =
            JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;

    /** Global instance of the scopes required by this quickstart.
     *
     * If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/calendar-java-quickstart
     */
    private static final List<String> SCOPES =
            Arrays.asList(CalendarScopes.CALENDAR);

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize() throws IOException {
        // Load client secrets.
        InputStream in =
                Test.class.getResourceAsStream("/client_secret.json");
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                        .setDataStoreFactory(DATA_STORE_FACTORY)
                        .setAccessType("offline")
                        .build();
        Credential credential = new AuthorizationCodeInstalledApp(
                flow, new LocalServerReceiver()).authorize("user");
        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    /**
     * Build and return an authorized Calendar client service.
     * @return an authorized Calendar client service
     * @throws IOException
     */
    public static com.google.api.services.calendar.Calendar
    getCalendarService() throws IOException {
        Credential credential = authorize();
        return new com.google.api.services.calendar.Calendar.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public static void main(String[] args) throws IOException{
        // Build a new authorized API client service.
        // Note: Do not confuse this class with the
        //   com.google.api.services.calendar.model.Calendar class.
        com.google.api.services.calendar.Calendar service =
                getCalendarService();

        Credentials credentials = new Credentials(
                "TWS-TN",
                "thenewpotato",
                "***REMOVED***",
                "PARENTSWEB-STUDENT");

        /*//gradebook
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
        */

        // schedule
        Schedule schedule = new Schedule(credentials);
        for (Course course : schedule.get(Schedule.MONDAY)) {
            /*//gcal
            DateTime start = new DateTime("2017-09-26T" + course.startTime.toString("HH:mm:ss" + "-05:00"));
            DateTime end = new DateTime("2017-09-26T" + course.endTime.toString("HH:mm:ss") + "-05:00");
            Event event = new Event()
                    .setSummary(course.code)
                    .setStart(new EventDateTime().setDateTime(start).setTimeZone("America/Chicago"))
                    .setEnd(new EventDateTime().setDateTime(end).setTimeZone("America/Chicago"))
                    .setRecurrence(Arrays.asList("RRULE:FREQ=WEEKLY;UNTIL=20171220T170000Z"));
            service.events().insert(calendarAccount, event).execute();
            */
            System.out.println("In " + course.location +
                    " for " + course.name +
                    " at T" + course.startTime.toString("HH:mm:ss") + "-06:00-" + course.endTime);
        }

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
