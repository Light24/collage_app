package ru.collage;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Игорь on 04.01.2016.
 */
public class CategoriesListAdapter extends BaseAdapter
{
  private Context m_context;
  private LayoutInflater m_inflater;

  public CategoriesListAdapter(Context context)
  {
    m_context = context;
    m_inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
  }

  @Override
  public int getCount()
  {
    return CategoriesManager.instance().getCount();
  }

  @Override
  public Object getItem(int i)
  {
    return CategoriesManager.instance().get(i);
  }

  @Override
  public long getItemId(int i)
  {
    return i;
  }

  @Override
  public View getView(int position, View contentView, ViewGroup parent)
  {
    View rowView = contentView;
    if (rowView == null)
    {
      rowView = m_inflater.inflate(R.layout.adapter_categories, parent, false);
    }
    ImageView image = (ImageView) rowView.findViewById(R.id.image_collage);
    CategoriesManager.Category data = (CategoriesManager.Category) getItem(position);
    Bitmap bitmap = data.getBitmap();
    image.setImageBitmap(bitmap);

    TextView tview = (TextView) rowView.findViewById(R.id.text_collage);
    tview.setText(data.getName());


    return rowView;
  }
}
