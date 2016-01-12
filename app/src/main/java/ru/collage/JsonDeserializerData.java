package ru.collage;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Игорь on 25.11.2015.
 */

class JsonDeserializeData implements JsonDeserializer<JsonDeserializeData.JsonData>
{
  static public class JsonData
  {
    private String              m_key;
    private String              m_value;
    private ArrayList<JsonData> m_object;

    public void setKey(String key)                    { m_key = key; }
    public void setValue(String value)                { m_value = value; }
    public void setObject(ArrayList<JsonData> object) { m_object = object; }

    public String getKey()                 { return m_key; }
    public String getValue()               { return m_value; }
    public ArrayList<JsonData> getObject() { return m_object; }
  }

  public JsonDeserializeData.JsonData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
  {
    JsonData jsonData = new JsonData();
    jsonData.setObject(new ArrayList<JsonData>());
    deserializeInternal(jsonData.getObject(), json, typeOfT, context);
    return jsonData;
  }

  private void deserializeInternal(ArrayList<JsonData> jsonData, JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
  {/*
    if (json.isJsonArray())
    {
      jsonData.add(new JsonData());
      jsonData.get(jsonData.size()-1).setObject(new ArrayList<JsonData>());
      JsonArray jsonArray = json.getAsJsonArray();
      for (int index = 0; index != jsonArray.size(); ++index)
        deserializeInternal(jsonData.get(jsonData.size()-1).getObject(), jsonArray.get(index), typeOfT, context);
      return;
    }*/

    if (json.isJsonPrimitive())
    {
      jsonData.add(new JsonData());
      jsonData.get(jsonData.size() - 1).setValue(json.getAsJsonPrimitive().getAsString());
      return;
    }

    JsonObject jsonObj = json.getAsJsonObject();
    for(Map.Entry<String,JsonElement> entry : jsonObj.entrySet())
    {
      jsonData.add(new JsonData());

      jsonData.get(jsonData.size()-1).setKey(entry.getKey());
      JsonElement innerJson = entry.getValue();

      if (innerJson.isJsonArray())
      {
        jsonData.get(jsonData.size()-1).setObject(new ArrayList<JsonData>());
        JsonArray jsonArray = innerJson.getAsJsonArray();
        for (int index = 0; index != jsonArray.size(); ++index)
          deserializeInternal(jsonData.get(jsonData.size()-1).getObject(), jsonArray.get(index), typeOfT, context);
      }
      else if (innerJson.isJsonObject())
      {
        jsonData.get(jsonData.size()-1).setObject(new ArrayList<JsonData>());
        deserializeInternal(jsonData.get(jsonData.size()-1).getObject(), innerJson, typeOfT, context);
      }
      else if (innerJson.isJsonPrimitive())
      {
        jsonData.get(jsonData.size()-1).setValue(innerJson.getAsString());
      }
    }
  }
}