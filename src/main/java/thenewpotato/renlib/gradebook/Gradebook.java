package thenewpotato.renlib.gradebook;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import thenewpotato.renlib.Credentials;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Gradebook {

    public Credentials credentials;
    private ArrayList<Course> courses = new ArrayList<>();

    public Gradebook(Credentials credentials) {
        this.credentials = credentials;
        // get schedule directory page
        HttpGet httpGet = new HttpGet("https://tws-tn.client.renweb.com/pw/student/");
        HttpResponse httpResponse = null;
        try {
            httpResponse = credentials.httpClient.execute(httpGet, credentials.httpContext);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        System.out.println("get (directory page): " + httpResponse.getStatusLine());

        // parse out subdirectory page url
        Document document = null;
        try {
            document = Jsoup.parse(EntityUtils.toString(httpResponse.getEntity()));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        Element tr = document.select("div.wrapper:nth-child(1) > div:nth-child(2) > section:nth-child(2) > section:nth-child(2) > section:nth-child(1) > section:nth-child(2) > section:nth-child(3) > section:nth-child(3) > div:nth-child(1) > section:nth-child(1) > table:nth-child(2) > tbody:nth-child(2) > tr").first();
        String overview_url = "https://tws-tn.client.renweb.com/pw/student/" + tr.select("td:nth-child(2) > a:nth-child(1)").first().attr("href");

        // get subdirectory page
        HttpGet overviewGet = new HttpGet(overview_url);
        try {
            httpResponse = credentials.httpClient.execute(overviewGet, credentials.httpContext);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        System.out.println("get (gradebook subject overview): " + httpResponse.getStatusLine());

        // parse out schedule urls
        try {
            document = Jsoup.parse(EntityUtils.toString(httpResponse.getEntity()));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        Elements forms = document.select(".box > section:nth-child(3) > form");
        for (int i = 1; i < forms.size(); i++) {
            Element district = forms.get(i).select("input:nth-child(1)").first();
            Element reportType = forms.get(i).select("input:nth-child(2)").first();
            Element sessionId = forms.get(i).select("input:nth-child(3)").first();
            Element reportHash = forms.get(i).select("input:nth-child(4)").first();
            Element schoolCode = forms.get(i).select("input:nth-child(5)").first();
            Element studentId = forms.get(i).select("input:nth-child(6)").first();
            Element classId = forms.get(i).select("input:nth-child(7)").first();
            Element termId = forms.get(i).select("input:nth-child(8)").first();
            String subjectGradeUrl = "https://tws-tn.client.renweb.com/renweb/reports/parentsweb/parentsweb_reports.cfm?"
                    + district.attr("name") + "=" + district.attr("value") + "&"
                    + "ReportType=Gradebook&"
                    + sessionId.attr("name") + "=" + sessionId.attr("value") + "&"
                    + reportHash.attr("name") + "=" + reportHash.attr("value") + "&"
                    + schoolCode.attr("name") + "=" + schoolCode.attr("value") + "&"
                    + studentId.attr("name") + "=" + studentId.attr("value") + "&"
                    + classId.attr("name") + "=" + classId.attr("value") + "&"
                    + termId.attr("name") + "=" + termId.attr("value");
            // get report page
            httpGet = new HttpGet(subjectGradeUrl);
            try {
                httpResponse = credentials.httpClient.execute(httpGet, credentials.httpContext);
                courses.add(new Course(EntityUtils.toString(httpResponse.getEntity())));
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
            System.out.println("get (report page): " + httpResponse.getStatusLine());
        }
    }

    /**get the gradebook entries belonging to the logged in account
     *
     * @return list of Course (gradebook) objects
     */
    public ArrayList<Course> getCourses() {
        return courses;
    }

}
