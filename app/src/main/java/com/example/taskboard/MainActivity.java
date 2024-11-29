package com.example.taskboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;

import com.example.taskboard.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    ArrayList<Task> printedTasks = new ArrayList<>();

//    public void updateTaskInfoFragment(Task task) {
//        TaskInfoFragment taskInfoFragment = (TaskInfoFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentTaskInfo);
//
//        if (taskInfoFragment != null) {
//            binding.fragmentTaskInfo.setVisibility(View.VISIBLE);
//            taskInfoFragment.updateTaskInfo(task);
//            taskInfoFragment.setClickListeners();
//        }
//    }

    public void updateTaskInfoFragment(Task task) {
        TaskInfoFragment taskInfoFragment = (TaskInfoFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentTaskInfo);

        if (taskInfoFragment != null) {
            binding.fragmentTaskInfo.setVisibility(View.VISIBLE);
            taskInfoFragment.updateTaskInfo(task);
            taskInfoFragment.setClickListeners();
        }
    }

    public void addTran(Task task) {
        if (task == null) {
            Log.e("TaskInfoFragment", "Task object is null");
            return;
        }

        TaskInfoFragment taskFragment = TaskInfoFragment.newInstance(task);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.fragmentTaskInfo, taskFragment);
        transaction.addToBackStack(null); // Optional: Only if you want to allow back navigation
        transaction.commit();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding =ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        File file = new File(this.getFilesDir(), "tasks.json");
//        try (FileOutputStream fos = new FileOutputStream(file)) {
//            // Write an empty string to clear the file
//            fos.write("".getBytes());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
        setSupportActionBar(binding.toolbar);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void printTasksInTaskList(ArrayList<Task> tasks) {
        TaskListFragment taskListFragment = (TaskListFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentTaskList);
        if (taskListFragment != null) {
            taskListFragment.PrintInfoForTasks(tasks);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        String title = String.format("%d %s", Task.GetReadyTasks().size(), getString(R.string.ready_tasks));
        getSupportActionBar().setTitle(title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

//        MenuItem searchItem = menu.findItem(R.id.search_tasks);
//        SearchView searchView = (SearchView) searchItem.getActionView();
//        if (searchView != null) {
//            searchView.setQueryHint(getString(R.string.search_for_name));
//
//            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//                @Override
//                public boolean onQueryTextSubmit(String query) {
//                    filterItems(query);
//                    return true;
//                }
//
//                @Override
//                public boolean onQueryTextChange(String newText) {
//                    Log.d("SearchView", "onQueryTextChange: " + newText);
//                    filterItems(newText) ;
//                    return true;
//                }
//            });
//
//
//            searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
//                if (!hasFocus) {
//                    filterItems("");
//                }
//            });
//        } else {
//            Log.e("MenuError", "SearchView is null");
//        }

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.add_new_task ){
            Intent intent = new Intent(MainActivity.this, CreateTaskActivity.class);
            startActivity(intent);
            return true;
        }
        if(item.getItemId() == R.id.return_back ){
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
            } else {
                super.onBackPressed();
            }
        }
//        if(item.getItemId() == R.id.sort_tasks ){
//            sortList();
//            return true;
//        }
        else {
            return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);

    }

    private void filterItems(String query) {
        Log.d("FilterItems", "Query: " + query);
        ArrayList<Task> filteredList = new ArrayList<>();
        if (query != null && !query.isEmpty()) {
            for (Task task : Task.AllTasks) {
                if (task.Name.toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(task);
                }
            }
        } else {
            filteredList.addAll(Task.AllTasks);
        }

        printTasksInTaskList(Task.SortById(filteredList));
    }

    private void sortList() {
        printedTasks.sort(Comparator.comparing(t -> t.Name));

        printTasksInTaskList(printedTasks);
    }

    public void CloseInfoTaskFragment() {
        binding.fragmentTaskInfo.setVisibility(View.INVISIBLE);
    }

//    public void onTaskUpdated() {
//        TaskListFragment taskListFragment = (TaskListFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentTaskList);
//        if (taskListFragment != null) {
//            ArrayList<Task> updatedTasks = Task.AllTasks; // Get the latest tasks
//            taskListFragment.updateTaskList(updatedTasks);
//        }
//    }

}