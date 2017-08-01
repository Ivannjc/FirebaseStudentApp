package sg.edu.rp.c346.firebasestudentapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView lvStudent;
    private ArrayList<Student> alStudent;
    private ArrayAdapter<Student> aaStudent;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference studentListRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lvStudent = (ListView)findViewById(R.id.listViewStudents);
        alStudent = new ArrayList<Student>();
        aaStudent = new ArrayAdapter<Student>(this, android.R.layout.simple_list_item_1, alStudent);
        lvStudent.setAdapter(aaStudent);

        firebaseDatabase = FirebaseDatabase.getInstance();
        studentListRef = firebaseDatabase.getReference("studentList");

        lvStudent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Student studentList = (Student)adapterView.getItemAtPosition(i);
                Intent intent = new Intent(MainActivity.this, StudentDetailsActivity.class);
                intent.putExtra("Student", studentList);
                startActivity(intent);
            }
        });

        studentListRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.i("MainActivity", "onChildAdded()");
                Student student = dataSnapshot.getValue(Student.class);
                if (student != null) {
                    student.setId(dataSnapshot.getKey());

                    alStudent.add(student);
                    aaStudent.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.i("MainActivity", "onChildChanged()");

                String selectedId = dataSnapshot.getKey();
                Student student = dataSnapshot.getValue(Student.class);
                if (student != null) {
                    for (int i = 0; i < alStudent.size(); i++) {
                        if (alStudent.get(i).getId().equals(selectedId)) {
                            student.setId(selectedId);
                            alStudent.set(i, student);
                        }
                    }
                    aaStudent.notifyDataSetChanged();
                }
            }


            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.i("MainActivity", "onChildRemoved()");

                String selectedId = dataSnapshot.getKey();
                for(int i= 0; i < alStudent.size(); i++) {
                    if (alStudent.get(i).getId().equals(selectedId)) {
                        alStudent.remove(i);
                    }
                }
                aaStudent.notifyDataSetChanged();
            }


            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.i("MainActivity", "onChildMoved()");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("MainActivity", "Database error occurred", databaseError.toException());

            }
        });



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), AddStudentActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
