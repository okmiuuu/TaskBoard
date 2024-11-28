package com.example.taskboard;


import static androidx.core.content.res.TypedArrayUtils.getString;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskboard.databinding.ItemTaskBinding;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private final List<Task> taskList;
    private final Context context;
    private static final int EDIT_TASK = R.id.edit_task;
    private static final int DELETE_TASK = R.id.delete_task;


    public TaskAdapter(Context context, List<Task> taskList) {
        this.context = context;
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTaskBinding binding = ItemTaskBinding.inflate(LayoutInflater.from(context), parent, false);
        return new TaskViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.bind(task);

        holder.itemView.setOnLongClickListener(v -> {
            showContextMenu(v, task);
            return true;
        });


        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                Intent intent = new Intent(context, TaskInfoActivity.class);
                intent.putExtra("selectedTask", task);
                context.startActivity(intent);
            } else {
                if (context instanceof MainActivity) {
                    ((MainActivity) context).updateTaskInfoFragment(task);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private final ItemTaskBinding binding;

        public TaskViewHolder(@NonNull ItemTaskBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Task task) {
            binding.taskName.setText(task.Name);

            switch(task.Difficulty) {
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

            switch(task.Urgency) {
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

            switch(task.Status) {
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

            if (task.PhotoUri != null) {
                try {
                    Uri imageUri = Uri.parse(task.PhotoUri);
                    try (InputStream inputStream = context.getContentResolver().openInputStream(imageUri)) {
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        binding.taskPhoto.setImageBitmap(bitmap);
                    }
                } catch (IOException | SecurityException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void showContextMenu(View view, Task task) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.task_context_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            int chosenItemId = item.getItemId();
                if (chosenItemId == EDIT_TASK) {
                    editTask(task);
                    return true;
                } else if (chosenItemId == DELETE_TASK) {
                    deleteTask(task);
                    return true;
                } else {
                    return false;
                }
        });

        popupMenu.show();
    }

    private void editTask(Task task) {
        Intent intent = new Intent(context, EditTaskActivity.class);
        intent.putExtra("selectedTask", task);
        context.startActivity(intent);
    }

    private void deleteTask(Task task) {
        new AlertDialog.Builder(context)
                .setMessage(R.string.delete_confirm)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    int position = taskList.indexOf(task);
                    if (position >= 0) {
                        Task.AllTasks.remove(position);

                        Task.saveAllTasksToJsonFile(context);

                        notifyItemRemoved(position);

                        if (TaskInfoFragment.IsOpenedFragmentForGivenTask(task) && context instanceof MainActivity) {
                            ((MainActivity) context).CloseInfoTaskFragment();
                        }
                    }
                })
                .setNegativeButton(R.string.no, (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }
}