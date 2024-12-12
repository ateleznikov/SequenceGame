package com.example.sequencegame;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HiScoreActivity extends AppCompatActivity {

    private HighScoreDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hi_scores);

        dbHelper = new HighScoreDatabaseHelper(this);

        LinearLayout scoresLayout = findViewById(R.id.scoresLayout);
        Button backButton = findViewById(R.id.backButton);

        displayHighScores(scoresLayout);

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(HiScoreActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void displayHighScores(LinearLayout scoresLayout) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query(
                "high_scores",
                null,
                null,
                null,
                null,
                null,
                "score DESC",
                "5"
        );

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                int score = cursor.getInt(cursor.getColumnIndexOrThrow("score"));

                TextView scoreEntry = new TextView(this);
                scoreEntry.setText(String.format("%s - %d", name, score));
                scoreEntry.setTextSize(18);
                scoreEntry.setTextColor(getResources().getColor(android.R.color.white));
                scoresLayout.addView(scoreEntry);

            } while (cursor.moveToNext());
        }

        cursor.close();
    }
}
