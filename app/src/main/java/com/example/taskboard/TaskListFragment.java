package com.example.taskboard;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView; // Import RecyclerView
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.taskboard.databinding.FragmentTaskListBinding;
import java.util.ArrayList;

public class TaskListFragment extends Fragment {

    private FragmentTaskListBinding binding;

    public TaskListFragment() {
        // Required empty public constructor
    }

    public static TaskListFragment newInstance(String param1, String param2) {
        TaskListFragment fragment = new TaskListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTaskListBinding.inflate(inflater, container, false);


        binding.scrollToTopButton.setOnClickListener(v -> {
            binding.recyclerView.smoothScrollToPosition(0);
        });

        // Add a scroll listener to show/hide the button based on scroll position
        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    binding.scrollToTopButton.setVisibility(View.VISIBLE);
                } else if (dy < 0) {
                    binding.scrollToTopButton.setVisibility(View.GONE);
                }
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getContext() != null) {
            Task.loadAllTasksFromFile(getContext());
            PrintInfoForTasks(Task.SortById(Task.AllTasks));
        }
    }

    public void PrintInfoForTasks(ArrayList<Task> tasks) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) binding.recyclerView.getLayoutManager();

        if (layoutManager == null) {
            layoutManager = new LinearLayoutManager(requireContext());
            binding.recyclerView.setLayoutManager(layoutManager);
        }

        int scrollPosition = layoutManager.findFirstVisibleItemPosition();

        TaskAdapter taskAdapter = new TaskAdapter(requireContext(), tasks);
        binding.recyclerView.setAdapter(taskAdapter);

        binding.recyclerView.scrollToPosition(scrollPosition);
    }
}