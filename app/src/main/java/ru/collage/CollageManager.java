package ru.collage;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Игорь on 03.01.2016.
 */
public class CollageManager
{
  static public class Image
  {
    String name;
    Bitmap image;

    boolean Save(String name)
    {
      return true;
    }
    boolean Load(String name)
    {
      return true;
    }
  }

  static public class Collage
  {
    int m_id;
    int m_cid;
    int m_iid;

    public Bitmap getOriginBitmap()
    {
      return ImagesHash.instance().getOriginBitmap(getIid());
    }

    public Bitmap getBitmap()
    {
      return ImagesHash.instance().getImage(getIid());
    }

    public int getId()
    {
      return m_id;
    }

    public int getCid()
    {
      return m_cid;
    }

    public int getIid()
    {
      return m_iid;
    }


    public void setId(int id)
    {
      m_id = id;
    }

    public void setCid(int cid)
    {
      m_cid = cid;
    }

    public void setIid(int iid)
    {
      m_iid = iid;
    }
  }

  /* ---------------------------------------------------------------------------------------------   */
  private static CollageManager m_instance;
  private List<Collage> m_collages;

  public static CollageManager instance()
  {
    if (m_instance == null)
      m_instance = new CollageManager();
    return m_instance;
  }

  private CollageManager()
  {
    m_collages = new ArrayList<Collage>();
  }

  public int getCount()
  {
    return m_collages.size();
  }

  public Collage get(int position)
  {
    return m_collages.get(position);
  }

  public int getCount(int cid)
  {
    if (cid < 0)
      return getCount();

    int count = 0;
    for (int i = 0; i != m_collages.size(); ++i)
      if (m_collages.get(i).getCid() == cid)
        ++count;

    return count;
  }

  public Collage get(int cid, int position)
  {
    if (cid < 0)
      return get(position);

    Log.d("myLogs-get", "position: " + position + " cid " + cid);
    int count = -1;
    for (int i = 0; i != m_collages.size(); ++i)
      if (m_collages.get(i).getCid() == cid)
        if (++count == position)
          return m_collages.get(i);
    Log.d("myLogs-get", "null");

    return null;
  }

  public int findById(int id)
  {
    for (int i = 0; i != m_collages.size(); ++i)
      if (m_collages.get(i).getId() == id)
        return i;

    return -1;
  }

  public void add(Collage collage)
  {
    int index = findById(collage.getId());
    if (index >= 0)
      return;

     m_collages.add(collage);
  }
}
