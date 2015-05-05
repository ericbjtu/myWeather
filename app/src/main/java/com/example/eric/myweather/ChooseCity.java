package com.example.eric.myweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.eric.app.MyApplication;
import com.example.eric.bean.City;

import java.util.ArrayList;

public class ChooseCity extends ActionBarActivity implements View.OnClickListener{

    private ImageView mBackbtn;
    private ListView mlistView;
    private TextView mCityNameTv;
    private MyApplication m_myApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_city);

        m_myApplication = MyApplication.getInstance();
        mBackbtn = (ImageView) findViewById(R.id.title_back);
        mBackbtn.setOnClickListener(this);

        mCityNameTv = (TextView) findViewById(R.id.title_name);
        SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        String cityName = sharedPreferences.getString("main_city_name","北京");//缺省值为北京
        mCityNameTv.setText("当前城市："+cityName);

        ArrayList<String> mCityNameData = m_myApplication.showCityList();
        String[] data = new String[mCityNameData.size()];
        for(int i=0,j=mCityNameData.size();i<j;i++){
            data[i]=mCityNameData.get(i);
        }

        mlistView = (ListView) findViewById(R.id.city_list_view);
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(ChooseCity.this,android.R.layout.simple_expandable_list_item_1,data);
        mlistView.setAdapter(adapter);

        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //String posS = "你单击了："+Integer.toString(position);
                //Toast.makeText(ChooseCity.this, posS, Toast.LENGTH_SHORT).show();

                City city = m_myApplication.getCity(position);
                String returnCityNumber = city.getNumber();
                String returnCityName = city.getCity();
                //点击后切换到指定城市
                SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("main_city_code",returnCityNumber);
                editor.putString("main_city_name",returnCityName);
                editor.commit();

                Intent intent = new Intent();
                intent.putExtra("CityNumber",returnCityNumber);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_choose_city, menu);
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
