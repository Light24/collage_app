package ru.collage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.os.Debug;
import android.os.Environment;
import android.util.Log;
import android.util.Pair;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


/**
 * Created by Игорь on 23.11.2015.
 */

public class AsyncRequestManager
{
  class RequestData
  {
    private List<Pair<String, String>> m_listData;
    private String m_encodeData;
    private String m_url;
    private boolean m_isGetRequest;
    private Object m_object;

    RequestData(boolean isGetRequest)
    {
      m_isGetRequest = isGetRequest;
      m_url = new String();
    }

    public void addData(String key, String value)
    {
      if (m_listData == null)
        m_listData = new ArrayList<Pair<String, String>>();
      m_listData.add(new Pair<String, String>(key, value));
      encodeData(key, value);
    }

    public boolean isGetRequest()
    {
      return m_isGetRequest;
    }

    public String getData()
    {
      return m_encodeData;
    }

    public String getUrl()
    {
      return m_url;
    }

    public void setUrl(String url)
    {
      m_url = url;
    }

    private Object getObject()
    {
      return m_object;
    }

    private void setObject(Object object)
    {
      m_object = object;
    }



    private void encodeData(String key, String value)
    {
      if (m_encodeData == null)
        m_encodeData = new String();
      else
        m_encodeData += "&";

      m_encodeData += key + "=" + value;
    }
  };

  List<AsyncRequest> m_asyncRequests;

  Context m_context;

  AsyncRequestManager(Context context)
  {
    m_context = context;
    m_asyncRequests = new ArrayList<AsyncRequest>();
  }
/* http://collagetest.tioo.ru */

  public void get_categories()
  {
    AsyncRequest currentRequest = new AsyncRequest();
    m_asyncRequests.add(currentRequest);

    RequestData requestData = new RequestData(true);
    requestData.addData("action", "get_cats");
    requestData.setUrl("http://id6921786.myjino.ru/api.php");

    currentRequest.execute(new Pair<RequestData, ResponseObject>(requestData, new ResponseObjectGetCategories()));
  }


  public void get_collages_by_category(int cid)
  {
    AsyncRequest currentRequest = new AsyncRequest();
    m_asyncRequests.add(currentRequest);

    RequestData requestData = new RequestData(true);
    requestData.addData("action", "get_collages_by_cat");
    requestData.addData("cid", String.valueOf(cid));
    requestData.setUrl("http://id6921786.myjino.ru/api.php");
    //requestData.setObject(zId);

    currentRequest.execute(new Pair<RequestData, ResponseObject>(requestData, new ResponseObjectGetCollagesNew()));
  }


  public void get_collages_new()
  {
    AsyncRequest currentRequest = new AsyncRequest();
    m_asyncRequests.add(currentRequest);

    RequestData requestData = new RequestData(true);
    requestData.addData("action", "get_collages_new");
    requestData.setUrl("http://id6921786.myjino.ru/api.php");
    //requestData.setObject(zId);

    currentRequest.execute(new Pair<RequestData, ResponseObject>(requestData, new ResponseObjectGetCollagesNew()));
  }

  public void get_image(int imageId)
  {
    AsyncRequest currentRequest = new AsyncRequest();
    m_asyncRequests.add(currentRequest);

    RequestData requestData = new RequestData(true);
    requestData.addData("action", "get_image");
    requestData.addData("iid", String.valueOf(imageId));
    requestData.setUrl("http://id6921786.myjino.ru/api.php");
    requestData.setObject(Integer.valueOf(imageId));

    currentRequest.execute(new Pair<RequestData, ResponseObject>(requestData, new ResponseObjectGetImage()));
  }

  abstract class ResponseObject
  {
    private String m_data;
    abstract public void doResponse(RequestData requestData);

    public void data(String data)
    {
      m_data = data;
    }
    public String data() { return m_data;  }
    public void invalidateData() { data(null); }

    protected JsonDeserializeData.JsonData getDecodeData()
    {
      GsonBuilder gsonBuilder = new GsonBuilder();
      gsonBuilder.registerTypeAdapter(JsonDeserializeData.JsonData.class, new JsonDeserializeData());
      Gson gson = gsonBuilder.create();

      JsonDeserializeData.JsonData jsonData = gson.fromJson(data(), JsonDeserializeData.JsonData.class);
      return jsonData;
    }

    protected int getNodeIndexByName(List<JsonDeserializeData.JsonData> list, String nodeName)
    {
      for (int index = 0; index != list.size(); ++index)
      {
        JsonDeserializeData.JsonData item = list.get(index);
        if (item.getKey().equals(nodeName))
          return index;
      }
      return list.size();
    }
  }

  static class RequestObject
  {
    static public String doRequest(String url_link, String data, boolean isGetReqest)
    {
      try {
        URL url = new URL(url_link + (isGetReqest == true ? "?" + data : ""));
        Log.d("myLogs-route", url_link + (isGetReqest == true ? "?" + data : ""));
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setDoInput(true);

        if (!isGetReqest)
        {
          conn.setRequestMethod("POST");

          if (data != null)
          {
            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8")); //WINDOWS-1251
            writer.write(data);

            writer.flush();
            writer.close();
            os.close();
          } else
          {
            conn.setDoOutput(false);
          }
        }
        conn.connect();

        logHeap();

        InputStream in = conn.getInputStream();

        /*InputStreamReader inr = new InputStreamReader(conn.getInputStream(), "windows-1251");
        char buffer[] = new char[1251];
        inr.read(buffer);
        Log.d("myLogs", "buffer.. " + buffer);
        inr.close();*/

        //BufferedReader bReader = new  BufferedReader(new InputStreamReader(in));
        Log.d("myLogs", "response.. " + in.available());

        logHeap();

        int readBytes, readBytesTotal = 0, bytesSize = 1024;
        byte bytes[] = new byte[bytesSize];
        while ((readBytes = in.read(bytes, readBytesTotal, bytesSize - readBytesTotal)) > 0)
        {
          readBytesTotal += readBytes;
          if (bytesSize - readBytesTotal <= 100)
          {
            byte _bytes[] = bytes;

            bytesSize *= 5;
            bytes = new byte[bytesSize];

            int index = -1;
            for (byte _byte : _bytes)
              bytes[++index] = _byte;
          }
        }
        in.close();
        conn.disconnect();

        logHeap();
        Log.d("myLogs", "result. readBytesTotal: " + readBytesTotal + " length: " + bytes.length);

        String response = new String();
          ;//response += new String(buffer, 0, readBytes);
        //response = "[{\"id\":\"5\",\"cid\":\"1\",\"iid\":\"28\"}]";
       // if (readBytesTotal > 10000)
        response = new String(bytes, 0, readBytesTotal, "cp1251"); //cp1251 utf-8
        bytes = null;
        Log.d("myLogs", "end..");

        logHeap();

        Log.d("myLogs", "response: " + response);
        return response;
      }
      catch (IOException e)
      {
        Log.d("myLogs", "error: " + url_link + "e " + e.getMessage() + "e.getStackTrace " + e.getStackTrace().toString());
        return null;
      }
    }
  }

  /**
   * Получение списка категорий
   */
  class ResponseObjectGetCategories extends ResponseObject
  {
    public void doResponse(RequestData requestData)
    {
      List<JsonDeserializeData.JsonData> jsonListData = getDecodeData().getObject().get(0).getObject();

      for (JsonDeserializeData.JsonData jsomItem : jsonListData)
      {
        CategoriesManager.Category category = new CategoriesManager.Category();
        for (JsonDeserializeData.JsonData item : jsomItem.getObject())
        {
          Log.d("myLogs-data", "prepare: " + item.getKey());
          switch (item.getKey())
          {
            case "id":
              category.setId(Integer.valueOf(item.getValue()));
              break;
            case "iid":
              category.setIid(Integer.valueOf(item.getValue()));
              break;
            case "name":
              category.setName(item.getValue());
              break;
          }
        }
        Log.d("myLogs", "11111`");
        CategoriesManager.instance().add(category);
        if (!ImagesHash.instance().prepareImage(category.getIid()))
        {
          Log.d("myLogs", "333333`");
          get_image(category.getIid());
        }
      }

      if (m_context instanceof Events.OnUpdateAdapterData)
        ((Events.OnUpdateAdapterData) m_context).onUpdateAdapterData();
    }
  }

  /**
   * Получение координат по названию локации
   */
  class ResponseObjectGetCollagesNew extends ResponseObject
  {
    public void doResponse(RequestData requestData)
    {
      Log.d("myLogs", "ResponseObjectGetCollagesNew");
      List<JsonDeserializeData.JsonData> jsonListData = getDecodeData().getObject().get(0).getObject();

      for (JsonDeserializeData.JsonData jsomItem : jsonListData)
      {
        Log.d("myLogs-addCollage", "jsomItem.getKey: " + jsomItem.getKey() + " value: " + jsomItem.getValue());
        CollageManager.Collage collage = new CollageManager.Collage();
        for (JsonDeserializeData.JsonData item : jsomItem.getObject())
        {
          Log.d("myLogs-data", "prepare: " + item.getKey());
          switch (item.getKey())
          {
            case "id":
              collage.setId(Integer.valueOf(item.getValue()));
              break;
            case "cid":
              collage.setCid(Integer.valueOf(item.getValue()));
              break;
            case "iid":
              collage.setIid(Integer.valueOf(item.getValue()));
              break;
          }
        }
        Log.d("myLogs-addCollage", "collage.setId: " + collage.getId());
        CollageManager.instance().add(collage);
        if (!ImagesHash.instance().prepareImage(collage.getIid()))
        {
          get_image(collage.getIid());
        }
      }


      if (m_context instanceof Events.OnUpdateAdapterData)
        ((Events.OnUpdateAdapterData) m_context).onUpdateAdapterData();
    }
  }

  public static void logHeap()
  {
    Double allocated = new Double(Debug.getNativeHeapAllocatedSize())/new Double((1048576));
    Double available = new Double(Debug.getNativeHeapSize())/1048576.0;
    Double free = new Double(Debug.getNativeHeapFreeSize())/1048576.0;
    DecimalFormat df = new DecimalFormat();
    df.setMaximumFractionDigits(2);
    df.setMinimumFractionDigits(2);

    Log.d("myLogs-APP", "debug. =================================");
    Log.d("myLogs-APP", "debug.heap native: allocated " + df.format(allocated) + "MB of " + df.format(available) + "MB (" + df.format(free) + "MB free) in [");
    Log.d("myLogs-APP", "debug.memory: allocated: " + df.format(new Double(Runtime.getRuntime().totalMemory()/1048576)) + "MB of " + df.format(new Double(Runtime.getRuntime().maxMemory()/1048576))+ "MB (" + df.format(new Double(Runtime.getRuntime().freeMemory()/1048576)) +"MB free)");
    System.gc();
    System.gc();

    // don't need to add the following lines, it's just an app specific handling in my app
   /* if (allocated>=(new Double(Runtime.getRuntime().maxMemory())/new Double((1048576))-MEMORY_BUFFER_LIMIT_FOR_RESTART)) {
      android.os.Process.killProcess(android.os.Process.myPid());
    }*/
  }
  /**
   * Получение картинки в бинарном виде по её id
   */
  class ResponseObjectGetImage extends ResponseObject
  {
    public void doResponse(RequestData requestData)
    {
      Log.d("myLogs", "ResponseObjectGetImage");
      byte bytes[] = null;
      try
      {
        bytes = data().getBytes("cp1251"); //cp1251 utf-8
      }
      catch(UnsupportedEncodingException e)
      {
        Log.d("myLogs", "UnsupportedEncodingException");
      }

/*
      try
      {

        Log.d("myLogs", " Envir: " + m_context.getFilesDir() + "/12345.bmp");
        File file = new File(m_context.getFilesDir(), "12345.bmp");
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(bytes);
        fos.close();
      }
      catch (IOException e)
      {
      }*/
      //try
      {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4; // might try 8 also
        InputStream is = new ByteArrayInputStream(bytes, 0, bytes.length);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);

        Log.d("myLogs", "size: " + bytes.length);
        //InputStream is = new ByteArrayInputStream(bytes, 0, bytes.length);
        //Bitmap bitmap = BitmapFactory.decodeStream(is);
        //is.close();

        //Bitmap bitmap = BitmapFactory.decodeStream(is);
        Log.d("myLogs", "bitmap != null: " + (bitmap != null));
        ImagesHash.instance().insertImage((Integer) requestData.getObject(), bitmap);
      }
      //catch (IOException e)
      {
      }
/*
      try
      {

        Log.d("myLogs", " Envir: " + m_context.getFilesDir() + "/12345.bmp");
        File file = new File(m_context.getFilesDir(), "12345.bmp");
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(bytes);
        fos.close();
      }
      catch (IOException e)
      {
      }*/


      if (m_context instanceof Events.OnUpdateAdapterData)
        ((Events.OnUpdateAdapterData) m_context).onUpdateAdapterData();

      //ImageView image = (ImageView) ((Activity) m_context).findViewById(R.id._image);
      //image.setImageBitmap(bitmap);

      //if (!ImagesHash.instance().prepareImage(collage.getId()));
      //get_image(collage.getId());
/*
      Log.d("myLogs", "success.");

      //if (!ImagesHash.instance().prepareImage(collage.getId()));
      //get_image(collage.getId());

      //if (m_context instanceof Events.OnGetCoordinates)
      //  ((Events.OnGetCoordinates) m_context).onUpdateCoordinates((String) requestData.getObject(), new LatLng(lat, lng));*/
    }
  }

  class AsyncRequest extends AsyncTask<Pair<RequestData, ResponseObject>, Void, Pair<RequestData, ResponseObject>>
  {
    protected Pair<RequestData, ResponseObject> doInBackground(Pair<RequestData, ResponseObject>... args)
    {
      RequestData request = args[0].first;
      ResponseObject response  = args[0].second;
      String data = RequestObject.doRequest(request.getUrl(), request.getData(), request.isGetRequest());
      response.data(data);

      return new Pair<RequestData, ResponseObject>(request, response);
    }

    protected void onPostExecute(Pair<RequestData, ResponseObject> args)
    {
      try
      {
        args.second.doResponse(args.first);
      }
      catch (NullPointerException exception)
      {

      }
      finally
      {
        int index = m_asyncRequests.indexOf(this);
        if (index >= 0 && index < m_asyncRequests.size())
          m_asyncRequests.remove(index);
      }
    }
  }
}