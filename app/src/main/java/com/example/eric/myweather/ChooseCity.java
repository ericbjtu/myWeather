package com.example.eric.myweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eric.app.MyApplication;
import com.example.eric.bean.City;
import com.example.eric.util.PinYinUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChooseCity extends ActionBarActivity implements View.OnClickListener{

    private ImageView mBackbtn;
    private ListView mlistView;
    private TextView mCityNameTv;
    private MyApplication m_myApplication;

    private EditText mEditText;
    private List<City> mSearchList;

    TextWatcher mTextWatcher = new TextWatcher() {
        private CharSequence temp;
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            temp = s;
            Log.d("TextWatcher:","beforeTextChanged:"+temp);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String searchKey = s.toString();
            mSearchList = m_myApplication.showCurCityList(searchKey);

            String[] data = new String[mSearchList.size()];
            for(int i=0,j=mSearchList.size();i<j;i++){
                data[i]= mSearchList.get(i).getProvince() + "-" + mSearchList.get(i).getCity();
            }
            ArrayAdapter<String> adapter;
            adapter = new ArrayAdapter<String>(ChooseCity.this,android.R.layout.simple_expandable_list_item_1,data);
            mlistView.setAdapter(adapter);
        }

        @Override
        public void afterTextChanged(Editable s) {
            Log.d("TextWatcher","afterTextChanged");
        }
    };

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

        mEditText = (EditText) findViewById(R.id.search_edit);
        mEditText.addTextChangedListener(mTextWatcher);

        mSearchList = new ArrayList<City>();
        mSearchList = m_myApplication.getAllCityList();

        String[] data = new String[mSearchList.size()];
        for(int i=0,j=mSearchList.size();i<j;i++){
            data[i]= mSearchList.get(i).getProvince() + "-" + mSearchList.get(i).getCity();
        }

        mlistView = (ListView) findViewById(R.id.city_list_view);
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(ChooseCity.this,android.R.layout.simple_expandable_list_item_1,data);
        mlistView.setAdapter(adapter);

        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(ChooseCity.this, "You have selected "+position,Toast.LENGTH_LONG).show();
                City city = mSearchList.get(position);
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
