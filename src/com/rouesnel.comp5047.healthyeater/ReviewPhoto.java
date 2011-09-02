package com.rouesnel.comp5047.healthyeater;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by IntelliJ IDEA.
 * User: Laurence Rouesnel
 * Date: 1/09/11
 * Time: 3:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReviewPhoto extends Activity {

  private long pictureId;
  private Model model;

  private byte[] pictureData;

  private Button redButton;
  private Button yellowButton;
  private Button greenButton;
  private ImageView displayView;

  private class RatingClickHandler implements View.OnClickListener {

    private String rating;

    public RatingClickHandler(String rating) {
      this.rating = rating;
    }

    public void onClick(View view) {
      model.ratePicture(pictureId, "yellow");
      finish();
    }
  }

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.photo_review);


    // Initialize the controls.
    redButton = (Button)findViewById(R.photoReview.redButton);
    yellowButton = (Button)findViewById(R.photoReview.yellowButton);
    greenButton = (Button)findViewById(R.photoReview.greenButton);
    displayView = (ImageView)findViewById(R.photoReview.imageView);

    // Grab picture from the database.

    pictureId = getIntent().getLongExtra(TakePhoto.PHOTO_ID, 0);
    model = new Model(this);
    model.Open();
    pictureData = model.getPicture(pictureId);

    // Initialize the display and controls.
    displayView.setImageBitmap(BitmapFactory.decodeByteArray(pictureData, 0,
        pictureData.length));

    redButton.setOnClickListener(new RatingClickHandler("red"));

    yellowButton.setOnClickListener(new RatingClickHandler("yellow"));

    greenButton.setOnClickListener(new RatingClickHandler("green"));
  }


}