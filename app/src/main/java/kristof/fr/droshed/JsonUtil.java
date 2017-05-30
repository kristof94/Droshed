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
            ItemExplorer itemExplorer = createItemExplorer(jsonLine);
            list.add(itemExplorer);
        }
        return list;
    }

    public static ItemExplorer toCustomItem(String json) throws JSONException {
        if(json==null)
            return null;
        ArrayList<ItemExplorer> list = new ArrayList<>();
        JSONObject jObj = new JSONObject(json);
        return createItemExplorer(jObj);
    }

    private static ItemExplorer createItemExplorer(JSONObject json) throws JSONException {
        String name = json.getString("name");
        String type = json.getString("type");
        String path = json.getString("path");
        int version = 0;
        if(json.has("version")){
            version = json.getInt("version");
        }
        ItemExplorer itemExplorer = null;
        if(type.equals("file"))
            itemExplorer = new FileItemExplorer(type,name,R.layout.custom_item_layout,path,version);
        else if (type.equals("directory")){
            itemExplorer =  new FolderItemExplorer(type,name,R.layout.custom_item_folder_layout,path,toListofCustomItemfromJsonObject(json));
        }
        return itemExplorer;
    }

    public static String getContent(JSONObject json) throws JSONException {
        return json.getString("content");
    }

    public static JSONObject createJsonFileUploadString(String filename,String content,String path,String type,int version) {
        JSONObject object = new JSONObject();
        try {
            object.put("filename", filename);
            object.put("path",path);
            object.put("content",content);
            object.put("type",type);
            object.put("version",version);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  object;
    }

    public static JSONObject createJsonFileUploadString(FileItemExplorer fileItemExplorer,String content) {
        return createJsonFileUploadString(fileItemExplorer.getName(),content,fileItemExplorer.getPath(),fileItemExplorer.getType(),fileItemExplorer.getVersion());
    }



}