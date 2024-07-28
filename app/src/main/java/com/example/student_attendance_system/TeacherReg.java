package com.example.student_attendance_system;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TeacherReg extends Fragment
{

    private Spinner idcourse, idyear;
    private EditText teachername, username, Password;
    private CardView btnlogin;
    ArrayList<String> course;
    ArrayList<String> subject;
    ProgressDialog progressDialog;
        @SuppressLint("MissingInflatedId")
        @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view=inflater.inflate(R.layout.fragment_teacher_reg, container, false);

            progressDialog = new ProgressDialog(getContext());
            idcourse = view.findViewById(R.id.idcourse);
            idyear = view.findViewById(R.id.idyear);
            teachername = view.findViewById(R.id.teacherName);
            username = view.findViewById(R.id.username);
            Password = view.findViewById(R.id.password);
            btnlogin = view.findViewById(R.id.btnlogin);

            new LoadDataTask().execute();

            btnlogin.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {

                    String course = idcourse.getSelectedItem() != null ? idcourse.getSelectedItem().toString().trim() : "";
                    String year = idyear.getSelectedItem() != null ? idyear.getSelectedItem().toString().trim() : "";
                    String teacherName = teachername.getText().toString().trim();
                    String userName = username.getText().toString().trim();
                    String password = Password.getText().toString().trim();

                    if (course.equals("Select Course")) {
                        Toast.makeText(getContext(), "Please select your Course", Toast.LENGTH_SHORT).show();
                    } else if (course.isEmpty()) {
                        Toast.makeText(getContext(), "Please select your Course", Toast.LENGTH_SHORT).show();
                    } else if (year.equals("Select Year")) {
                        Toast.makeText(getContext(), "Please select your Year", Toast.LENGTH_SHORT).show();
                    } else if (teacherName.isEmpty()) {
                        Toast.makeText(getContext(), "Please enter teacher Name", Toast.LENGTH_SHORT).show();
                    } else if (userName.isEmpty()) {
                        Toast.makeText(getContext(), "Please enter Email", Toast.LENGTH_SHORT).show();
                    } else if (password.isEmpty()) {
                        Toast.makeText(getContext(), "Please enter Mobile Number", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        // Check if any of the selections are null
                        if (course == null || year == null || /*selectedSubject == null ||*/ teacherName == null || userName == null || password == null)
                        {
                            Toast.makeText(getContext(), "All fields must be filled", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            new TeacherRegistrationTask().execute(course, year,teacherName, userName, password);
                            //Intent intent = new Intent(getContext(), TeacherDashboard.class);
                            //startActivity(intent);
                        }
                    }
                }
            });

            idcourse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    String selectedCourse = idcourse.getSelectedItem().toString();

                    if (subject != null) {
                        subject.clear();
                    }
                    if (!selectedCourse.equals("Select Course")) {
                        // Call getData2() to fetch subjects when both course and year are selected
                        ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(getContext(), R.array.year, android.R.layout.simple_spinner_item);
                        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        idyear.setAdapter(yearAdapter);

                        idyear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                                String selectedCourse = idcourse.getSelectedItem().toString();
                                String selectedYear = idyear.getSelectedItem().toString();
                                if (position > 0) {
                                    // Call getData2() to fetch subjects when both course and year are selected
                                    getData2(selectedCourse, selectedYear);
                                /*ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, subject);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                idsubject.setAdapter(adapter);*/
                                }
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parentView) {
                                // Do nothing here
                            }
                        });
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // Do nothing here
                }
            });
            return view;
        }
    // gtData() method to fetch courses
    private void getData() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        course = new ArrayList<>();
        if (course != null) {
            course.clear();
        }
        course.add("Select Course");
        OkHttpClient client4 = new OkHttpClient();
        Request request4 = new Request.Builder().url("http://testproject.life/Projects/GPKSASystem/SASystem_coursedata.php").build();
        try {
            Response response4 = client4.newCall(request4).execute();
            String responseString4 = response4.body().string();
            JSONArray contacts = new JSONArray(responseString4);
            for (int i = 0; i < contacts.length(); i++) {
                JSONObject c = contacts.getJSONObject(i);
                String courseName = c.getString("courseName").toString();
                course.add(courseName);
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, course);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            idcourse.setAdapter(adapter);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    // getData2() method to fetch subjects
    private void getData2(String selectedCourse, String selectedYear) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        subject = new ArrayList<>();

        if (subject != null) {
            subject.clear();
        }
        subject.add("Select Subject");
        OkHttpClient client4 = new OkHttpClient();
        Request request4 = new Request.Builder()
                .url("http://testproject.life/Projects/GPKSASystem/SASystem_getsubject.php?courseName=" + selectedCourse + "&year=" + selectedYear)
                .build();
        try {
            Response response4 = client4.newCall(request4).execute();
            String responseString4 = response4.body().string();
            JSONArray contacts = new JSONArray(responseString4);
            for (int i = 0; i < contacts.length(); i++) {
                JSONObject c = contacts.getJSONObject(i);
                String subjectName = c.getString("subjectName").toString();
                subject.add(subjectName);
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }
    // AsyncTask to load data in the background
    public class LoadDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    getData();
                }
            });
            return null;
        }
        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            progressDialog.dismiss();
        }
    }
    // AsyncTask to perform teacher registration
    private class TeacherRegistrationTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                String course = params[0];
                String year = params[1];
                String teacherName = params[2];
                String userName = params[3];
                String password = params[4];
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(BaseURL.BASEURL+"SASystem_teacherRegistration.php?teacherName=" + teacherName + "&userName=" + userName + "&password=" + password + "&course=" + course + "&year=" + year)
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            }
        }
        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Log.e("error",result);
                if (result.equalsIgnoreCase("Registration Success.")) {
                    Toast.makeText(getContext(), "Teacher Registration Successfully.", Toast.LENGTH_SHORT).show();
                    clearUi();
                } else {
                    Toast.makeText(getContext(), "Registration failed. Please try again later.", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Network error occurred
                Toast.makeText(getContext(), "Network error. Please check your connection.", Toast.LENGTH_SHORT).show();
            }
        }



    }

    private void clearUi() {
        teachername.setText("");
        username.setText("");
        Password.setText("");

    }
}