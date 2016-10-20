package net.wezu.jxg.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.IOException;

/**
 * Created by snox on 2016/4/18.
 */
public class CameraBeforeActivity extends Activity {

    private static final int RC_START_CAMERA = 443;

    String filePath = "";
    File cameraFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.getString("filePath") != null) {
            Intent intent = getIntent();

            Bundle bundle = new Bundle();
            bundle.putString("path", savedInstanceState.getString("filePath"));
            intent.putExtras(bundle);

            setResult(RESULT_OK, intent);

            finish();
            return;
        }

        String dirPath = Environment.getExternalStorageDirectory().getPath() + "/jxg/Thumb";
        cameraFile = new File(dirPath + "/" + System.currentTimeMillis() + ".jpg");
        if (!cameraFile.exists()) {
            try {
                cameraFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Uri cameraUrl = Uri.fromFile(cameraFile);
        filePath = cameraUrl.getPath();

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUrl);

        startActivityForResult(intent, RC_START_CAMERA);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("filePath", filePath);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == RC_START_CAMERA) {
            if (cameraFile != null) {
                filePath = cameraFile.getPath();

                Intent intent = getIntent();
                Bundle bundle = new Bundle();
                bundle.putString("path", filePath);
                intent.putExtras(bundle);

                setResult(RESULT_OK, intent);
                finish();
            }
        } else {
            finish();
        }
    }
}
