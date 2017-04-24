package kristof.fr.droshed;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kristof.fr.droshed.Explorer.FileItemExplorer;
import kristof.fr.droshed.Explorer.FolderItemExplorer;
import kristof.fr.droshed.Explorer.ItemExplorer;

public class JsonUtil {

    public static ArrayList<ItemExplorer> toListofCustomItem(String json) throws JSONException {
        if(json==null)
            return null;
        ArrayList<ItemExplorer> list = new ArrayList<>();
        JSONObject jObj = new JSONObject(json);
        return toListofCustomItemfromJsonObject(jObj);
    }

    public static ArrayList<ItemExplorer> toListofCustomItemfromJsonObject(JSONObject json) throws JSONException {
        if(json==null)
            return null;
        ArrayList<ItemExplorer> list = new ArrayList<>();
        JSONArray jsonArray = json.getJSONArray("children");
        for(int i=0;i<jsonArray.length();i++){
            JSONObject jsonLine = jsonArray.getJSONObject(i);
            list.add(createItemExplorer(jsonLine));
        }
        return list;
    }

    private static ItemExplorer createItemExplorer(JSONObject json) throws JSONException {
        String name = json.getString("name");
        String type = json.getString("type");
        if(type.equals("file"))
            return new FileItemExplorer(type,name,R.layout.custom_item_layout);
        else if (type.equals("directory")){
            return new FolderItemExplorer(type,name,R.layout.custom_item_layout,toListofCustomItemfromJsonObject(json));
        }
        else return null;
    }



}