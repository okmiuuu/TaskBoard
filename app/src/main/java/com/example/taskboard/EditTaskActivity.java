package com.example.taskboard;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.taskboard.databinding.ActivityCreateTaskBinding;
import com.example.taskboard.databinding.ActivityEditTaskBinding;

import java.io.IOException;
import java.io.InputStream;

public class EditTaskActivity extends AppCompatActivity {
    Task.DifficultyType taskDifficulty;
    Task.UrgencyType taskUrgency;
    ActivityEditTaskBinding binding;
    Uri taskImageUri;
    private static final int PICK_IMAGE_REQUEST = 4;


    Task oldTask;
    Task updatedTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityEditTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        SetOldTaskData();


        binding.addTaskImageView.setOnClickListener(v -> openImageChooser());

        binding.difficultyEasyButton.setOnClickListener(v -> SetActiveDifficultyButton(Task.DifficultyType.EASY));
        binding.difficultyMediumButton.setOnClickListener(v -> SetActiveDifficultyButton(Task.DifficultyType.MEDIUM));
        binding.difficultyHardButton.setOnClickListener(v -> SetActiveDifficultyButton(Task.DifficultyType.HARD));

        binding.urgencyUrgentButton.setOnClickListener(v -> SetActiveUrgencyButton(Task.UrgencyType.URGENT));
        binding.urgencyCanWaitButton.setOnClickListener(v -> SetActiveUrgencyButton(Task.UrgencyType.CAN_WAIT));
        binding.urgencyLongPeriodButton.setOnClickListener(v -> SetActiveUrgencyButton(Task.UrgencyType.LONG_PERIOD));

        binding.saveTaskButton.setOnClickListener(v -> OutSaveChangesPopUp());

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

    private void SetActiveDifficultyButton(Task.DifficultyType newDifficulty) {
        switch (newDifficulty) {
            case EASY: {
                taskDifficulty = Task.DifficultyType.EASY;
                binding.difficultyEasyButton.setImageResource(R.drawable.difficulty_easy_active);
                binding.difficultyMediumButton.setImageResource(R.drawable.difficulty_medium);
                binding.difficultyHardButton.setImageResource(R.drawable.difficulty_hard);
                break;
            }
            case MEDIUM: {
                taskDifficulty = Task.DifficultyType.MEDIUM;
                binding.difficultyEasyButton.setImageResource(R.drawable.difficulty_easy);
                binding.difficultyMediumButton.setImageResource(R.drawable.difficulty_medium_active);
                binding.difficultyHardButton.setImageResource(R.drawable.difficulty_hard);
                break;
            }
            case HARD: {
                taskDifficulty = Task.DifficultyType.HARD;
                binding.difficultyEasyButton.setImageResource(R.drawable.difficulty_easy);
                binding.difficultyMediumButton.setImageResource(R.drawable.difficulty_medium);
                binding.difficultyHardButton.setImageResource(R.drawable.difficulty_hard_active);
                break;
            }
        }
    }

    private void SetActiveUrgencyButton(Task.UrgencyType newUrgency) {
        switch (newUrgency) {
            case URGENT: {
                taskUrgency = Task.UrgencyType.URGENT;
                binding.urgencyUrgentButton.setImageResource(R.drawable.urgency_urgent_active);
                binding.urgencyCanWaitButton.setImageResource(R.drawable.urgency_can_wait);
                binding.urgencyLongPeriodButton.setImageResource(R.drawable.urgency_long_period);
                break;
            }
            case CAN_WAIT: {
                taskUrgency = Task.UrgencyType.CAN_WAIT;
                binding.urgencyUrgentButton.setImageResource(R.drawable.urgency_urgent);
                binding.urgencyCanWaitButton.setImageResource(R.drawable.urgency_can_wait_active);
                binding.urgencyLongPeriodButton.setImageResource(R.drawable.urgency_long_period);
                break;
            }
            case LONG_PERIOD: {
                taskUrgency = Task.UrgencyType.LONG_PERIOD;
                binding.urgencyUrgentButton.setImageResource(R.drawable.urgency_urgent);
                binding.urgencyCanWaitButton.setImageResource(R.drawable.urgency_can_wait);
                binding.urgencyLongPeriodButton.setImageResource(R.drawable.urgency_long_period_active);
                break;
            }
        }
    }

    private void SetOldTaskData() {
        Intent intent = getIntent();
        oldTask = intent.getParcelableExtra("selectedTask");

        if (oldTask.PhotoUri != null) {
            try {
                Uri imageUri = Uri.parse(oldTask.PhotoUri);
                try (InputStream inputStream = this.getContentResolver().openInputStream(imageUri)) {
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    binding.addTaskImageView.setImageBitmap(bitmap);
                    taskImageUri = imageUri;
                }
            } catch (IOException | SecurityException e) {
                e.printStackTrace();
            }
        }

        binding.nameEditText.setText(oldTask.Name);
        binding.descriptionEditText.setText(oldTask.Description);
        taskDifficulty = oldTask.Difficulty;
        SetActiveDifficultyButton(taskDifficulty);
        taskUrgency = oldTask.Urgency;
        SetActiveUrgencyButton(taskUrgency);
    }

    private void OutSaveChangesPopUp() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.accept_changes)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    SaveChanges();
                })
                .setNegativeButton(R.string.no, (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }


    private void SaveChanges() {
        String taskNameEditText = binding.nameEditText.getText().toString();
        String taskName = taskNameEditText.isEmpty() ? getString(R.string.no_name) : taskNameEditText;
        String taskDescriptionEditText = binding.descriptionEditText.getText().toString();
        String taskDescription = taskDescriptionEditText.isEmpty() ? "" : taskDescriptionEditText;
        String imageUriToString = taskImageUri != null ? taskImageUri.toString() : GetPlaceHolderUriString();

        updatedTask = new Task(oldTask);
        Task.AllTasks.remove(oldTask);
        updatedTask.Name = taskName;
        updatedTask.Description = taskDescription;
        updatedTask.Urgency = taskUrgency;
        updatedTask.Difficulty = taskDifficulty;
        updatedTask.PhotoUri = imageUriToString;

        Task.AllTasks.add(updatedTask);

        Task.saveAllTasksToJsonFile(this);
        finish();
    }
}
