package ru.collage;

import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TabHost;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements Events.OnUpdateAdapterData
{
    private AsyncRequestManager m_asyncRequestmanager;
    private BaseAdapter m_collage_adapter, m_collage_by_cat_adapter, m_category_adapter;

    private TabHost m_tabHost;

    CollageView m_collageView;


    private AdapterView.OnItemClickListener m_on_collage_click = new AdapterView.OnItemClickListener()
    {
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
        {
            m_tabHost.setCurrentTab(4);

            CollageManager.Collage collage = (CollageManager.Collage) view.getTag();

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

            m_collageView.setBitmap(collage.getOriginBitmap(), CollageView.BitmapNumber.FIRST);
            m_collageView.setBitmap(collage.getOriginBitmap(), CollageView.BitmapNumber.SECOND);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK)
            return;
/*
        if (requestCode == REQUEST_CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
            File destination = new File(Environment.getExternalStorageDirectory(),
                                         System.currentTimeMillis() + ".jpg");
            FileOutputStream fo;
            try {
                destination.createNewFile();
                fo = new FileOutputStream(destination);
                fo.write(bytes.toByteArray());
                fo.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ivImage.setImageBitmap(thumbnail);
        } else if (requestCode == SELECT_FILE) {*/
            Uri selectedImageUri = data.getData();

            CursorLoader cursorLoader = new CursorLoader(this, selectedImageUri, new String[] { MediaStore.MediaColumns.DATA }, null, null,
                                                          null);
            Cursor cursor = cursorLoader.loadInBackground();
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            cursor.moveToFirst();
            String selectedImagePath = cursor.getString(column_index);

                    Bitmap bm;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(selectedImagePath, options);
            final int REQUIRED_SIZE = 200;
            int scale = 1;
            while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                     && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                scale *= 2;
            options.inSampleSize = scale;
            options.inJustDecodeBounds = false;
            bm = BitmapFactory.decodeFile(selectedImagePath, options);

            m_collageView.setBitmap(bm, (requestCode == 0) ? CollageView.BitmapNumber.FIRST : CollageView.BitmapNumber.SECOND);
        //}


        Log.d("myLogs-onResult", "result.. data != null " + (data != null));
        if (data == null)
            return;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
       /* prepareImages();
        try
        {
            String mProjections[] = new String[]
                                      {
                                        MediaStore.Images.ImageColumns._ID,
                                      };

            String selectionClause = new String(MediaStore.Images.ImageColumns.TITLE + " = " + "?");
            String selectionArgs[] = new String[]{"IMG_20160103_162052"};

            String imageId = null;
            Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, mProjections, selectionClause, selectionArgs, null);
            if (cursor != null && cursor.getCount() > 0)
            {
                cursor.moveToFirst();
                imageId = cursor.getString(0);
            }
            Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(getContentResolver(), Integer.valueOf(imageId), MediaStore.Images.Thumbnails.MINI_KIND, null);


            Uri uri = MediaStore.Images.Thumbnails.getContentUri("IMG_20160103_162052");
            Log.d("myLogs", "!!!: ");
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            Log.d("myLogs", "!!!: " + uri.toString());
            Log.d("myLogs", "is not null: " + (bitmap != null));
            Log.d("myLogs", bitmap.toString());
        }
        catch (IOException e)
        {

        }*/
       // MediaStore.Images.Thumbnails.
        Application.init(getContentResolver(), this, new Pair<Integer, Integer>(200, 200));
        m_asyncRequestmanager = new AsyncRequestManager(this);
        m_asyncRequestmanager.get_collages_new();
        m_asyncRequestmanager.get_categories();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collages_main);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
*/
        m_tabHost = (TabHost) findViewById(R.id.tabHost);
        m_tabHost.setup();

        TabHost.TabSpec spec;

        spec = m_tabHost.newTabSpec("tag1");
        spec.setIndicator("Новинки");
        spec.setContent(R.id.tab1_content);
        m_tabHost.addTab(spec);

        spec = m_tabHost.newTabSpec("tag2");
        spec.setIndicator("Писатель");
        spec.setContent(R.id.tab2_content);
        m_tabHost.addTab(spec);

        spec = m_tabHost.newTabSpec("tag3");
        spec.setIndicator("Все");
        spec.setContent(R.id.tab3_content);
        m_tabHost.addTab(spec);

        spec = m_tabHost.newTabSpec("tag4");
        spec.setIndicator("Добавить");
        spec.setContent(R.id.tab4_content);
        m_tabHost.addTab(spec);

        spec = m_tabHost.newTabSpec("tag5");
        spec.setIndicator("Добавить");
        spec.setContent(R.id.tab5_content);
        m_tabHost.addTab(spec);


        m_tabHost.getTabWidget().getChildTabViewAt(1).setVisibility(View.GONE);
        m_tabHost.getTabWidget().getChildTabViewAt(4).setVisibility(View.GONE);

        m_tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener()
        {
            public void onTabChanged(String tabId)
            {
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
            }
        });

        /*TextView textView = new TextView(tabHost.getContext());//LayoutInflater.from(tabHost.getContext()).inflate();
        textView.setText("SADASD");

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("tb0").setIndicator(textView);*/

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
     * Events
     */
    public void onUpdateAdapterData()
    {
        Log.d("myLogs", "onUpdateAdapterData: ");
        m_collage_adapter.notifyDataSetChanged();
        m_collage_by_cat_adapter.notifyDataSetChanged();
        m_category_adapter.notifyDataSetChanged();
    }

    public void onChangeViewType(View view)
    {
        switch (view.getId())
        {
            case R.id.rectangle_top_to_bottom:
                m_collageView.setViewType(CollageView.ViewType.RECTANGLE_TOP_TO_BOTTOM);
                break;
            case R.id.rectangle_left_to_right:
                m_collageView.setViewType(CollageView.ViewType.RECTANGLE_LEFT_TO_RIGHT);
                break;
            case R.id.trinagle_top_to_bottom:
                m_collageView.setViewType(CollageView.ViewType.TRIANGLE_TOP_TO_BOTTOM);
                break;
            case R.id.trinagle_left_to_right:
                m_collageView.setViewType(CollageView.ViewType.TRIANGLE_LEFT_TO_RIGHT);
                break;
        }
    }


    public void completeEdit(View view)
    {
        m_collageView.setEditMode(false);
    }
}
