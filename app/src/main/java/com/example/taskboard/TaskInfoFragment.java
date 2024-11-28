package com.example.taskboard;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.taskboard.databinding.FragmentTaskInfoBinding;

import java.io.IOException;
import java.io.InputStream;

public class TaskInfoFragment extends Fragment {

    private FragmentTaskInfoBinding binding;
    private static Task mainTask = new Task();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mainTask = getArguments().getParcelable("selectedTask");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTaskInfoBinding.inflate(inflater, container, false);
        //loadTaskData();
        return binding.getRoot();
    }

    public void setClickListeners() {
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
            Task.saveAllTasksToJsonFile(requireContext());

            SetTaskStatus();

            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).printTasksInTaskList(Task.SortById(Task.AllTasks));
                //((MainActivity) getActivity()).onTaskUpdated();
            }
        });
    }

    public void updateTaskInfo(Task task) {
        mainTask = task;
        if (mainTask != null) {
            binding.taskName.setText(mainTask.Name);
            binding.taskDescription.setText(mainTask.Description);

            SetTaskDifficulty();
            SetTaskUrgency();
            SetTaskStatus();

            if (mainTask.PhotoUri != null) {
                try {
                    Uri imageUri = Uri.parse(mainTask.PhotoUri);
                    try (InputStream inputStream = requireActivity().getContentResolver().openInputStream(imageUri)) {
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        binding.taskPhoto.setImageBitmap(bitmap);
                    }
                } catch (IOException | SecurityException e) {
                    e.printStackTrace();
                }
            }

            Task.saveAllTasksToJsonFile(requireContext());
        }
    }

    public static boolean IsOpenedFragmentForGivenTask(Task task) {
        return mainTask.Id == task.Id;
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

    public static TaskInfoFragment newInstance(Task task) {
        TaskInfoFragment fragment = new TaskInfoFragment();
        Bundle args = new Bundle();
        args.putParcelable("selectedTask", task);
        fragment.setArguments(args);

        return fragment;
    }
}