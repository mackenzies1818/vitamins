import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/*
This class makes an API request, currently supports HTTP GET and POST
*/

public class apiCalls {

    public static List<Map<String, Object>> postToURL(String url, String payload, HttpClient httpClient) throws RuntimeException, UnsupportedEncodingException {
        HttpPost postRequest = new HttpPost(url);

        StringEntity input = new StringEntity(payload);
        input.setContentType("application/json");
        postRequest.setEntity(input);
        postRequest.setHeader("Accept", "application/json");
        postRequest.setHeader("Content-Type", "application/json");
        return sendRequest(httpClient, postRequest);
    }

    public static List<Map<String, Object>> urlGet(String url, HttpClient httpClient) throws RuntimeException {
        HttpGet getRequest = new HttpGet(url);
        getRequest.setHeader("Accept", "application/json");
        getRequest.setHeader("Content-Type", "application/json");
        return sendRequest(httpClient, getRequest);
    }

    private static List<Map<String,Object>> sendRequest(HttpClient httpClient, HttpRequestBase requestBody) {
        try {
            HttpResponse response = httpClient.execute(requestBody);

            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatusLine().getStatusCode());
            }

            BufferedReader br = new BufferedReader(
                    new InputStreamReader((response.getEntity().getContent())));
            List<Map<String, Object>> data = new ObjectMapper().readValue(response.getEntity().getContent(), List.class);
            return data;
        } catch (IOException e) {
            System.out.println("error calling api");
            return null;
        }

    }
}
