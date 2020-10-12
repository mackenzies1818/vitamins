import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class getVitamin {
    public static List<Map<String, Object>> postToURL(String url, String payload, HttpClient httpClient) throws IOException, IllegalStateException, UnsupportedEncodingException, RuntimeException {
        HttpPost postRequest = new HttpPost(url);

        StringEntity input = new StringEntity(payload);
        input.setContentType("application/json");
        postRequest.setEntity(input);
        postRequest.setHeader("Accept", "application/json");
        postRequest.setHeader("Content-Type", "application/json");
        HttpResponse response = httpClient.execute(postRequest);

        if (response.getStatusLine().getStatusCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + response.getStatusLine().getStatusCode());
        }

        BufferedReader br = new BufferedReader(
                new InputStreamReader((response.getEntity().getContent())));
        List<Map<String, Object>> data = new ObjectMapper().readValue(response.getEntity().getContent(), List.class);
        return data;
    }
}