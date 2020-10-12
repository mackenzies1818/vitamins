import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.FileNotFoundException;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.HttpClientBuilder;


public class vitamin {

    public static final String API_URL = "https://api.nal.usda.gov/fdc/v1/foods";
    public static final String api_key = "VnLODWjndfCHvvDR8AhX3rgZuqcF2SsZy05MQFBB";
    public static final String searchQuery = "/search?query=";
    public static Comparator<Map<String, Object>> mapComparator = new Comparator<Map<String, Object>>() {
        public int compare(Map<String, Object> m1, Map<String, Object> m2) {
            return ((String)m1.get("description")).compareTo((String)m2.get("description"));
        }
    };
    public static void main(String[] args) throws FileNotFoundException {
        //get data from user for what stuff they want to get vitamin info about
        //create excel sheet
        //send api call to get all the info
        nutrientConstants.initializeProperties();
        //getFoodIdFromName();
        //parse the info into the excel sheet
        try {
            List<Map<String, Object>> vitaminInfo = getVitaminsFromName();
            //sort food alphabetically

            Collections.sort(vitaminInfo, mapComparator);
           // Map<String, String> data = new ObjectMapper().readValue(vitaminInfo, HashMap.class);
            ExcelTransformation.writeDataToExcel(vitaminInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private static List<Map<String, Object>> getFoodIdFromName() {
        String url = getUrl();
        HttpClient httpClient = HttpClientBuilder.create().build();
        List<Map<String, Object>> vitaminInfo = new ArrayList<>();
        try {
            //
            //String payload = "{\"fdcIds\":[534358,373052,616350],\"format\":\"abridged\",\"nutrients\":[203,204,205]}";
            vitaminInfo = apiCalls.urlGet(url, httpClient);
        } catch (Exception e) {
            System.out.println("something bad happened, need to debug");
        }
        return vitaminInfo;
    }

    private static List<Map<String, Object>> getVitaminsFromName() {
        String url = API_URL + "?"+ "api_key=" + api_key  ;
        HttpClient httpClient = HttpClientBuilder.create().build();
        List<Map<String, Object>> vitaminInfo = new ArrayList<>();
        try {
            String payload = getPayload();
            //String payload = "{\"fdcIds\":[534358,373052,616350],\"format\":\"abridged\",\"nutrients\":[203,204,205]}";
            vitaminInfo = apiCalls.postToURL(url, payload, httpClient);
        } catch (Exception e) {
            System.out.println("something bad happened, need to debug");
        }
        return vitaminInfo;
    }

    private static String getPayload() {
        String fdcIds = StringUtils.join(nutrientConstants.foodIds, ',');
        String nutrientIds = StringUtils.join(nutrientConstants.nutrientIds, ',');
        return "{\"fdcIds\":["+fdcIds+"],\"format\":\"abridged\",\"nutrients\":["+nutrientIds+"]}";
    }

    private static String getUrl() {
        //https://api.nal.usda.gov/fdc/v1/foods/search?query=cheddar%20cheese&dataType=Foundation,SR%20Legacy&pageSize=25&sortBy=description&sortOrder=asc&brandOwner=Kar%20Nut%20Products%20Company" -H "accept: application/json"
        String foods = "brocoli";
        String pageSize = "&pageSize=25";
        String sorting = "&sortBy=description&sortOrder=asc";
        String url = API_URL + searchQuery + foods + pageSize + sorting + "&api_key=" + api_key +"&dataType=Foundation,SR%20Legacy" ;
        System.out.println(url);
        return url;
    }




}
