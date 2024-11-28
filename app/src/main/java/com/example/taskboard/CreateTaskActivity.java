package com.example.taskboard;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.taskboard.databinding.ActivityCreateTaskBinding;

import java.io.IOException;
import java.io.InputStream;

public class CreateTaskActivity extends AppCompatActivity {
    Task.DifficultyType taskDifficulty = Task.DifficultyType.EASY;
    Task.UrgencyType taskUrgency = Task.UrgencyType.CAN_WAIT;
    ActivityCreateTaskBinding binding;
    Uri taskImageUri;
    private static final int PICK_IMAGE_REQUEST = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityCreateTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.addTaskImageView.setOnClickListener(v -> openImageChooser());

        binding.difficultyEasyButton.setOnClickListener(v -> {
            taskDifficulty = Task.DifficultyType.EASY;
            binding.difficultyEasyButton.setImageResource(R.drawable.difficulty_easy_active);
            binding.difficultyMediumButton.setImageResource(R.drawable.difficulty_medium);
            binding.difficultyHardButton.setImageResource(R.drawable.difficulty_hard);
        });

        binding.difficultyMediumButton.setOnClickListener(v -> {
            taskDifficulty = Task.DifficultyType.MEDIUM;
            binding.difficultyEasyButton.setImageResource(R.drawable.difficulty_easy);
            binding.difficultyMediumButton.setImageResource(R.drawable.difficulty_medium_active);
            binding.difficultyHardButton.setImageResource(R.drawable.difficulty_hard);
        });

        binding.difficultyHardButton.setOnClickListener(v -> {
            taskDifficulty = Task.DifficultyType.HARD;
            binding.difficultyEasyButton.setImageResource(R.drawable.difficulty_easy);
            binding.difficultyMediumButton.setImageResource(R.drawable.difficulty_medium);
            binding.difficultyHardButton.setImageResource(R.drawable.difficulty_hard_active);
        });

        binding.urgencyUrgentButton.setOnClickListener(v -> {
            taskUrgency = Task.UrgencyType.URGENT;
            binding.urgencyUrgentButton.setImageResource(R.drawable.urgency_urgent_active);
            binding.urgencyCanWaitButton.setImageResource(R.drawable.urgency_can_wait);
            binding.urgencyLongPeriodButton.setImageResource(R.drawable.urgency_long_period);
        });

        binding.urgencyCanWaitButton.setOnClickListener(v -> {
            taskUrgency = Task.UrgencyType.CAN_WAIT;
            binding.urgencyUrgentButton.setImageResource(R.drawable.urgency_urgent);
            binding.urgencyCanWaitButton.setImageResource(R.drawable.urgency_can_wait_active);
            binding.urgencyLongPeriodButton.setImageResource(R.drawable.urgency_long_period);
        });

        binding.urgencyLongPeriodButton.setOnClickListener(v -> {
            taskUrgency = Task.UrgencyType.LONG_PERIOD;
            binding.urgencyUrgentButton.setImageResource(R.drawable.urgency_urgent);
            binding.urgencyCanWaitButton.setImageResource(R.drawable.urgency_can_wait);
            binding.urgencyLongPeriodButton.setImageResource(R.drawable.urgency_long_period_active);
        });

        binding.addTaskButton.setOnClickListener(v -> {
            String taskNameEditText = binding.nameEditText.getText().toString();
            String taskName = taskNameEditText.isEmpty() ? getString(R.string.no_name) : taskNameEditText;
            String taskDescriptionEditText = binding.descriptionEditText.getText().toString();
            String taskDescription = taskDescriptionEditText.isEmpty() ? "" : taskDescriptionEditText;
            String imageUriToString = taskImageUri != null ? taskImageUri.toString() : GetPlaceHolderUriString();

            Task newTask = new Task(taskName,
                                    taskDescription,
                                    taskDifficulty,
                                    Task.StatusType.NOT_STARTED,
                                    taskUrgency,
                                    imageUriToString
                                    );

            Task.AllTasks.add(newTask);

            Task.saveAllTasksToJsonFile(this);
            finish();
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.choose_photo)), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            taskImageUri = data.getData();
            getContentResolver().takePersistableUriPermission(taskImageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try (InputStream inputStream = getContentResolver().openInputStream(taskImageUri)) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                binding.addTaskImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String GetPlaceHolderUriString() {
        Resources resources = this.getResources();
        int resourceId = R.drawable.placeholder;
        Uri uri = new Uri.Builder()
                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(resources.getResourcePackageName(resourceId))
                .appendPath(resources.getResourceTypeName(resourceId))
                .appendPath(resources.getResourceEntryName(resourceId))
                .build();
        return uri.toString();
    }
}