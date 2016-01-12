package ru.collage;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

/**
 * Created by Игорь on 02.01.2016.
 */
public class CollageListAdapter extends BaseAdapter
{
  private Context m_context;
  private LayoutInflater m_inflater;
  private int m_cid;

  public CollageListAdapter(Context context)
  {
    m_context = context;
    m_inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    invalidateCategory();
  }

  public void setCategory(int cid)
  {
    m_cid = cid;
  }

  public void invalidateCategory()
  {
    m_cid = -1;
  }

  @Override
  public int getCount()
  {
    Log.d("myLogs-adapter", "m_cid: " + m_cid + " count: " + CollageManager.instance().getCount(m_cid));
    return CollageManager.instance().getCount(m_cid);
  }

  @Override
  public Object getItem(int i)
  {
    return CollageManager.instance().get(m_cid, i);
  }

  @Override
  public long getItemId(int i)
  {
    return i;
  }

  @Override
  public View getView(int position, View contentView, ViewGroup parent)
  {
    Log.d("myLogs-adapter", "position: " + position);
    View rowView = contentView;
    if (rowView == null)
    {
      rowView = m_inflater.inflate(R.layout.adapter_collage, parent, false);
    }
    ImageView image = (ImageView) rowView.findViewById(R.id.image_collage);
    CollageManager.Collage collage = (CollageManager.Collage) getItem(position);
    Bitmap bitmap = collage.getBitmap();
    image.setImageBitmap(bitmap);
    rowView.setTag(getItem(position));


    //image.setImageBitmap();

    return rowView;
  }
}
