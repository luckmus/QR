package com.chatqr.ui.features;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.chatqr.Controller;
import com.chatqr.R;
import com.chatqr.bl.dao.model.Settings;

public class InitActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
    }

    @Override
    public void onBackPressed() {

    }

    public void saveClick(final View view) {
        Controller.getInstance().saveSettings(this, this);
    }
}
