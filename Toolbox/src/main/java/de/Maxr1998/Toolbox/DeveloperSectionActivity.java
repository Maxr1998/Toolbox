package de.Maxr1998.Toolbox;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import java.util.Random;

public class DeveloperSectionActivity extends Activity {
    private static Activity ACTIVITY;
    private static Button BUTTON;
    private static Random RANDOM = new Random();
    final long countDownTime = 300 * 1000;
    int i;
    SharedPreferences prefs;

    public void Timer(long waitTime) {
        new CountDownTimer(waitTime, 1000) {
            public void onTick(long millisUntilFinished) {
                long sUntilFinished = millisUntilFinished / 1000;
                long minutesUntilFinished = sUntilFinished / 60;
                long slmUntilFinished = sUntilFinished - minutesUntilFinished * 60;
                String slmUntilFinishedString;
                if (slmUntilFinished < 10) {
                    slmUntilFinishedString = "0" + String.valueOf(slmUntilFinished);
                } else slmUntilFinishedString = String.valueOf(slmUntilFinished);
                BUTTON.setText("Wait " + minutesUntilFinished + ":" + slmUntilFinishedString);
            }

            public void onFinish() {
                BUTTON.setEnabled(true);
                BUTTON.setText("Random Boolean");
            }
        }.start();
    }

    private boolean getRandomBoolean() {
        return RANDOM.nextBoolean();
    }

    protected void onCreate(Bundle savedInstanceState) {
        // Layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev);

        ACTIVITY = this;
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        i = 0;

        // Button
        BUTTON = (Button) findViewById(R.id.button);

        BUTTON.setText("Random Boolean");
        long lastClickTime = prefs.getLong("lastClickTime", 0);
        long passedTime = System.currentTimeMillis() - lastClickTime;
        if (passedTime < countDownTime) {
            BUTTON.setEnabled(false);
            Timer(countDownTime - passedTime);
        } else BUTTON.setEnabled(true);

        BUTTON.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                i++;
                String message = String.valueOf(getRandomBoolean());

                new AlertDialog.Builder(ACTIVITY)
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok, null)
                        .create().show();

                if (i >= 3) {
                    BUTTON.setEnabled(false);
                    prefs.edit().putLong("lastClickTime", System.currentTimeMillis()).commit();
                    Timer(countDownTime);
                }
            }
        });
    }
}