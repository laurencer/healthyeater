package com.rouesnel.comp5047.healthyeater;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Laurence Rouesnel
 * Date: 15/09/11
 * Time: 9:20 AM
 */
public class ViewGallery extends ListActivity {

    private class PictureClickListener implements View.OnClickListener {

        long id;

        public PictureClickListener(long id) {
            this.id = id;
        }

        @Override
        public void onClick(View view) {
            Intent i = new Intent(ViewGallery.this, ViewPicture.class);
            i.putExtra(TakePicture.PHOTO_ID, id);

            startActivity(i);
        }
    }

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
                image.setImageBitmap(p.getThumbnail());
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
        private List<DataSetObserver> observers = new ArrayList<DataSetObserver>();

        public DayAdapter(Context context) {
            this.context = context;
            this.model = new Model(context);
            model.open();
            this.days = model.getDays();
            model.close();
        }

        public void onModelUpdate() {
            for (DataSetObserver observer : observers) {
                observer.onChanged();
            }
        }

        @Override
        public void registerDataSetObserver(DataSetObserver dataSetObserver) {
            observers.add(dataSetObserver);
        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
            if (observers.contains(dataSetObserver)) {
                observers.remove(dataSetObserver);
            }
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

                TableLayout images = (TableLayout) view.findViewById(R.dayView.images);


                PictureAdapter adapter = new PictureAdapter(context, 0,
                        day.getPictures().toArray(new Model.Picture[0]));

                // remove all existing pictures.
                images.removeAllViews();

                // add each picture manually in the table layout.
                int columnNumber = 0;
                TableRow row = new TableRow(context);
                boolean rowAdded = false;

                for (int j = 0; j < adapter.getCount(); ++j) {
                    View v = adapter.getView(j, null, images);
                    v.setOnClickListener(new PictureClickListener(adapter.getItem(j).getId()));

                    // this section handles the logic dealing with creating tablerows with only two columns.

                    row.addView(v);
                    columnNumber++;
                    rowAdded = false;

                    if (columnNumber > 1) {
                        images.addView(row);
                        rowAdded = true;
                        row = new TableRow(context);
                        columnNumber = 0;
                    }

                }

                if (rowAdded == false) {
                    images.addView(row);
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
            // prevents each item from being selectable/clickable (which is what we want).
            return false;
        }

        @Override
        public boolean isEnabled(int i) {
            // prevents each item from being selectable/clickable (which is what we want).
            return false;
        }
    }

    DayAdapter adapter;

    @Override
    public void onStart() {
        super.onStart();
        adapter.onModelUpdate();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_gallery);

        adapter = new DayAdapter(this);
        setListAdapter(adapter);

    }
}