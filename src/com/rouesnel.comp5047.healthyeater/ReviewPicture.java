package com.rouesnel.comp5047.healthyeater;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

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

  private RadioGroup.OnCheckedChangeListener onRatingChange = new
      RadioGroup.OnCheckedChangeListener() {
        public void onCheckedChanged(RadioGroup radioGroup, int buttonId) {
          RadioButton button = (RadioButton)findViewById(buttonId);
          String rating = button.getText().toString();
          picture.setRating(rating);
        }
      };

  private Button.OnClickListener onSaveClick = new Button.OnClickListener() {
    public void onClick(View view) {
      finish();
    }
  };

  private Button.OnClickListener onRetakeClick = new Button.OnClickListener() {
    public void onClick(View view) {
      picture.retake();
      finish();
    }
  };

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.review_picture);

    // Get the views.
    displayView = (ImageView)findViewById(R.photoReview.imageView);
    ratingOptions = (RadioGroup)findViewById(R.photoReview.rating);
    retakePicture = (Button)findViewById(R.photoReview.retake);
    savePicture = (Button)findViewById(R.photoReview.save);

    // Grab picture from the database.
    pictureId = getIntent().getLongExtra(TakePicture.PHOTO_ID, 0);
    model = new Model(this);
    picture = model.getPicture(pictureId);

    // Set up click listeners
    ratingOptions.setOnCheckedChangeListener(onRatingChange);
    retakePicture.setOnClickListener(onRetakeClick);
    savePicture.setOnClickListener(onSaveClick);

    // Initialize the display and controls.
    displayView.setImageBitmap(picture.getBitmap());

  }

}