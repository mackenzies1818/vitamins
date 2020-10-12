import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

public class nutrientConstants {

    public static final List<String> nutrients = Arrays.asList(
            "Protein",
            "Total_lipid",
            "Carbohydrate",
            "Fiber",
            "Sugars",
            "Calcium",
            "Iron",
            "Magnesium",
            "Phosphorus",
            "Potassium",
            "Sodium",
            "Zinc",
            "Vitamin_C",
            "Thiamin",
            "Riboflavin",
            "Niacin",
            "Vitamin_B-6",
            "Folate",
            "Vitamin_A",
            "Vitamin_E",
            "Vitamin_K"
    );
    public static final Map<String, String> nutrientDailyAmount = new HashMap<String,String>();
    public static final Map<String, String> nutrientUnits= new HashMap<String,String>();
    public static final Map<String, String> mappings = new HashMap<String,String>();
    public static final List<String> nutrientIds = new ArrayList<String>();
    public static final List<String> foodIds = new ArrayList<String>();
    public static void initializeProperties() throws FileNotFoundException {
        Properties prop = new Properties();
        Properties map = new Properties();
        Properties map1 = new Properties();
        Properties map2 = new Properties();
        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream is = classloader.getResourceAsStream("nutrients.properties");
            prop.load(is);
            for (Map.Entry<Object, Object> entry : prop.entrySet()) {
                nutrientDailyAmount.put((String) entry.getKey(), (String) entry.getValue());
            }
            InputStream is1 = classloader.getResourceAsStream("APItoExcelMapping.properties");
            map.load(is1);
            for (Map.Entry<Object, Object> entry : map.entrySet()) {
                mappings.put((String) entry.getKey(), (String) entry.getValue());
                nutrientIds.add((String)entry.getValue());
            }
            InputStream is2 = classloader.getResourceAsStream("food_ids.properties");
            map1.load(is2);
           for (Map.Entry<Object, Object> entry : map1.entrySet()) {
               foodIds.add((String)entry.getValue());
            }
            InputStream is3 = classloader.getResourceAsStream("unitsOfMeasure.properties");
            map2.load(is3);
            for (Map.Entry<Object, Object> entry : map2.entrySet()) {
                nutrientUnits.put((String) entry.getKey(), (String) entry.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
