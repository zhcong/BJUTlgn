package com.zhcong.lgnbjut;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/**
 * Created by zhangcong on 17-4-11.
 */

public class AddActivity extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar4);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    void save(){}

    void load(){}
}
