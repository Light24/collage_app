package ru.collage;

import android.content.ContentResolver;
import android.content.Context;
import android.util.Pair;

/**
 * Created by Игорь on 03.01.2016.
 */
public class Application
{
  private static Application m_instance;
  private ContentResolver m_contnet_resolver;
  private Context m_context;
  private Pair<Integer, Integer> m_image_size;
  private String m_image_path;

  public static Application instance()
  {
    if (m_instance == null)
    {
      m_instance = new Application();
    }
    return m_instance;
  }

  public static void init(ContentResolver contnet_resolver, Context context, Pair<Integer, Integer> imageSize)
  {
    instance().m_contnet_resolver = contnet_resolver;
    instance().m_image_size = imageSize;
    instance().m_context = context;
  }

  ContentResolver getContentResolver()
  {
    return m_contnet_resolver;
  }
  Context getContext()
  {
    return m_context;
  }
}
