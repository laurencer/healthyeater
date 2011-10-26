package com.rouesnel.comp5047.healthyeater;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.AndroidRuntimeException;
import android.view.*;
import android.widget.*;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

public class TakePicture extends Activity implements SurfaceHolder.Callback {

  private class CameraCallback implements Camera.PictureCallback {
    public void onPictureTaken(byte[] bytes, Camera camera) {
      model.open();
      long id = model.storePicture(bytes);
      Intent i = new Intent(TakePicture.this, ReviewPicture.class);
      i.putExtra(PHOTO_ID, id);
      model.close();

      startActivity(i);
    }
  }

  private class OpenImageOnClick implements View.OnClickListener {
    private final long id;

    public OpenImageOnClick(long id) {
      this.id = id;
    }

    @Override
    public void onClick(View view) {
      Intent intent = new Intent(TakePicture.this, ViewPicture.class);
      intent.putExtra(PHOTO_ID, id);
      startActivity(intent);
    }
  }

  public static String PHOTO_ID = "photoId";

  private static int MINIMUM_PICTURE_EDGE_LENGTH = 1000;

  private SurfaceView previewSurfaceView;
  private SurfaceHolder previewSurface;
  private Camera camera;
  private TextView noFoodText;
  private LinearLayout imageList;
  private boolean cameraPreviewing = false;
  private Model model;
  private List<Model.Picture> pictures;
  private Button viewGallery;

  private View.OnClickListener takePhotoListener = new View.OnClickListener() {
      public void onClick(View view) {
        camera.takePicture(null, null, new CameraCallback());
      }
    };

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.take_picture);

    // Setup control bindings.
    previewSurfaceView = (SurfaceView) findViewById(R.camera.surface);
    previewSurface = previewSurfaceView.getHolder();
    imageList = (LinearLayout) findViewById(R.camera.pictureGallery);
    noFoodText = (TextView) findViewById(R.camera.noFoodText);
    viewGallery = (Button) findViewById(R.camera.viewGallery);

    // Setup the camera surface.
    previewSurface.addCallback(this);
    previewSurface.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    // open the database.
    model = new Model(this);

    previewSurfaceView.setOnClickListener(takePhotoListener);
    viewGallery.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View view) {
        Intent intent = new Intent(TakePicture.this, ViewGallery.class);
        startActivity(intent);
      }
    });
  }

  @Override
  public void onStart() {
    super.onStart();
    loadPictures();
  }

  protected void setDisplayOrientation(Camera camera, int angle) {
    Method downPolymorphic;
    try {
      downPolymorphic = camera.getClass().getMethod("setDisplayOrientation", new Class[]{int.class});
      if (downPolymorphic != null)
        downPolymorphic.invoke(camera, new Object[]{angle});
    } catch (Exception e1) {
    }
  }

  private void loadPictures() {

    model.open();
    pictures = model.getTodaysPictures();
    model.close();

    imageList.removeAllViews();

    // slice the image list
    if (pictures.size() > 0) {
      noFoodText.setVisibility(View.INVISIBLE);
      LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      for (Model.Picture p : pictures) {
        View v = vi.inflate(R.layout.side_image_view, null);
        ImageView image = (ImageView) v.findViewById(R.side_image_view.image);
        image.setImageBitmap(p.getThumbnail());
        TextView time = (TextView) v.findViewById(R.side_image_view.time);
        time.setText(p.getTimeAgo());
        v.setOnClickListener(new OpenImageOnClick(p.getId()));
        imageList.addView(v);
      }
    } else {

      noFoodText.setVisibility(View.VISIBLE);
    }
  }

  public void surfaceCreated(SurfaceHolder surfaceHolder) {
    camera = Camera.open();

    try {
      camera.setPreviewDisplay(previewSurface);
    } catch (IOException ex) {
      throw new AndroidRuntimeException(ex);
    }
  }

  public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int w, int h) {
    if (cameraPreviewing) {
      camera.stopPreview();
    }

    Camera.Parameters parameters = camera.getParameters();
    parameters.setPreviewSize(h, w);

    for (Camera.Size size : parameters.getSupportedPictureSizes()) {
      parameters.setPictureSize(size.width, size.height);
      if (size.width > size.height && size.height >
          MINIMUM_PICTURE_EDGE_LENGTH) {
        break;
      }
    }

    camera.setParameters(parameters);
    setDisplayOrientation(camera, 90);

    camera.startPreview();
    cameraPreviewing = true;
  }

  public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
    camera.stopPreview();
    cameraPreviewing = false;
    camera.release();
    camera = null;
  }
}
