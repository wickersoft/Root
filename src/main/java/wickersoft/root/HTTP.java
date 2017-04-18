package wickersoft.root;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;

public class HTTP {

    public static HTTPResponse http(String url) throws IOException {
        return http(url, 30000);
    }

    public static HTTPResponse http(String url, int timeout) throws IOException {
        return http(url, null, null, timeout);
    }

    public static HTTPResponse http(String url, String postData) throws IOException {
        return http(url, postData, "application/x-www-form-urlencoded", 30000);
    }

    public static HTTPResponse http(String url, String postData, String formType, int timeout) throws IOException {
        URL myurl = new URL(url);
        HttpURLConnection con = (HttpURLConnection) myurl.openConnection();
        con.setConnectTimeout(timeout);
        con.setReadTimeout(timeout);

        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31");

        
        if (postData != null) {
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-length", String.valueOf(postData.length()));
            con.setRequestProperty("Content-Type", formType);
            con.setDoOutput(true);
            con.setDoInput(true);
            DataOutputStream output = new DataOutputStream(con.getOutputStream());
            output.writeBytes(postData);
            output.close();
        } else {
            con.setRequestMethod("GET");
            con.setDoInput(true);
        }

        Map<String, List<String>> headers = con.getHeaderFields();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataInputStream input = new DataInputStream(con.getInputStream());
        for (int c = input.read(); c != -1; c = input.read()) {
            bos.write(c);
        }
        input.close();
        byte[] content = bos.toByteArray();

        return new HTTPResponse(headers, content);
    }

    public static class HTTPResponse {

        public Map<String, List<String>> headers;
        public byte[] content;

        public HTTPResponse(Map<String, List<String>> headers, byte[] content) {
            this.headers = headers;
            this.content = content;
        }

        public String getCookieString() {
            String httpCookies = "";
            List<String> cookies = headers.get("Set-Cookie");
            for (int i = 0; i < cookies.size() - 1; i++) {
                httpCookies += cookies.get(i).substring(0, cookies.get(i).indexOf(";")) + "; ";
                System.out.println("D1 Cookie detected! " + cookies.get(i));
            }
            httpCookies += cookies.get(cookies.size() - 1).substring(0, cookies.get(cookies.size() - 1).indexOf(";"));
            System.out.println("D1 Resulting cookie field: '" + httpCookies + "'");
            return httpCookies;
        }
    }
}
