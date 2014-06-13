package de.Maxr1998.Toolbox;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;

import java.util.Random;

public class DeveloperSectionActivity extends Activity {
    public static int randInt(int min, int max) {

        // Usually this can be a field rather than a method variable
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    protected void onCreate(Bundle savedInstanceState) {
        // Layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev);
    }

    public void test(View view) {
        String answer;
        String numbers;
        int x = randInt(0, 9);
        int y = randInt(0, 9);
        int z = randInt(0, 9);

        numbers = String.valueOf(x) + String.valueOf(y) + String.valueOf(z);

        if (x % 2 == 0) {
            x = 0;
        } else x = 1;

        if (y % 2 == 0) {
            y = 0;
        } else y = 1;

        if (z % 2 == 0) {
            z = 0;
        } else z = 1;

        if (x + y + z <= 1) {
            answer = "Yes";
        } else answer = "No";

        new AlertDialog.Builder(this)
                .setTitle(answer)
                .setMessage(numbers)
                .create().show();
    }

}
