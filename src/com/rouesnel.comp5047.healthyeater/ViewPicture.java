package com.rouesnel.comp5047.healthyeater;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.TimeFormatException;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
  private TextView timeView;
  private Button deleteButton;
  private Button shareButton;
  

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.view_picture);

    // Get views
    displayView = (ImageView) findViewById(R.viewPicture.imageView);
    ratingView = (TextView) findViewById(R.viewPicture.rating);
    timeView = (TextView) findViewById(R.viewPicture.time);
    deleteButton = (Button) findViewById(R.viewPicture.delete);
    shareButton = (Button) findViewById(R.viewPicture.share);


    // Grab picture from the database.
    long pictureId = getIntent().getLongExtra(TakePicture.PHOTO_ID, 0);
    model = new Model(this);
    picture = model.getPicture(pictureId);

    Date date = picture.getDateTaken();
    CharSequence time = DateUtils.formatSameDayTime(picture.getDateTaken().getTime
        (), new Date().getTime(), java.text.DateFormat.MEDIUM,
        java.text.DateFormat.SHORT);
    timeView.setText(Html.fromHtml("Photo taken: <b>" + time + "</b>"));

    String rating = picture.getRating();
    if (rating != null && !rating.isEmpty()) {
      ratingView.setText(Html.fromHtml("You were eating because: <b>" +
          rating + "</b>"));
    }

    displayView.setImageBitmap(picture.getBitmap());

    shareButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        // set the photo data.
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(picture.getFile()));

        startActivity(Intent.createChooser(intent, "Share photo with"));
      }
    });

    deleteButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        picture.delete();
        finish();
      }
    });
  }
}