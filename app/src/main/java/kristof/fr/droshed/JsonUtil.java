package kristof.fr.droshed;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kristof.fr.droshed.custom.CustomItem;

public class JsonUtil {

    public static ArrayList<CustomItem> toObject(String json) throws JSONException {
        ArrayList<CustomItem> list = new ArrayList<>();
        JSONObject jObj = new JSONObject(json);
        String models = jObj.getString("results");
        Pattern pattern = Pattern.compile("(?<=\\{name:)(.*?)(?=\\})");
        Matcher matcher = pattern.matcher(models);
            while (matcher.find()){
                String data = matcher.group(0);
                list.add(new CustomItem(data));
            }
        return list;
    }
}