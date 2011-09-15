package com.rouesnel.comp5047.healthyeater;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Laurence Rouesnel
 * Date: 15/09/11
 * Time: 9:20 AM
 */
public class ViewGallery extends Activity {

  private class DayViewPager extends PagerAdapter {

    private class PictureAdapter extends ArrayAdapter<Model.Picture> {

       private Model.Picture[] pictures;

       public PictureAdapter(Context context, int textViewResourceId, Model.Picture[] objects) {
         super(context, textViewResourceId, objects);
         this.pictures = objects;
       }

       @Override
       public View getView(int position, View convertView, ViewGroup parent) {
         View v = convertView;
         if (v == null) {
           LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
           v = vi.inflate(R.layout.side_image_view, null);
         }
         if (position < pictures.length) {
           Model.Picture p = pictures[position];
           ImageView image = (ImageView) v.findViewById(R.side_image_view.image);
           image.setImageBitmap(p.getBitmap());
           TextView time = (TextView) v.findViewById(R.side_image_view.time);
           time.setText("");
         }

         return v;
       }

     }


    private final Model model;
    private final Context context;
    private List<Model.Day> days;

    public DayViewPager(Context context) {
      this.context = context;
      this.model = new Model(context);
      model.open();
      this.days = model.getDays();
      model.close();
    }

    @Override
    public int getCount() {
      return days.size();
    }

    @Override
    public void startUpdate(View view) {

    }

    @Override
    public Object instantiateItem(View collection, int i) {
      LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

      View view = vi.inflate(R.layout.day_view, null);


      if (i < getCount()) {
        Model.Day day = days.get(i);

        TextView title = (TextView) view.findViewById(R.dayView.title);
        title.setText(day.getDayString());

        GridView images = (GridView) view.findViewById(R.dayView.images);
        images.setAdapter(new PictureAdapter(context, 0,
            day.getPictures().toArray(new Model.Picture[0])));
      }

      ((ViewPager) collection).addView(view, i);
      return view;

    }

    @Override
    public void destroyItem(View collection, int i, Object o) {
      ((ViewPager) collection).removeView((View) o);
    }

    @Override
    public void finishUpdate(View view) {

    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
      return view == (View)o;
    }

    @Override
    public Parcelable saveState() {
      return null;
    }

    @Override
    public void restoreState(Parcelable parcelable, ClassLoader classLoader) {
      
    }
  }

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.view_gallery);

    ViewPager pager = (ViewPager)findViewById(R.viewGallery.dayViews);
    pager.setAdapter(new DayViewPager(this));
  }
}