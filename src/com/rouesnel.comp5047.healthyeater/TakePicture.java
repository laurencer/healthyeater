package com.rouesnel.comp5047.healthyeater;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.AndroidRuntimeException;
import android.view.*;
import android.widget.*;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

public class TakePicture extends Activity implements SurfaceHolder.Callback {

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
      }

      return v;
    }

  }

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

  public static String PHOTO_ID = "photoId";

  private static int MINIMUM_PICTURE_EDGE_LENGTH = 1000;

  private SurfaceHolder previewSurface;
  private Camera camera;
  private ImageView primaryImage;
  private long primaryImageId = -1;
  private GridView imageList;
  private boolean cameraPreviewing = false;
  private Model model;
  private List<Model.Picture> pictures;

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.take_picture);

    // Setup control bindings.
    previewSurface = ((SurfaceView) findViewById(R.camera.surface)).getHolder();
    primaryImage = (ImageView) findViewById(R.camera.primaryImage);
    imageList = (GridView) findViewById(R.camera.previousPhotos);

    // Setup the camera surface.
    previewSurface.addCallback(this);
    previewSurface.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    // open the database.
    model = new Model(this);

    Button takePhoto = (Button) findViewById(R.camera.takePhoto);
    takePhoto.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        camera.takePicture(null, null, new CameraCallback());
      }
    });

    primaryImage.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        if (primaryImageId != -1) {
          Intent intent = new Intent(TakePicture.this, ViewPicture.class);
          intent.putExtra(PHOTO_ID, primaryImageId);
          startActivity(intent);
        }
      }
    });

    imageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Model.Picture picture = (Model.Picture)adapterView.getItemAtPosition
            (i);
        Intent intent = new Intent(TakePicture.this, ViewPicture.class);
        intent.putExtra(PHOTO_ID, picture.getId());
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

    // initialize the display.
    if (pictures.size() > 0) {
      primaryImage.setImageBitmap(pictures.get(0).getBitmap());
      primaryImageId = pictures.get(0).getId();
    } else {
      primaryImageId = -1;
      primaryImage.setImageBitmap(null);
    }

    // slice the image list
    if (pictures.size() > 1) {
      List<Model.Picture> otherPictures = pictures.subList(1,
          pictures.size());

      imageList.setAdapter(new PictureAdapter(this, 0,
          otherPictures.toArray(new Model.Picture[0])));

    } else {
      imageList.setAdapter(new PictureAdapter(this, 0,
          new Model.Picture[0]));
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
    parameters.setPictureSize(MINIMUM_PICTURE_EDGE_LENGTH, MINIMUM_PICTURE_EDGE_LENGTH);
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
