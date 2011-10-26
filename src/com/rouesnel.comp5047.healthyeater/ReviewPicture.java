package com.rouesnel.comp5047.healthyeater;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * Created by IntelliJ IDEA.
 * User: Laurence Rouesnel
 * Date: 1/09/11
 * Time: 3:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReviewPicture extends Activity {

    private long pictureId;
    private Model model;

    private RadioGroup ratingOptions;
    private ImageView displayView;
    private Button retakePicture;
    private Button savePicture;

    private Model.Picture picture;

    /**
     * Handles the selection event for the radio buttons. These are used to rate the picture or delete it (so it can be
     * retaken). This ends up in the current activity being closed - so that it returns to the TakePicture activity.
     */
    private RadioGroup.OnCheckedChangeListener onRatingChange = new
            RadioGroup.OnCheckedChangeListener() {
                public void onCheckedChanged(RadioGroup radioGroup, int buttonId) {
                    if (buttonId == R.photoReview.retake) {
                        picture.retake();
                    } else {
                        RadioButton button = (RadioButton) findViewById(buttonId);
                        String rating = button.getText().toString();
                        picture.setRating(rating);
                    }
                    // Close the current activity to return to the TakePicture activity.
                    finish();
                }
            };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_picture);

        // Get the views.
        displayView = (ImageView) findViewById(R.photoReview.imageView);
        ratingOptions = (RadioGroup) findViewById(R.photoReview.rating);

        // Grab picture from the database.
        pictureId = getIntent().getLongExtra(TakePicture.PHOTO_ID, 0);
        model = new Model(this);
        picture = model.getPicture(pictureId);

        // Set up click listeners
        ratingOptions.setOnCheckedChangeListener(onRatingChange);

        // Initialize the display and controls.
        displayView.setImageBitmap(picture.getThumbnail());

    }

}