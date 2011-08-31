package com.rouesnel.comp5047.healthyeater;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.AndroidRuntimeException;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class TakePhoto extends Activity implements SurfaceHolder.Callback
{

    private SurfaceHolder previewSurface;
    private Camera camera;

    private boolean cameraPreviewing = false;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Setup control bindings.
        previewSurface = ((SurfaceView)findViewById(R.camera.surface)).getHolder();
        previewSurface.addCallback(this);
        previewSurface.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    }

    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        camera = Camera.open();
    }

    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int w, int h) {
        if (cameraPreviewing) {
            camera.stopPreview();
        }

        Camera.Parameters parameters = camera.getParameters();

        Camera.Size previewSize = null;
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
