package com.rouesnel.comp5047.healthyeater;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
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

    /**
     * Handles the click events that are generated from the thumbnail images and starts ViewPicture activity for
     * the relevant picture.
     */
    private class PictureClickListener implements View.OnClickListener {

        /**
         * The id of the picture that should be opened.
         */
        long id;

        /**
         * Default constructor.
         *
         * @param id the id of the picture this listener is associated with.
         */
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

    /**
     * An adapter that converts a list of Model.Picture instances into views that display the pictures as thumbnails.
     * <p/>
     * This utilizes the SideImageView layout to generate the view.
     */
    private class PictureAdapter extends ArrayAdapter<Model.Picture> {

        /**
         * The pictures that are associated with the view - these will be displayed.
         */
        private Model.Picture[] pictures;

        /**
         * Default constructor.
         *
         * @param context
         * @param textViewResourceId
         * @param objects            the pictures to be displayed/adapted.
         */
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

    /**
     * Adapts a Model.Day object to a set of list rows. This is used to display each Day (and associated pictures)
     * in a list.
     */
    private class DayAdapter implements ListAdapter {

        private final Model model;
        private final Context context;
        private List<Model.Day> days;
        private List<DataSetObserver> observers = new ArrayList<DataSetObserver>();

        /**
         * Default constructor.
         *
         * @param context
         */
        public DayAdapter(Context context) {
            this.context = context;
            this.model = new Model(context);

            // Load the list of days from the database.
            model.open();
            this.days = model.getDays();
            model.close();
        }

        /**
         * Notifies the adapter that the underlying dataset may have changed (ie. the set of days and pictures may have
         * changed).
         * <p/>
         * This is called whenever the activity is started because changes can only occur in activities launched from
         * this one (since no changes occur within this activity).
         */
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

        /**
         * Generates the view that corresponds to a day associated with the adapter.
         *
         * @param i         the position of the day in the set to generate.
         * @param view      an existing view that may be reused.
         * @param viewGroup the parent that will hold the generated view.
         * @return a view that displays the relevant day.
         */
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // Generate the main layout that holds the specific day being generated.
            if (view == null) {
                view = vi.inflate(R.layout.day_view, null);
            }

            // Check that the day actually exists in the set.
            if (i < getCount()) {
                Model.Day day = days.get(i);

                // Set the title of the day to the date.
                TextView title = (TextView) view.findViewById(R.dayView.title);
                title.setText(day.getDayString());


                // Get the table which will hold all the pictures for the day.
                TableLayout images = (TableLayout) view.findViewById(R.dayView.images);

                // Generate an adapter which will generate the relevant thumbnails for the day.
                PictureAdapter adapter = new PictureAdapter(context, 0,
                        day.getPictures().toArray(new Model.Picture[0]));

                // Remove all existing pictures from the view.
                images.removeAllViews();

                // Add each picture manually in the table layout.
                int columnNumber = 0;
                TableRow row = new TableRow(context);
                boolean rowAdded = false;

                // Iterate through each picture for the day.
                for (int j = 0; j < adapter.getCount(); ++j) {
                    // Generate the thumbnail using the adapter.
                    View v = adapter.getView(j, null, images);

                    // Set a click listener - so that the ViewPicture activity is opened when the thumbnail is clicked.
                    v.setOnClickListener(new PictureClickListener(adapter.getItem(j).getId()));

                    /* This section handles the logic dealing with creating tablerows with only two columns.
                     *
                     * This basically works by maintaining a row and counting the number of images in the row. After a
                     * certain number (in this case - when 2 pictures are added) it will add the row to the table and
                     * generate a new row and begin.
                    */
                    row.addView(v);
                    columnNumber++;
                    rowAdded = false;

                    // Add the row to the table if it is now full.
                    if (columnNumber > 1) {
                        images.addView(row);
                        rowAdded = true;
                        row = new TableRow(context);
                        columnNumber = 0;
                    }

                }

                // Add the last row if it hasn't been added yet.
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

    /**
     * This is used to make sure the dataset is always up to date - this activity can launch the ViewPicture activity
     * which can modify/delete an image in the dataset. Hence when this activity is re-entered - we have to check or
     * update the dataset being displayed (otherwise the image won't appear to have been deleted).
     */
    @Override
    public void onStart() {
        super.onStart();
        adapter.onModelUpdate();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_gallery);

        // Set up the adapter for the list.
        adapter = new DayAdapter(this);
        setListAdapter(adapter);

    }
}