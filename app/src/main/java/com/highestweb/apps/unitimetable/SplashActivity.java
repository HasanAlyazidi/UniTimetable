package com.highestweb.apps.unitimetable;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Thread logoTimer = new Thread(){

            public void run()
            {

                try{

                    int logoTimer = 0;
                    while(logoTimer < 2000)
                    {
                        sleep(100);
                        logoTimer = logoTimer + 100;
                    }

                    // class: Profile, access everything related to profile
                    Profile profile = new Profile(SplashActivity.this);

                    // check if the profile has been setup
                    if (profile.isSetup())
                    {
                        // go to main activity
                        Intent IntentActivityToGo = new Intent(getBaseContext(), MainActivity.class);
                        startActivity(IntentActivityToGo);
                    }
                    else
                    {
                        // go to profile activity (add profile)
                        Intent IntentActivityToGo = new Intent(getBaseContext(), ProfileActivity.class);
                        IntentActivityToGo.putExtra("QueryType", "add");
                        startActivity(IntentActivityToGo);
                    }

                }
                catch(Exception e)
                {
                    e.getStackTrace();
                }
                finally
                {
                    finish();
                }

            }

        };// - Thread:logoTimer
        logoTimer.start();
    }
}
