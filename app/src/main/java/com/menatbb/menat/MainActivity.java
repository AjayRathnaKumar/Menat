package com.menatbb.menat;

import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.Toolbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
ViewPager pager;
Adapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pager=findViewById(R.id.pager);
        adapter=new Adapter(getSupportFragmentManager());
        pager.setOffscreenPageLimit(3);
        setSupportActionBar((android.support.v7.widget.Toolbar) findViewById(R.id.action_bar));
        //PagerTabStrip pagerTabStrip=findViewById(R.id.tabs);
        //pagerTabStrip.add
        TabLayout tabLayout=findViewById(R.id.tab);
        tabLayout.setupWithViewPager(pager);
        final JSONObject jsonObject=new JSONObject();
        final JSONObject jsonObject1=new JSONObject();
        String id= android.provider.Settings.Secure.getString(getApplicationContext().getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
        try {
            jsonObject.put("DeviceId", id);
            jsonObject.put("AppVersion", 1);
            jsonObject.put("OsType", 1);
            jsonObject.put("UserId", 0);
            jsonObject.put("LanguageType", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AsyncTask asyncTask=new AsyncTask() {
            String result;

            @Override
            protected void onPostExecute(Object o) {
                System.out.println(result);
                adapter.addFragment(new Listing(),"Home");
                adapter.addFragment(new Upload(),"Upload");
                adapter.addFragment(new Settings(),"Settings");
                pager.setAdapter(adapter);
                super.onPostExecute(o);
            }

            @Override
            protected Object doInBackground(Object[] objects) {
                result=sendRequest("http://menatservice.menatbb.com/MenaServiceCommon.svc/RegisterDevice",jsonObject.toString());
                return null;
            }
        };
        asyncTask.execute();


    }
    private String sendRequest(String url,String jsonStr){
        try {
            URL url1=new URL(url);
            HttpURLConnection httpURLConnection= (HttpURLConnection) url1.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("Accept", "application/json");
            httpURLConnection.setRequestMethod("POST");
            OutputStreamWriter outputStreamWriter=new OutputStreamWriter(httpURLConnection.getOutputStream());
            outputStreamWriter.write(jsonStr);
            outputStreamWriter.flush();
            outputStreamWriter.close();
            return convertStreamToString(httpURLConnection.getInputStream());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String convertStreamToString(InputStream is){
        BufferedReader reader=new BufferedReader(new InputStreamReader(is));
        StringBuilder stringBuilder=new StringBuilder();
        String line=null;
        try{
            while((line=reader.readLine())!=null){
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try
            {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return stringBuilder.toString();
    }
}
