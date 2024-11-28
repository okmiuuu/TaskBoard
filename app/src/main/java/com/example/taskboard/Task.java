package com.example.taskboard;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class Task implements Parcelable {

    public int Id;
    public String Name;
    public String Description;
    public StatusType Status;
    public DifficultyType Difficulty;
    public UrgencyType Urgency;
    public String PhotoUri;

    public static ArrayList<Task> AllTasks = new ArrayList<>();


    public enum StatusType {
        NOT_STARTED,
        IN_PROGRESS,
        READY
    }

    public enum DifficultyType {
        EASY,
        MEDIUM,
        HARD
    }

    public enum UrgencyType {
        URGENT,
        CAN_WAIT,
        LONG_PERIOD
    }

    protected Task(Parcel in) {
        Id = in.readInt();
        Name = in.readString();
        Description = in.readString();
        Status = (StatusType) in.readSerializable();
        Difficulty = (DifficultyType) in.readSerializable();
        Urgency = (UrgencyType) in.readSerializable();
        PhotoUri = in.readString();
    }

    public Task() {
        // empty constructor
    }

    public Task(String _name,
                String _description,
                DifficultyType _difficulty,
                StatusType _status,
                UrgencyType _urgency,
                String _photoUri) {
        this.Id = GenerateNewId();
        this.Name = _name;
        this.Description = _description;
        this.Difficulty = _difficulty;
        this.Status = _status;
        this.Urgency = _urgency;
        this.PhotoUri = _photoUri;
    }

    public Task(Task task) {
        this.Id = task.Id;
        this.Name = task.Name;
        this.Description = task.Description;
        this.Status = task.Status;
        this.Difficulty = task.Difficulty;
        this.Urgency = task.Urgency;
        this.PhotoUri = task.PhotoUri;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Task task = (Task) obj;
        return Id == task.Id;
    }

//    public static Task createFromJson(String jsonTask) {
//        Task taskFromJson;
//
//        if (jsonTask != null) {
//            Gson gson = new Gson();
//            taskFromJson = gson.fromJson(jsonTask, Task.class);
//            return taskFromJson;
//        } else {
//            return new Task();
//        }
//    }

    public static void loadAllTasksFromFile(Context context) {
        File file = new File(context.getFilesDir(), "tasks.json");
        if (!file.exists() || file.length() == 0) {
            AllTasks = new ArrayList<>();
            return;
        }

        StringBuilder jsonStringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath())))) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonStringBuilder.append(line);
            }
        } catch (IOException e) {
            Log.e("File Read Error", "Error reading JSON from file: " + e.getMessage());
            AllTasks = new ArrayList<>();
            return;
        }

        String jsonString = jsonStringBuilder.toString();
        Log.d("File Content", "JSON read from file: " + jsonString);

        try {
            Gson gson = new Gson();

            Type bookListType = new TypeToken<ArrayList<Task>>() {}.getType();
            ArrayList<Task> tasksFromFile = gson.fromJson(jsonString, bookListType);
            if (tasksFromFile != null) {
                AllTasks = tasksFromFile;
            } else {
                AllTasks = new ArrayList<>();
            }
        } catch (Exception e) {
            Log.e("JSON Parsing Error", "Error parsing JSON: " + e.getMessage());
            AllTasks = new ArrayList<>();
        }
    }

    public static void saveAllTasksToJsonFile(Context context) {
        Gson gson = new Gson();

        String jsonString = gson.toJson(Task.AllTasks); // This should now serialize correctly

        FileOutputStream fos = null;
        try {
            File file = new File(context.getFilesDir(), "tasks.json");

            fos = new FileOutputStream(file);
            fos.write(jsonString.getBytes());
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(Id);
        dest.writeString(Name);
        dest.writeString(Description);
        dest.writeSerializable(Status);
        dest.writeSerializable(Difficulty);
        dest.writeSerializable(Urgency);
        dest.writeString(PhotoUri);
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    private static int GenerateNewId() {
        boolean isIdTaken = true;
        int randId = 0;
        Random random = new Random();
        while (isIdTaken) {
            randId = random.nextInt(1000001);
            if (!GetAllIds().contains(randId)) {
                isIdTaken = false;
            }
        }
        return randId;
    }

    private static ArrayList<Integer> GetAllIds() {
        ArrayList<Integer> allIds = new ArrayList<>();

        for (Task task:
             AllTasks) {
            allIds.add(task.Id);
        }

        return allIds;
    }


    public static ArrayList<Task> GetReadyTasks() {
        ArrayList<Task> readyTasks = new ArrayList<>();

        for (Task task:
                AllTasks) {
            if (task.Status == StatusType.READY) {
                readyTasks.add(task);
            }
        }
        return readyTasks;
    }

    public static ArrayList<Task> SortById(ArrayList<Task> tasks) {
        Collections.sort(tasks, Comparator.comparingInt(t -> t.Id));

        return tasks;
    }
}