package com.example.sequencegame;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class GameOverActivity extends AppCompatActivity {

    private int score;
    private SQLiteDatabase database;
    private boolean isHighScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        score = getIntent().getIntExtra("score", 0);

        HighScoreDatabaseHelper dbHelper = new HighScoreDatabaseHelper(this);
        database = dbHelper.getWritableDatabase();

        TextView scoreTextView = findViewById(R.id.scoreTextView);
        EditText nameEditText = findViewById(R.id.nameEditText);
        Button saveButton = findViewById(R.id.saveButton);
        Button hiScoreButton = findViewById(R.id.hiScoreButton);

        scoreTextView.setText("Your Score: " + score);

        isHighScore = checkIfHighScore();

        if (isHighScore) {
            nameEditText.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.VISIBLE);
        } else {
            nameEditText.setVisibility(View.GONE);
            saveButton.setVisibility(View.GONE);
        }

        saveButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            if (!name.isEmpty()) {
                saveHighScore(name, score);
                nameEditText.setVisibility(View.GONE);
                saveButton.setVisibility(View.GONE);
            }
        });

        hiScoreButton.setOnClickListener(v -> {
            Intent intent = new Intent(GameOverActivity.this, HiScoreActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private boolean checkIfHighScore() {
        HighScoreDatabaseHelper dbHelper = new HighScoreDatabaseHelper(this);
        List<Integer> highScores = dbHelper.getHighScores(database);
        return highScores.size() < 5 || score > highScores.get(highScores.size() - 1);
    }

    private void saveHighScore(String name, int score) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("score", score);
        database.insert("high_scores", null, values);
    }
}
