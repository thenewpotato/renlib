package thenewpotato.renlib.homework;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.joda.time.LocalDate;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import thenewpotato.renlib.Credentials;

import java.util.ArrayList;

public class Week {

    private static final String DIV_CLASS_NAME = "homework_item";
    public static final int MONDAY = 0;
    public static final int TUESDAY = 1;
    public static final int WEDNESDAY = 2;
    public static final int THURSDAY = 3;
    public static final int FRIDAY = 4;
    public static final int SATURDAY = 5;
    public static final int SUNDAY = 6;

    private Credentials credentials;
    private Document document;

    public Week(Credentials credentials, LocalDate ofWeek) {
        // get homework directory page
        HttpGet httpGet = new HttpGet("https://tws-tn.client.renweb.com/pw/student/homework.cfm");
        HttpResponse httpResponse = null;
        try {
            httpResponse = credentials.httpClient.execute(httpGet, credentials.httpContext);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        System.out.println("get (hw directory page): " + httpResponse.getStatusLine());

        // parse out homework url
        Document hwDocument = null;
        try {
            hwDocument = Jsoup.parse(EntityUtils.toString(httpResponse.getEntity()));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        Element sect = hwDocument.select(".right_section > section:nth-child(3)").first();
        String studentId = sect.attr("id").substring(3);
        String scheduleUrl = "https://tws-tn.client.renweb.com/pw/student/homework-print.cfm?studentid="
                + studentId + "&weekof=" + ofWeek.toString("MM/dd/yyyy") + "&events=0";
        System.out.println("schedule URL: " + scheduleUrl);

        // get homework directory page
        httpGet = new HttpGet(scheduleUrl);
        try {
            httpResponse = credentials.httpClient.execute(httpGet, credentials.httpContext);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        System.out.println("get (hw page): " + httpResponse.getStatusLine());
        try {
            document = Jsoup.parse(EntityUtils.toString(httpResponse.getEntity()));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Day> getDays() {
        ArrayList<Day> days = new ArrayList<>();
        for(Element li : document.select("li.dateli")) {
            days.add(new Day());
            for(Element div : li.select("div")) {
                if (div.attr("class").equals(DIV_CLASS_NAME)) {
                    String courseName = div.select("strong").text();
                    String assignment = div.text().replace(courseName, "").substring(3);
                    days.get(days.size() - 1).assignments.add(
                            new Assignment(
                                    courseName,
                                    assignment));
                }
            }
        }
        return days;
    }

}
