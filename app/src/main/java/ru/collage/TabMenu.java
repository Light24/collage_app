package ru.collage;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TabHost;
import android.widget.TextView;

/**
 * Created by Игорь on 12.01.2016.
 */
public class TabMenu extends TabHost implements TabHost.OnTabChangeListener
{
  public TabMenu(Context context)
  {
    this(context, null);
  }

  public TabMenu(Context context, AttributeSet attrs)
  {
    this(context, attrs, 0);
  }

  public TabMenu(Context context, AttributeSet attrs, int defStyleAttr)
  {
    this(context, attrs, defStyleAttr, 0);
  }

  public TabMenu(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
  {
    super(context, attrs, defStyleAttr, defStyleRes);
    initTabs();
  }

  private void addTab(String tagName, String textName, int viewId)
  {
    TabHost.TabSpec spec;
    spec = newTabSpec(tagName);
    spec.setIndicator(textName);
    spec.setContent(R.id.tab1_content);
    addTab(spec);
  }


  private void initTabs()
  {
.   this.setup();

    addTab("tag1", "Новинки", R.id.tab1_content);
    addTab("tag2", "Писатель", R.id.tab2_content);
    addTab("tag3", "Все", R.id.tab3_content);
    addTab("tag4", "Добавить", R.id.tab4_content);
    addTab("tag5", "Добавить", R.id.tab5_content);

    getTabWidget().getChildTabViewAt(1).setVisibility(View.GONE);
    getTabWidget().getChildTabViewAt(4).setVisibility(View.GONE);

    GridView gridView;
    m_collage_adapter = new CollageListAdapter(this);
    gridView = (GridView) findViewById(R.id.collage_list_view);
    gridView.setAdapter(m_collage_adapter);
    gridView.setOnItemClickListener(m_on_collage_click);

    m_collage_by_cat_adapter = new CollageListAdapter(this);
    gridView = (GridView) findViewById(R.id.collage__by_cat_list_view);
    gridView.setAdapter(m_collage_by_cat_adapter);
    gridView.setOnItemClickListener(m_on_collage_click);

    m_category_adapter = new CategoriesListAdapter(this);
    gridView = (GridView) findViewById(R.id.category_list_view);
    gridView.setAdapter(m_category_adapter);
    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
    {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
      {
        CategoriesManager.Category category = CategoriesManager.instance().get(position);
        if (category == null)
          return;

        ((CollageListAdapter) m_collage_by_cat_adapter).setCategory(category.getId());

        m_tabHost.setCurrentTab(1);
        m_tabHost.getTabWidget().getChildTabViewAt(1).setVisibility(View.VISIBLE);

        onUpdateAdapterData();
      }
    });

    setOnTabChangedListener(this);
  }

  @Override
  public void onTabChanged(String s)
  {
    /*
    Log.d("myLogs", "getCurrentTabTag" + m_tabHost.getCurrentTabTag() + " tabId: " + tabId);

    if (tabId != "tag2")
    {
      m_tabHost.getTabWidget().getChildTabViewAt(1).setVisibility(View.GONE);
    }

    if (tabId == "tag4")
    {



      m_tabHost.setCurrentTab(4);

      findViewById(R.id.collage_edit_image);


      //(new CollageView.TriagleFigure(), m_imageView_first, m_imageView_second, collage.getOriginBitmap(), collage.getOriginBitmap());

      m_collageView = (CollageView) findViewById(R.id.collage_edit_image);
      if (android.os.Build.VERSION.SDK_INT >= 11)
        m_collageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
      m_collageView.setOnEditModeChange(new OnEditModeChange()
      {
        @Override
        public void onEditModeChange(View view)
        {
          TextView text_view = (TextView) findViewById(R.id.compleateEdit);
          boolean is_edit_mode_enabled = ((CollageView) view).isEditModeEnabled();
          text_view.setVisibility((is_edit_mode_enabled == true) ? View.GONE : View.VISIBLE);
        }
      });

      m_collageView.setBitmap(null, CollageView.BitmapNumber.FIRST);
      m_collageView.setBitmap(null, CollageView.BitmapNumber.SECOND);

    }
  }*/
  }
}
