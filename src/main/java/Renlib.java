import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class Renlib {

    private static HttpClient httpClient;
    private static HttpContext httpContext;

    /**
     * Initializes Renlib and performs login.
     *
     * @param districtCode district code of school, e.g. "TWS-TN"
     * @param userName user name of student
     * @param password password of student
     * @param userType user type, the only acceptable value right now is "PARENTSWEB-STUDENT"
     */
    public static void init(String districtCode, String userName, String password, String userType) {
        CookieStore cookieStore = new BasicCookieStore();
        HttpClientBuilder builder = HttpClientBuilder.create().setDefaultCookieStore(cookieStore).setRedirectStrategy(new LaxRedirectStrategy());
        httpClient = builder.build();
        httpContext = new BasicHttpContext();

        // post/login init
        HttpPost httpPost = new HttpPost("https://tws-tn.client.renweb.com/pw/");
        httpPost.setHeader("Referer", "https://tws-tn.client.renweb.com/pw/");
        List<NameValuePair> params = new ArrayList<>(4);
        params.add(new BasicNameValuePair("DistrictCode", districtCode));
        params.add(new BasicNameValuePair("UserName", userName));
        params.add(new BasicNameValuePair("Password", password));
        params.add(new BasicNameValuePair("UserType", userType));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            System.out.println(e.getMessage());
        }

        // perform post/login
        HttpResponse httpResponse;
        try {
            httpResponse = httpClient.execute(httpPost);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
        System.out.println("login status: " + httpResponse.getStatusLine());
    }

    /**
     * gets the list of courses of the logged in student
     *
     * @return a list of the courses of the logged in student in the form a separate Course objects
     */
    public static ArrayList<Course> getCourses() {
        // get schedule directory page
        HttpGet httpGet = new HttpGet("https://tws-tn.client.renweb.com/pw/student/schedules.cfm");
        HttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpGet, httpContext);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        System.out.println("get (directory page) status: " + httpResponse.getStatusLine());

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
            httpResponse = httpClient.execute(httpGet, httpContext);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        System.out.println("get (schedule status): " + httpResponse.getStatusLine());

        // parse schedule
        ArrayList<Course> courses = new ArrayList<>();
        try {
            document = Jsoup.parse(EntityUtils.toString(httpResponse.getEntity()));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        Elements courseEntries = document.select("body > table:nth-child(2) > tbody:nth-child(2) > tr");
        for (int i = 2; i < courseEntries.size() - 1; i++) {
            Elements courseSects = courseEntries.get(i).select("td");
            // empty Course index holder
            courses.add(new Course());
            for (int l = 0; l < courseSects.size(); l++) {
                switch (l) {
                    case 0:
                        courses.get(i - 2).name = courseSects.get(l).text();
                        break;
                    case 1:
                        courses.get(i - 2).code = courseSects.get(l).text();
                        break;
                    case 2:
                        courses.get(i - 2).location = courseSects.get(l).text();
                        break;
                    case 3:
                        courses.get(i - 2).instructor = courseSects.get(l).text();
                        break;
                }
            }
        }

        return courses;
    }

}
