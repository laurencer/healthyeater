package com.rouesnel.comp5047.healthyeater;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.AndroidRuntimeException;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

public class TakePhoto extends Activity implements SurfaceHolder.Callback {

  public static String PHOTO_ID = "photoId";

  private SurfaceHolder previewSurface;
  private Camera camera;

  private boolean cameraPreviewing = false;

  private Model model;

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    // Setup control bindings.
    previewSurface = ((SurfaceView) findViewById(R.camera.surface)).getHolder();
    previewSurface.addCallback(this);
    previewSurface.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    // Open the database.
    model = new Model(this);
    model.Open();

    Button takePhoto = (Button)findViewById(R.camera.takePhoto);
    takePhoto.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        camera.takePicture(null, null, new CameraCallback());
      }
    });
  }

  private class CameraCallback implements Camera.PictureCallback {
    public void onPictureTaken(byte[] bytes, Camera camera) {
      long id = model.storePicture(bytes);
      Intent i = new Intent(TakePhoto.this, ReviewPhoto.class);
      i.putExtra(PHOTO_ID, id);
      model.Close();
      startActivity(i);
    }
  }

  public void surfaceCreated(SurfaceHolder surfaceHolder) {
    camera = Camera.open();
  }

  public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int w, int h) {
    if (cameraPreviewing) {
      camera.stopPreview();
    }

    Camera.Parameters parameters = camera.getParameters();

    /** Find the ideal size for preview according to the device screen size
     * and the available preview sizes available from the camera.
     */
    Camera.Size previewSize = parameters.getSupportedPreviewSizes().get(0);
    for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
      if (size.width < w && size.height < h) {
        previewSize = size;
      } else {
        break;
      }
    }

    parameters.setPreviewSize(previewSize.width, previewSize.height);


    try {
      camera.setPreviewDisplay(previewSurface);
    } catch (IOException ex) {
      throw new AndroidRuntimeException(ex);
    }

    camera.startPreview();
    cameraPreviewing = true;
  }

  public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
    camera.stopPreview();
    cameraPreviewing = false;
    camera.release();
  }
}
