package com.rouesnel.comp5047.healthyeater;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Laurence Rouesnel
 * Date: 15/09/11
 * Time: 9:20 AM
 */
public class ViewGallery extends ListActivity {

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

    private class DayAdapter implements ListAdapter {

        private final Model model;
        private final Context context;
        private List<Model.Day> days;

        public DayAdapter(Context context) {
            this.context = context;
            this.model = new Model(context);
            model.open();
            this.days = model.getDays();
            model.close();
        }

        @Override
        public void registerDataSetObserver(DataSetObserver dataSetObserver) {
        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
        }

        @Override
        public int getItemViewType(int i) {
            return IGNORE_ITEM_VIEW_TYPE;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public int getCount() {
            return days.size();
        }

        @Override
        public Object getItem(int i) {
            return days.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (view == null) {
                view = vi.inflate(R.layout.day_view, null);
            }

            if (i < getCount()) {
                Model.Day day = days.get(i);

                TextView title = (TextView) view.findViewById(R.dayView.title);
                title.setText(day.getDayString());

                LinearLayout images = (LinearLayout) view.findViewById(R.dayView.images);

                // if we're relaying out a view we need to get rid of all existing images (otherwise we'll just be
                // appending additional images to the end and doubling up).
                images.removeAllViews();
                
                List<Model.Picture> pictures = day.getPictures();

                // this lays out all of the images in a single day.
                for (int j = 0; j < pictures.size(); ++j) {

                    // create a new view for the image.
                    View v = vi.inflate(R.layout.side_image_view, null);
                    
                    // load the actual picture and bind all the ui elements to it.
                    Model.Picture p = pictures.get(j);
                    ImageView image = (ImageView) v.findViewById(R.side_image_view.image);
                    image.setImageBitmap(p.getBitmap());
                    TextView time = (TextView) v.findViewById(R.side_image_view.time);
                    time.setText("");

                    // add the completed view to the linearlayout.
                    images.addView(v);
                }
            }

            return view;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int i) {
            return false;
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_gallery);

        setListAdapter(new DayAdapter(this));

    }
}