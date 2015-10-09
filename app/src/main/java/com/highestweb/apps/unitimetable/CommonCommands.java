package com.highestweb.apps.unitimetable;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by HasanAlYazidi on 10/2/2015.
 */

public class CommonCommands {

    private Activity activity;

    // initialization
    public CommonCommands(Activity activity) {
        this.activity = activity;
    }

    public static void apply(Activity act) {

        // get the activity to access its views
        final Activity activity = act;

        // header
        TextView textviewProfileName = (TextView) activity.findViewById(R.id.textviewProfileName);
        CircleImageView imageviewProfilePicture = (CircleImageView) activity.findViewById(R.id.imageviewProfilePicture);

        // class: Profile, access everything related to profile
        Profile profile = new Profile(activity);

        // show profile name
        textviewProfileName.setText(profile.getName());

        // show profile picture, if any
        if (profile.getPicturePath().isEmpty())
        {
            Picasso.with(activity.getApplicationContext()).load(R.drawable.profile_no_image).noFade().fit().centerCrop().into(imageviewProfilePicture);
        }
        else
        {
            Picasso.with(activity.getApplicationContext()).load(new File(profile.getPicturePath())).noFade().fit().centerCrop().into(imageviewProfilePicture);
        }

        // menu button
        final ImageButton imagebuttonMenu = (ImageButton) activity.findViewById(R.id.imagebuttonMenu);
        imagebuttonMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showPopup(activity, imagebuttonMenu);

            }
        });


    }

    // The method that displays the popup.
    private static void showPopup(final Activity activity, ImageButton aboveView) {

        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        //The "x" and "y" position of the popup's button on screen.
        int[] location = new int[2];

        // Get the x, y location and store it in the location[] array
        // location[0] = x, location[1] = y.
        aboveView.getLocationOnScreen(location);

        //Initialize the Point with x, and y positions
        final Point pointer = new Point();
        pointer.x = location[0] + aboveView.getWidth();
        pointer.y = location[1] + aboveView.getHeight(); // [button.getHeight()] to make the popup be at the bottom of the button
        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

        // Inflate the popup layout
        LinearLayout viewGroup = (LinearLayout) activity.findViewById(R.id.layoutPopup);
        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.popup_layout, viewGroup);

        // Creating the PopupWindow
        final PopupWindow popup = new PopupWindow(activity);
        popup.setContentView(layout);
        popup.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT); //popupWidth
        popup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT); //popupHeight
        popup.setFocusable(true);

        // Some offset to align the popup a bit to the right, and a bit down, relative to button's position.
        int OFFSET_X = 0;
        int OFFSET_Y = 0;

        // Clear the default translucent background
        popup.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        // Displaying the popup at the specified location, + offsets.
        popup.showAtLocation(layout, Gravity.NO_GRAVITY, pointer.x + OFFSET_X, pointer.y + OFFSET_Y);

        // menu button: profile
        Button buttonMenuProfile = (Button) layout.findViewById(R.id.buttonMenuProfile);
        buttonMenuProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // go to profile activity (add profile)
                Intent IntentActivityToGo = new Intent(activity, ProfileActivity.class);
                IntentActivityToGo.putExtra("QueryType", "edit");
                activity.startActivity(IntentActivityToGo);

            }
        });

        // menu button: about
        Button buttonMenuAbout = (Button) layout.findViewById(R.id.buttonMenuAbout);
        buttonMenuAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity.getApplicationContext(), "About: Soon", Toast.LENGTH_LONG).show();
            }
        });

    } // - showPopup

    public static void backButton(Activity activity, Class classActivityToGoTO){

        // go to an activity
        Intent IntentActivityToGo = new Intent(activity, classActivityToGoTO);
        IntentActivityToGo.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(IntentActivityToGo);
        activity.finish();

    }
}