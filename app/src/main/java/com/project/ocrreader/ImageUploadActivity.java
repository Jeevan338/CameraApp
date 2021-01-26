package com.project.ocrreader;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.IpCons;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

public class ImageUploadActivity extends AppCompatActivity {
    ImageView image;
    Button choose, upload;
    int PICK_IMAGE_REQUEST = 111;
    String URL = "http://url";

    String resultUrl = "result.txt";
    Bitmap bitmap;
    ProgressDialog progressDialog;
    private Image imageResult;
    private Uri photoURI;
    public final String APP_TAG = "MyCustomApp";
    private File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);
        image = (ImageView) findViewById(R.id.image);
        choose = (Button) findViewById(R.id.choose);
        upload = (Button) findViewById(R.id.upload);
        //opening image chooser option
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPicker(12);
            }
        });
        findViewById(R.id.camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Create a File reference to access to future access
                DateFormat s = new SimpleDateFormat("yyyyMMdd_HHmmss");
                String timeStamp = s.format(new Date());
                String imageFileName = "JPEG_" + timeStamp + "_.jpg";
                photoFile = getPhotoFileUri(imageFileName);

                // wrap File object into a content provider
                // required for API >= 24
                // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
                Uri fileProvider = FileProvider.getUriForFile(ImageUploadActivity.this, "com.project.ocrreader.fileprovider", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    // Start the image capture intent to take photo
                    startActivityForResult(intent, REQUEST_TAKE_PHOTO);
                }
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(ImageUploadActivity.this);
                progressDialog.setMessage("Uploading, please wait...");
                progressDialog.show();
                //converting image to base64 string
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                final String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                //sending image to server
                StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        progressDialog.dismiss();
                        if (s.equals("true")) {
                            Toast.makeText(ImageUploadActivity.this, "Uploaded Successful", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(ImageUploadActivity.this, "Some error occurred!", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(ImageUploadActivity.this, "Some error occurred -> " + volleyError, Toast.LENGTH_LONG).show();
                        ;
                    }
                }) {
                    //adding parameters to send
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> parameters = new HashMap<String, String>();
                        parameters.put("image", imageString);
                        return parameters;
                    }
                };
                RequestQueue rQueue = Volley.newRequestQueue(ImageUploadActivity.this);
                rQueue.add(request);
            }
        });
    }


    private void showPicker(int code) {
        ImagePicker.create(this)
                .returnMode(ReturnMode.ALL) // set whether pick and / or camera action should return immediate result or not.
                .folderMode(true) // folder mode (false by default)
                .toolbarFolderTitle("Select ") // folder selection title
                .toolbarImageTitle("select") // image selection title
                .toolbarArrowColor(Color.WHITE) // Toolbar 'up' arrow color
                .includeVideo(false)// Show video on image picker
                .single()
                .showCamera(true)
                .start(IpCons.RC_IMAGE_PICKER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IpCons.RC_IMAGE_PICKER) {
            if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
                try {
                    if (requestCode == IpCons.RC_IMAGE_PICKER) {
                        try {
                            imageResult = ImagePicker.getFirstImageOrNull(data);
                            System.out.println(imageResult.getPath());
                            Intent results = new Intent( this, ResultsActivity.class);
                            results.putExtra("IMAGE_PATH", imageResult.getPath());
                            results.putExtra("RESULT_PATH", resultUrl);
                            startActivity(results);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }


        } else if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {

            Intent results = new Intent( this, ResultsActivity.class);
            results.putExtra("IMAGE_PATH", photoFile.getAbsolutePath()); // file
            results.putExtra("RESULT_PATH", resultUrl);
            startActivity(results);
        }

    }

    String currentPhotoPath;

    private File createImageFile() throws IOException {
        //        // Create an image file name
        File image=null;
        try{DateFormat s = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String timeStamp = s.format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            image= File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = image.getAbsolutePath();}catch (Exception e){
            e.printStackTrace();
        }
        return image;
    }

    static final int REQUEST_TAKE_PHOTO = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "com.project.ocrreader.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    public File getPhotoFileUri(String fileName) {

        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(APP_TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }
}