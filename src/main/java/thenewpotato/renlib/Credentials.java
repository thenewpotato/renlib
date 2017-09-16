package thenewpotato.renlib;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.RequestUserAgent;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class Credentials {

    public static String tempPassword = "20020320hua";

    public HttpClient httpClient;
    public HttpContext httpContext;

    public Credentials(String districtCode, String userName, String password, String userType) {
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
        System.out.println("login: " + httpResponse.getStatusLine());
    }

}
