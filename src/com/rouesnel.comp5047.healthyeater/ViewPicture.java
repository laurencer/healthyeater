package com.rouesnel.comp5047.healthyeater;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Laurence Rouesnel
 * Date: 8/09/11
 * Time: 9:07 AM
 * To change this template use File | Settings | File Templates.
 */
public class ViewPicture extends Activity {

  private Model.Picture picture;
  private Model model;

  private ImageView displayView;
  private TextView ratingView;
  private TextView ratingLabelView;
  private TextView timeView;
  private Button deleteButton;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.view_picture);

    // Get views
    displayView = (ImageView) findViewById(R.viewPicture.imageView);
    ratingView = (TextView) findViewById(R.viewPicture.rating);
    ratingLabelView = (TextView) findViewById(R.viewPicture.ratingLabel);
    timeView = (TextView) findViewById(R.viewPicture.time);
    deleteButton = (Button) findViewById(R.viewPicture.delete);


    // Grab picture from the database.
    long pictureId = getIntent().getLongExtra(TakePicture.PHOTO_ID, 0);
    model = new Model(this);
    picture = model.getPicture(pictureId);

    Date date = picture.getDateTaken();
    String time = DateUtils.formatElapsedTime((new Date().getTime() - date
        .getTime())/1000);
    timeView.setText(time + " ago");

    String rating = picture.getRating();
    if (rating != null && !rating.isEmpty()) {
      ratingView.setText(rating);
      ratingLabelView.setVisibility(View.VISIBLE);
    }

    displayView.setImageBitmap(picture.getBitmap());

    deleteButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        picture.delete();
        finish();
      }
    });
  }
}