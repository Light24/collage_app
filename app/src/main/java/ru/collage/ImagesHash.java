package ru.collage;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Игорь on 03.01.2016.
 */
public class ImagesHash
{
  private static ImagesHash m_instance;
  private Map<Integer, Bitmap> m_data = new HashMap<Integer, Bitmap>();

  public static ImagesHash instance()
  {
    if (m_instance == null)
      m_instance = new ImagesHash();
    return m_instance;
  }

  private ImagesHash()
  {

  }


  public Bitmap getImage(int imageTitle)
  {
    Bitmap bitmap;
    if (m_data.containsKey(imageTitle))
    {
      bitmap = m_data.get(imageTitle);
    }
    else
    {
      String imageId = getImageId(String.valueOf(imageTitle));
      Log.d("myLogs-data", "imageId: " + imageId);
      if (imageId == null)
      {
        return null;
      }

      bitmap = MediaStore.Images.Thumbnails.getThumbnail(Application.instance().getContentResolver(), Integer.valueOf(imageId), MediaStore.Images.Thumbnails.MINI_KIND, null);
    }
    return bitmap;
  }



  public void insertImage(int imageTitle, Bitmap bitmap)
  {
    if (getImageId(String.valueOf(imageTitle)) != null)
      return;

    MediaStore.Images.Media.insertImage(Application.instance().getContentResolver(), bitmap, String.valueOf(imageTitle), String.valueOf(imageTitle));
    prepareImage(imageTitle);
  }

  public boolean prepareImage(int imageTitle)
  {
    if (m_data.containsKey(imageTitle))
      return true;

    Bitmap bitmap = getImage(imageTitle);
    if (bitmap == null)
      return false;

    m_data.put(imageTitle, bitmap);

    return true;
  }

  private String getImageId(String imageTitle)
  {
    Cursor cursor = Application.instance().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
      new String[] { MediaStore.Images.ImageColumns._ID },
      MediaStore.Images.ImageColumns.TITLE + " = " + "?",
      new String[] { imageTitle },
      null);

    if (cursor == null)
      return null;

    if (cursor.getCount() < 1)
      return null;

    cursor.moveToFirst();
    String data = cursor.getString(0);
    cursor.close();
    return data;
  }


  private String getContentUri(String imageTitle)
  {
    Cursor cursor = Application.instance().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
      new String[] { MediaStore.Images.ImageColumns.DATA },
      MediaStore.Images.ImageColumns.TITLE + " = " + "?",
      new String[] { imageTitle },
      null);

    if (cursor == null)
      return null;

    if (cursor.getCount() < 1)
      return null;

    cursor.moveToFirst();
    String data = cursor.getString(0);
    cursor.close();
    return data;
  }

  public Bitmap getOriginBitmap(int imageTitle)
  {
    Bitmap bitmap = null;

    String imageURI = getContentUri(String.valueOf(imageTitle));
    Log.d("myLogs-data", "getOriginImage imageURI: " + imageURI);
    if (imageURI == null)
    {
      return null;
    }

    try
    {
      bitmap = MediaStore.Images.Media.getBitmap(Application.instance().getContentResolver(), Uri.fromFile(new File(imageURI)));
    }
    catch (IOException e)
    {

    }
    return bitmap;
  }

  public Bitmap getOriginBitmap(String imageURI)
  {
    Bitmap bitmap = null;

    Log.d("myLogs-data", "getOriginImage imageURI: " + imageURI);
    if (imageURI == null)
    {
      return null;
    }

    try
    {
      bitmap = MediaStore.Images.Media.getBitmap(Application.instance().getContentResolver(), Uri.fromFile(new File(imageURI)));
    }
    catch (IOException e)
    {

    }
    return bitmap;
  }

}
