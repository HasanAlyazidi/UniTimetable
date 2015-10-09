package com.highestweb.apps.unitimetable;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends Activity {

    final int REQUEST_CODE_CAMERA = 1;
    final int REQUEST_CODE_GALLERY = 2;

    // ref: https://github.com/lopspower/CircularImageView
    CircleImageView imageviewProfilePicture;

    boolean isImageSelected = false;
    String selectedImagePath = ""; // no image selected and send data without the image

    String addOrEditProfile = "add"; // [add] = add profile, [edit] = edit profile

    // class: Profile, access everything related to profile
    Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // class: Profile, access everything related to profile
        profile = new Profile(ProfileActivity.this);

        Button buttonContinue = (Button) findViewById(R.id.buttonContinue);

        final EditText edittextProfileStudentID = (EditText) findViewById(R.id.edittextProfileStudentID);
        final EditText edittextProfileName = (EditText) findViewById(R.id.edittextProfileName);
        final EditText edittextProfileProgram = (EditText) findViewById(R.id.edittextProfileProgram);
        final EditText edittextProfileSemester = (EditText) findViewById(R.id.edittextProfileSemester);

        // add picture
        imageviewProfilePicture = (CircleImageView) findViewById(R.id.imageviewProfilePicture);
        imageviewProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectImage();

            }
        });

        //+ get the data that are sent by the previous activity +\\
        Bundle intentExtras = getIntent().getExtras();
        if (intentExtras != null)
        {
            String QueryType = intentExtras.getString("QueryType");

            if ( QueryType.equals("add") ) // from activity: SplashActivity
            {
                buttonContinue.setText("Continue");
            }
            else if ( QueryType.equals("edit") ) // from activity: any activity
            {
                buttonContinue.setText("Save");

                // set profile data to the activity
                edittextProfileStudentID.setText(profile.getStudentID());
                edittextProfileName.setText(profile.getName());
                edittextProfileProgram.setText(profile.getProgram());
                edittextProfileSemester.setText(profile.getSemester());

                selectedImagePath = profile.getPicturePath();

                if (selectedImagePath.isEmpty())
                    Picasso.with(getApplicationContext()).load(R.drawable.profile_no_image).noFade().fit().centerCrop().into(imageviewProfilePicture);
                else
                    Picasso.with(getApplicationContext()).load(new File(selectedImagePath)).noFade().fit().centerCrop().into(imageviewProfilePicture);

            }

            addOrEditProfile = QueryType;

        }
        //- get the data that are sent by the previous activity -\\

        // add or edit profile button
        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // get inputs data
                String profileStudentID = edittextProfileStudentID.getText().toString();
                String profileName = edittextProfileName.getText().toString();
                String profileProgram = edittextProfileProgram.getText().toString();
                String profileSemester = edittextProfileSemester.getText().toString();

                if (profileStudentID.isEmpty() || profileName.isEmpty() || profileProgram.isEmpty() || profileSemester.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please fulfil the data", Toast.LENGTH_LONG).show();
                }
                else // data are ok, save data
                {

                    // save the profile
                    long returnDB = profile.saveProfile(profileName, profileStudentID, profileProgram, profileSemester, selectedImagePath);

                    // check if data has been saved
                    if (returnDB >= 1) // saved
                    {
                        // go to main activity
                        if (addOrEditProfile.equals("add"))
                        {
                            Intent IntentActivityToGo = new Intent(getBaseContext(), MainActivity.class);
                            startActivity(IntentActivityToGo);
                            finish();
                        }
                        else if (addOrEditProfile.equals("edit"))
                        {
                            finish();
                        }

                    }
                    else // not saved
                    {
                        Toast.makeText(getApplicationContext(), "Your profile has not been saved", Toast.LENGTH_LONG).show();
                    }



                }

            }
        });

    }

    private void selectImage() {

        // ref: http://stackoverflow.com/questions/5991319/capture-image-from-camera-and-display-in-activity

        final CharSequence[] options = { "Take Photo", "Choose from Gallery", "Default", "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Add Picture");
        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo"))
                {
                    // create folder if the folder where pictures are saved in is not existing
                    File filePathSavePicture = new File(getSavingLocation());
                    if(!filePathSavePicture.isDirectory()) {
                        filePathSavePicture.mkdirs();
                    }

                    File f = new File(getSavingLocation(), "temp.jpg");

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, REQUEST_CODE_CAMERA);
                }
                else if (options[item].equals("Choose from Gallery"))
                {
                    // choose picture from gallery
                    Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, REQUEST_CODE_GALLERY);
                }
                else if (options[item].equals("Default"))
                {
                    // return the default profile picture
                    Picasso.with(getApplicationContext()).load(R.drawable.profile_no_image).noFade().fit().centerCrop().into(imageviewProfilePicture);
                    selectedImagePath = "";
                }
                else if (options[item].equals("Cancel"))
                {
                    dialog.dismiss();
                }

            }

        });

        builder.show();

    }

    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == REQUEST_CODE_CAMERA) {

                // save the picture
                try {

                    // tempFile holds the temporary picture that is created automatically after selecting "Take Photo" option
                    File tempFile = new File(getSavingLocation() + File.separator + "temp.jpg");

                    // the new picture
                    File file = new File(getSavingLocation(), String.valueOf(System.currentTimeMillis()) + ".jpg");

                    //+ save the new picture
                    OutputStream outFile = new FileOutputStream(file);

                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    Bitmap bitmap = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), bitmapOptions);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);

                    outFile.flush();
                    outFile.close();
                    //- save the new picture

                    // delete the temporary picture
                    tempFile.delete();

                    // show the picture
                    Picasso.with(getApplicationContext()).load(new File(file.toString())).noFade().fit().centerCrop().into(imageviewProfilePicture);

                    isImageSelected = true;
                    selectedImagePath = file.toString();

                    Toast.makeText(getApplicationContext(), "Nice Picture!", Toast.LENGTH_LONG).show();

                }
                catch (Exception e) {
                    e.printStackTrace();

                    Toast.makeText(getApplicationContext(), "Error, Please Try Again.", Toast.LENGTH_LONG).show();

                    isImageSelected = false;
                    selectedImagePath = "";
                }


            } else if (requestCode == REQUEST_CODE_GALLERY) {

                Uri selectedImage = data.getData();

                String[] filePath = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage, filePath, null, null, null);

                if (cursor != null) {

                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePath[0]);

                    String picturePath = cursor.getString(columnIndex);

                    isImageSelected = true;
                    selectedImagePath = picturePath;

                    cursor.close();

                    // show the picture
                    imageviewProfilePicture.setImageResource(0);
                    Picasso.with(getApplicationContext()).load(new File(picturePath)).noFade().fit().centerCrop().into(imageviewProfilePicture);

                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Error, Please Try Again.", Toast.LENGTH_LONG).show();
                }


            }

        }
    }

    // get location of where pictures are saved
    public String getSavingLocation()
    {
        return Environment.getExternalStorageDirectory() + File.separator + getResources().getString(R.string.app_name);
    }

}
