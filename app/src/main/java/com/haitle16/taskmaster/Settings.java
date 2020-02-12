package com.haitle16.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);



        TextView usernameinput = findViewById(R.id.usernameinput);

        View savebtn = findViewById(R.id.saveSettingbtn);
        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences storage = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = storage.edit();
                editor.putString("username",usernameinput.getText().toString());
                editor.apply();
                Toast toast = Toast.makeText(Settings.this,
                        "User information saved!",
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }
}
