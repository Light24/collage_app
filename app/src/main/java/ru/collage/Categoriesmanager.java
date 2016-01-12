package ru.collage;

import android.graphics.Bitmap;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Игорь on 03.01.2016.
 */
public class CategoriesManager
{
  static public class Category
  {
    int m_id;
    int m_iid;
    String m_name;

    public Bitmap getBitmap()
    {
      return ImagesHash.instance().getImage(getIid());
    }

    public int getId()
    {
      return m_id;
    }

    public int getIid()
    {
      return m_iid;
    }

    public String getName()
    {
      return m_name;
    }


    public void setId(int id)
    {
      m_id = id;
    }

    public void setIid(int iid)
    {
      m_iid = iid;
    }

    public void setName(String name)
    {
      m_name = name;
    }
  }

  /* ---------------------------------------------------------------------------------------------   */
  private static CategoriesManager m_instance;
  private List<Category> m_data;

  public static CategoriesManager instance()
  {
    if (m_instance == null)
      m_instance = new CategoriesManager();
    return m_instance;
  }

  private CategoriesManager()
  {
    m_data = new ArrayList<Category>();
  }

  public int getCount()
  {
    return m_data.size();
  }

  public Category get(int position)
  {
    return m_data.get(position);
  }

  public int findById(int id)
  {
    for (int i = 0; i != m_data.size(); ++i)
      if (m_data.get(i).getId() == id)
        return i;

    return -1;
  }

  public void add(Category item)
  {
    int index = findById(item.getId());
    if (index >= 0)
      return;

    m_data.add(item);
  }
}
