package com.example.sequencegame;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class PlayActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private TextView instructionTextView;
    private TextView logsTextView;
    private List<Integer> colorSequence;
    private int currentStep;
    private boolean isGameOver;
    private int score;

    private Handler handler = new Handler();
    private boolean isTiltPending = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        instructionTextView = findViewById(R.id.instructionTextView);
        logsTextView = findViewById(R.id.logsTextView);

        colorSequence = getIntent().getIntegerArrayListExtra("sequence");
        currentStep = 0;
        isGameOver = false;
        score = getIntent().getIntExtra("score", 0);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        instructionTextView.setText("Tilt your phone to match the sequence!");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (isGameOver || isTiltPending) return;

        float x = event.values[0];
        float y = event.values[1];

        String logMessage = "Accelerometer X: " + x + ", Y: " + y;
        logsTextView.setText(logMessage);

        isTiltPending = true;

        handler.postDelayed(() -> {
            int direction = getTiltDirection(x, y);
            if (direction != -1) {
                String directionString = "";
                switch (direction) {
                    case 0: directionString = "North (Red)"; break;
                    case 1: directionString = "South (Blue)"; break;
                    case 2: directionString = "East (Green)"; break;
                    case 3: directionString = "West (Yellow)"; break;
                }

                String tiltMessage = "Tilt direction: " + directionString;
                logsTextView.setText(logMessage + "\n" + tiltMessage);

                if (direction == colorSequence.get(currentStep)) {
                    currentStep++;
                    score += 4;

                    if (currentStep == colorSequence.size()) {
                        goToNextRound(score);
                    }
                } else {
                    gameOver();
                }
            }

            isTiltPending = false;

        }, 1000);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private int getTiltDirection(float x, float y) {
        if (x < -4) return 0;
        if (x > 4) return 1;
        if (y > 4) return 2;
        if (y < -4) return 3;
        return -1;
    }

    private void goToNextRound(int updatedScore) {
        isGameOver = true;
        instructionTextView.setText("Well done! Moving to the next round...");
        Intent intent = new Intent(PlayActivity.this, SequenceActivity.class);
        intent.putIntegerArrayListExtra("sequence", new ArrayList<>(colorSequence));
        intent.putExtra("score", updatedScore);
        startActivity(intent);
        finish();
    }

    private void gameOver() {
        isGameOver = true;
        instructionTextView.setText("Game Over! Score: " + score);
        Intent intent = new Intent(PlayActivity.this, GameOverActivity.class);
        intent.putExtra("score", score);
        startActivity(intent);
        finish();
    }
}
