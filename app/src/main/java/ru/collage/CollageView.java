package ru.collage;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Игорь on 06.01.2016.
 */

interface OnEditModeChange
{
  void onEditModeChange(View view);
}


public class CollageView extends View // ImageView
{
  enum BitmapNumber
  {
    FIRST,
    SECOND,
  }

  enum ViewType
  {
    RECTANGLE_TOP_TO_BOTTOM,
    RECTANGLE_LEFT_TO_RIGHT,
    TRIANGLE_TOP_TO_BOTTOM,
    TRIANGLE_LEFT_TO_RIGHT,
  }

  private ViewType m_view_type;
  private Figure m_figures[] = new Figure[2];

  private boolean m_is_edit_mode;
  private OnEditModeChange m_on_edit_mode_change;


  public void setEditMode(boolean is_edit_mode)
  {
    if (m_on_edit_mode_change != null)
      m_on_edit_mode_change.onEditModeChange(this);

    m_is_edit_mode = is_edit_mode;
  }

  public boolean isEditModeEnabled()
  {
    return m_is_edit_mode;
  }

  public void setOnEditModeChange(OnEditModeChange onEditModeChange)
  {
    m_on_edit_mode_change = onEditModeChange;
  }


  private ScaleGestureDetector mScaleDetector;
  private float mScaleFactor = 1.f;
  /*
  private Bitmap m_original_bitmap_first;
  private Bitmap m_original_bitmap_second;*/

  public CollageView(Context context)
  {
    super(context);
    init();
  }

  public CollageView(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    init();
  }

  public CollageView(Context context, AttributeSet attrs, int defStyleAttr)
  {
    super(context, attrs, defStyleAttr);
    init();
  }

  public CollageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
  {
    super(context, attrs, defStyleAttr, defStyleRes);
    init();
  }

  private void init()
  {
    Log.d("myLogs-size-1", "width: " + getWidth() + " height " + getHeight());
    mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.SimpleOnScaleGestureListener()
    {
      @Override
      public boolean onScale(ScaleGestureDetector detector) {
        mScaleFactor *= detector.getScaleFactor();

        // Don't let the object get too small or too large.
        mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));

        invalidate();
        return true;
      }
    });
    setViewType(ViewType.RECTANGLE_TOP_TO_BOTTOM);
  }

  public void setViewType(ViewType view_type)
  {
    m_view_type = view_type;

    for (int index = 0; index != m_figures.length; ++index)
    {
      boolean isTopFigure = (index == 0);
      switch (view_type)
      {
        case RECTANGLE_TOP_TO_BOTTOM:
          m_figures[index] = new RectangleTopToBottomFigure(m_figures[index], isTopFigure);
          break;

        case RECTANGLE_LEFT_TO_RIGHT:
          m_figures[index] = new RectangleLeftToRightFigure(m_figures[index], isTopFigure);
          break;

        case TRIANGLE_TOP_TO_BOTTOM:
          m_figures[index] = new TriagleTopToBottomFigure(m_figures[index], isTopFigure);
          break;

        case TRIANGLE_LEFT_TO_RIGHT:
          m_figures[index] = new TriagleLeftToRightFigure(m_figures[index], isTopFigure);
          break;
      }
    }
  }

  public void setBitmap(Bitmap originBitmap, BitmapNumber bitmapNumber)
  {
    Log.d("myLogs-setBitmap", "width: " + getWidth() + " height " + getHeight() + " ordinal " + bitmapNumber.ordinal());
    m_figures[bitmapNumber.ordinal()].setOriginalBitmap(originBitmap);
  }


  abstract public class Figure
  {
    private boolean m_is_top_figure;
    private int m_width, m_height;
    private Bitmap m_original_bitmap;

    private Bitmap m_bitmap;
    protected Point m_center;
    private Region m_region;

    private float m_offset_x, m_offset_y;


    public Figure(Figure figure, boolean isTopFigure)
    {
      m_center = new Point();
      m_is_top_figure = isTopFigure;
      if (figure == null)
        return;


      m_is_top_figure = figure.m_is_top_figure;
      m_width = figure.m_width;
      m_height = figure.m_height;

      setOriginalBitmap(figure.m_original_bitmap);
    }

    public Figure(Bitmap originalBitmap, int width, int height, boolean isTopFigure)
    {
      m_center = new Point();
      m_is_top_figure = isTopFigure;

      m_width = width;
      m_height = height;

      setOriginalBitmap(originalBitmap);
    }

    void setOriginalBitmap(Bitmap originalBitmap)
    {
      m_original_bitmap = originalBitmap;

      createBitmap();
    }

    public void onSizeChanged(int w, int h, int oldw, int oldh)
    {
      m_width = w;
      m_height = h;

      createBitmap();
    }

    public void addBitmapOffset(float deltaX, float deltaY)
    {
      m_offset_x = m_offset_x + deltaX / 2;
      m_offset_y = m_offset_y + deltaY / 2;

      //m_offset_x = (m_offset_x < 0) ? 0 : m_offset_x;
      //m_offset_y = (m_offset_y < 0) ? 0 : m_offset_y;

      createBitmap();
    }

    private void createBitmap()
    {
      if (m_original_bitmap == null)
        return;

      if (m_width <= 0 || m_height <= 0)
        return;

      Paint paint = new Paint();
      paint.setAlpha(0);
      paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

      Path path = new Path();
      path.setFillType(Path.FillType.EVEN_ODD);
      computePath(path, m_width, m_height, !m_is_top_figure);
      path.close();

      m_bitmap = Bitmap.createBitmap(m_width, m_height, Bitmap.Config.ARGB_8888);
      //m_bitmap = Bitmap.createScaledBitmap(m_original_bitmap, m_width, m_height, true);

      Canvas canvas = new Canvas(m_bitmap);
      canvas.drawBitmap(m_original_bitmap,
        new Rect(0, 0, m_original_bitmap.getWidth(), m_original_bitmap.getHeight()),
        new Rect((int) m_offset_x, (int) m_offset_y, m_width + (int) m_offset_x, m_height + (int) m_offset_y),
        null
      );
      /*
      canvas.drawBitmap(m_original_bitmap,
        new Rect((int) m_offset_x, (int) m_offset_y, m_original_bitmap.getWidth() - (int) m_offset_x, m_original_bitmap.getHeight() - (int) m_offset_y),
        new Rect(0, 0, m_width, m_height),
        null
        );
        */
      /*
      canvas.drawBitmap(m_original_bitmap,
        new Rect(0, 0, m_original_bitmap.getWidth(), m_original_bitmap.getHeight()),
        new Rect((int) m_offset_x, (int) m_offset_y, m_width, m_height),
        null
      );
      */
      canvas.drawPath(path, paint);

      computeBoundRegion();
      // перерисовка родителя
      invalidate();
    }

    private void computeBoundRegion()
    {
      Path path = new Path();
      path.setFillType(Path.FillType.EVEN_ODD);
      computePath(path, m_width, m_height, m_is_top_figure);
      path.close();

      RectF rectF = new RectF();
      path.computeBounds(rectF, true);

      m_region = new Region();
      m_region.setPath(path, new Region((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));
    }

    public Point getCenter()
    {
      return m_center;
    }

    Bitmap getBitmap()
    {
      return m_bitmap;
    }

    public Region getRegionRect()
    {
      return m_region;
    }

    abstract protected void computePath(Path path, float width, float height, boolean isTopFigure);
  }

  public class TriagleTopToBottomFigure extends Figure
  {
    public TriagleTopToBottomFigure(Figure figure, boolean isTopFigure)
    {
      super(figure, isTopFigure);
    }

    public TriagleTopToBottomFigure(Bitmap originalBitmap, int width, int height, boolean isTopFigure)
    {
      super(originalBitmap, width, height, isTopFigure);
    }

    protected void computePath(Path path, float width, float height, boolean isTopFigure)
    {
      computeCenterPoint(width, height, isTopFigure);
      if (isTopFigure)
      {
        path.moveTo(0, 0);
        path.lineTo(width, 0);
        path.lineTo(0, height);
      }
      else
      {
        path.moveTo(width, 0);
        path.lineTo(width, height);
        path.lineTo(0, height);
      }
    }

    private void computeCenterPoint(float width, float height, boolean isTopFigure)
    {
      if (isTopFigure)
      {
        m_center.x = (int) width - (int) width / 4;
        m_center.y = (int) height / 2;
      } else
      {
        m_center.x = (int) width / 4;
        m_center.y = (int) height / 2;
      }
    }
  }

  public class TriagleLeftToRightFigure extends Figure
  {
    public TriagleLeftToRightFigure(Figure figure, boolean isTopFigure)
    {
      super(figure, isTopFigure);
    }

    public TriagleLeftToRightFigure(Bitmap originalBitmap, int width, int height, boolean isTopFigure)
    {
      super(originalBitmap, width, height, isTopFigure);
    }

    protected void computePath(Path path, float width, float height, boolean isTopFigure)
    {
      computeCenterPoint(width, height, isTopFigure);
      if (isTopFigure)
      {
        path.moveTo(width, height);
        path.lineTo(0, 0);
        path.lineTo(width, 0);
      }
      else
      {
        path.moveTo(width, height);
        path.lineTo(0, height);
        path.lineTo(0, 0);
      }
    }

    private void computeCenterPoint(float width, float height, boolean isTopFigure)
    {
      if (isTopFigure)
      {
        m_center.x = (int) width / 4;
        m_center.y = (int) height / 2;
      }
      else
      {
        m_center.x = (int) width - (int) width / 4;
        m_center.y = (int) height / 2;
      }
    }
  }

  public class RectangleTopToBottomFigure extends Figure
  {
    public RectangleTopToBottomFigure(Figure figure, boolean isTopFigure)
    {
      super(figure, isTopFigure);
    }

    public RectangleTopToBottomFigure(Bitmap originalBitmap, int width, int height, boolean isTopFigure)
    {
      super(originalBitmap, width, height, isTopFigure);
    }

    protected void computePath(Path path, float width, float height, boolean isTopFigure)
    {
      computeCenterPoint(width, height, isTopFigure);
      if (isTopFigure)
      {
        path.moveTo(0, 0);
        path.lineTo(width, 0);
        path.lineTo(width, height / 2);
        path.lineTo(0, height / 2);
      }
      else
      {
        path.moveTo(width, height);
        path.lineTo(0, height);
        path.lineTo(0, height / 2);
        path.lineTo(width, height / 2);
      }
    }

    private void computeCenterPoint(float width, float height, boolean isTopFigure)
    {
      if (isTopFigure)
      {
        m_center.x = (int) width / 2;
        m_center.y = (int) height - (int) height / 4;
      }
      else
      {
        m_center.x = (int) width / 2;
        m_center.y = (int) height / 4;
      }
    }
  }

  public class RectangleLeftToRightFigure extends Figure
  {
    public RectangleLeftToRightFigure(Figure figure, boolean isTopFigure)
    {
      super(figure, isTopFigure);
    }

    public RectangleLeftToRightFigure(Bitmap originalBitmap, int width, int height, boolean isTopFigure)
    {
      super(originalBitmap, width, height, isTopFigure);
    }

    protected void computePath(Path path, float width, float height, boolean isTopFigure)
    {
      computeCenterPoint(width, height, isTopFigure);
      if (isTopFigure)
      {
        path.moveTo(0, 0);
        path.lineTo(width / 2, 0);
        path.lineTo(width / 2, height);
        path.lineTo(0, height);
      }
      else
      {
        path.moveTo(width, height);
        path.lineTo(width / 2, height);
        path.lineTo(width / 2, 0);
        path.lineTo(width, 0);
      }
    }

    private void computeCenterPoint(float width, float height, boolean isTopFigure)
    {
      if (isTopFigure)
      {
        m_center.x = (int) width - (int) width / 4;
        m_center.y = (int) height / 2;
      }
      else
      {
        m_center.x = (int) width / 4;
        m_center.y = (int) height / 2;
      }
    }
  }



  protected void onSizeChanged(int w, int h, int oldw, int oldh)
  {
    super.onSizeChanged(w, h, oldw, oldh);

    for (int index = 0; index != m_figures.length; ++index)
      if (m_figures[index] != null)
        m_figures[index].onSizeChanged(w, h, oldw, oldh);

    Log.d("myLgos-size-2", "m_figures.length: " + m_figures.length + "width: " + getWidth() + " height " + getHeight());
  }


  private float mLastTouchX;
  private float mLastTouchY;
  @Override
  public boolean onTouchEvent(MotionEvent event)
  {
    // Let the ScaleGestureDetector inspect all events.
    mScaleDetector.onTouchEvent(event);

    for (int index = 0; index != m_figures.length; ++index)
    {
      if (m_figures[index].getRegionRect() == null)
        continue;

      if (!m_figures[index].getRegionRect().contains((int) event.getX(), (int) event.getY()))
        continue;

      switch (event.getAction())// & MotionEvent.ACTION_MASK)
      {
        case MotionEvent.ACTION_MOVE:
        {
          Log.d("myLogs-Motion", "enter 1");
          if (isEditModeEnabled())
          {
            float deltaX = event.getX() - mLastTouchX;
            float deltaY = event.getY() - mLastTouchY;

            m_figures[index].addBitmapOffset(deltaX, deltaY);
          }
          mLastTouchX = event.getX();
          mLastTouchY = event.getY();
          return true;
        }
        case MotionEvent.ACTION_DOWN:
        {
          Log.d("myLogs-Motion", "enter 2");
          if (!isEditModeEnabled())
          {
            final int indexSelect = index;

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
            dialogBuilder.setMessage("Добавить фотографию");
            dialogBuilder.setPositiveButton("Камера", new DialogInterface.OnClickListener()
            {
              @Override
              public void onClick(DialogInterface dialogInterface, int i)
              {
                final boolean isTopFigure = (indexSelect == 0);
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                ((Activity) getContext()).startActivityForResult(Intent.createChooser(intent, ""), indexSelect);

                Log.d("myLogs", "clicl tag_nym: " + indexSelect);
              }
            });


            dialogBuilder.setNegativeButton("Галерея", new DialogInterface.OnClickListener()
            {
              @Override
              public void onClick(DialogInterface dialogInterface, int i)
              {
                final boolean isTopFigure = (indexSelect == 0);
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                ((Activity) getContext()).startActivityForResult(Intent.createChooser(intent, ""), indexSelect);

                Log.d("myLogs", "clicl tag_nym: " + indexSelect);
              }
            });


            dialogBuilder.setNeutralButton("Редактировать", new DialogInterface.OnClickListener()
            {
              @Override
              public void onClick(DialogInterface dialogInterface, int i)
              {
                setEditMode(true);
                Log.d("myLogs", "clicl tag_nym: " + indexSelect);
              }
            });

            dialogBuilder.create().show();
          }
          mLastTouchX = event.getX();
          mLastTouchY = event.getY();
          return true;
        }
        default:

          Log.d("myLogs-Motion", "enter 3");
          mLastTouchX = event.getX();
          mLastTouchY = event.getY();
          break;
      }
    }
    return super.onTouchEvent(event);
  }

  @Override
  protected void onDraw(Canvas canvas)
  {
    // TODO Auto-generated method stub
    super.onDraw(canvas);

    Paint paint = new Paint();
    for (int index = 0; index != m_figures.length; ++index)
    {
      if (m_figures[index].getBitmap() == null)
        continue;
      //paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));
      paint.setAlpha(255);
      paint.setAntiAlias(true);

      canvas.drawBitmap(m_figures[index].getBitmap(), 0, 0, paint);
    }

    if (!isEditModeEnabled())
    {
      paint.setARGB(140, 200, 200, 200);
      canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
    }

    for (int index = 0; index != m_figures.length; ++index)
    {
      if (m_figures[index].getBitmap() == null)
        continue;

      if (!isEditModeEnabled())
      {
        Point point = m_figures[index].getCenter();
        final Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.plus);
        paint.setAlpha(125);
        canvas.drawBitmap(bitmap, point.x - bitmap.getWidth() / 2, point.y - bitmap.getHeight() / 2, paint);
        //canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), rectF, paint);
      }
    }
  }
}
