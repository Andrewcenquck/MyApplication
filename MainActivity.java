package com.example.chenkui.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intIndicatorTabView();

//        setContentView(R.layout.watch_view);

    }
    private void intIndicatorTabView() {
        IndicatorTabView view = (IndicatorTabView) findViewById(R.id.myView);
        List<String> list = new ArrayList<>();
        list.add("待评价");
        list.add("待付款");
        list.add("待发货");
        list.add("待收货");
        view.setTabInfo(list);
        view.setSelection(3);//初始化选择指示器小球位置;
        view.setTabChangeListener(new IndicatorTabView.OnTabChangeListener() {
            @Override
            public void onTabSelected(View v, int position) {

            }
        });



    }
}
