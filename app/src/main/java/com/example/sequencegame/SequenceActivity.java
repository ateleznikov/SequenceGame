package com.example.sequencegame;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SequenceActivity extends AppCompatActivity {

    private List<Integer> colorSequence;
    private Button buttonRed, buttonBlue, buttonGreen, buttonYellow;
    private TextView instructionTextView;
    private Handler handler;
    private int currentStep;
    private int score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sequence);

        buttonRed = findViewById(R.id.buttonRed);
        buttonBlue = findViewById(R.id.buttonBlue);
        buttonGreen = findViewById(R.id.buttonGreen);
        buttonYellow = findViewById(R.id.buttonYellow);
        instructionTextView = findViewById(R.id.instructionTextView);

        colorSequence = new ArrayList<>();
        handler = new Handler();

        score = getIntent().getIntExtra("score", 0);

        generateSequence(4);
        displaySequence();
    }

    private void generateSequence(int size) {
        Random random = new Random();
        colorSequence.clear();
        for (int i = 0; i < size; i++) {
            colorSequence.add(random.nextInt(4));
        }
    }

    private void displaySequence() {
        currentStep = 0;
        instructionTextView.setText("Watch the sequence!");

        for (int i = 0; i < colorSequence.size(); i++) {
            final int index = i;
            handler.postDelayed(() -> highlightButton(colorSequence.get(index)), i * 1000L);
        }

        handler.postDelayed(() -> {
            Intent intent = new Intent(SequenceActivity.this, PlayActivity.class);
            intent.putIntegerArrayListExtra("sequence", new ArrayList<>(colorSequence));
            intent.putExtra("score", score);
            startActivity(intent);
            finish();
        }, colorSequence.size() * 1000L);
    }

    private void highlightButton(int color) {
        resetButtonColors();

        switch (color) {
            case 0:
                buttonRed.setBackgroundColor(Color.parseColor("#FF6666"));
                break;
            case 1:
                buttonBlue.setBackgroundColor(Color.parseColor("#6666FF"));
                break;
            case 2:
                buttonGreen.setBackgroundColor(Color.parseColor("#66FF66"));
                break;
            case 3:
                buttonYellow.setBackgroundColor(Color.parseColor("#FFFF66"));
                break;
        }

        handler.postDelayed(this::resetButtonColors, 500L);
    }

    private void resetButtonColors() {
        buttonRed.setBackgroundColor(getResources().getColor(R.color.red));
        buttonBlue.setBackgroundColor(getResources().getColor(R.color.blue));
        buttonGreen.setBackgroundColor(getResources().getColor(R.color.green));
        buttonYellow.setBackgroundColor(getResources().getColor(R.color.yellow));
    }
}
