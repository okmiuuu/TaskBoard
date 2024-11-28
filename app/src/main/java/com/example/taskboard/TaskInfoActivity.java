package com.example.taskboard;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.taskboard.databinding.ActivityTaskInfoBinding;

import java.io.IOException;
import java.io.InputStream;

public class TaskInfoActivity extends AppCompatActivity {

    ActivityTaskInfoBinding binding;
    Task mainTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityTaskInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loadTaskData();

        binding.taskStatus.setOnClickListener(v -> {
            int oldStatus = mainTask.Status.ordinal();
            Task.AllTasks.remove(mainTask);
            if (oldStatus < 2) {
                int newStatus = oldStatus + 1;
                mainTask.Status = Task.StatusType.values()[newStatus];
            } else {
                mainTask.Status = Task.StatusType.values()[0];
            }
            Task.AllTasks.add(mainTask);
            Task.saveAllTasksToJsonFile(this);

            SetTaskStatus();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadTaskData() {
        Intent intent = getIntent();
        mainTask = intent.getParcelableExtra("selectedTask");

        if (mainTask != null) {
            binding.taskName.setText(mainTask.Name);
            binding.taskDescription.setText(mainTask.Description);

            SetTaskDifficulty();
            SetTaskUrgency();
            SetTaskStatus();

            if (mainTask.PhotoUri != null) {
                try {
                    Uri imageUri = Uri.parse(mainTask.PhotoUri);
                    try (InputStream inputStream = this.getContentResolver().openInputStream(imageUri)) {
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        binding.taskPhoto.setImageBitmap(bitmap);
                    }
                } catch (IOException | SecurityException e) {
                    e.printStackTrace();
                }
            }

            Task.saveAllTasksToJsonFile(this);
        }
    }

    private void SetTaskDifficulty() {
        switch(mainTask.Difficulty) {
            case EASY:
                binding.taskDifficulty.setImageResource(R.drawable.difficulty_easy_active);
                break;
            case MEDIUM:
                binding.taskDifficulty.setImageResource(R.drawable.difficulty_medium_active);
                break;
            case HARD:
                binding.taskDifficulty.setImageResource(R.drawable.difficulty_hard_active);
                break;
        }
    }

    private void SetTaskUrgency() {
        switch(mainTask.Urgency) {
            case URGENT:
                binding.taskUrgency.setImageResource(R.drawable.urgency_urgent_active);
                break;
            case CAN_WAIT:
                binding.taskUrgency.setImageResource(R.drawable.urgency_can_wait_active);
                break;
            case LONG_PERIOD:
                binding.taskUrgency.setImageResource(R.drawable.urgency_long_period_active);
                break;
        }
    }

    private void SetTaskStatus() {
        switch(mainTask.Status) {
            case NOT_STARTED:
                binding.taskStatus.setImageResource(R.drawable.status_not_started);
                break;
            case IN_PROGRESS:
                binding.taskStatus.setImageResource(R.drawable.status_in_progress);
                break;
            case READY:
                binding.taskStatus.setImageResource(R.drawable.status_ready);
                break;
        }
    }
}