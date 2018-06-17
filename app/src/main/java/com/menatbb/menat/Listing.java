package com.menatbb.menat;


import android.content.Intent;
import android.graphics.ImageFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
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
import java.util.ArrayList;
import java.util.zip.Inflater;


/**
 * A simple {@link Fragment} subclass.
 */
public class Listing extends Fragment {


    public Listing() {
        // Required empty public constructor
    }
 String id;
    boolean switchb=false;
    int pos=0;
    ProgressBar progressBar;
    MScrollView mScrollView;
    ArrayList<String> details=new ArrayList<>();
    public LinearLayout l;
    String st="";
    View view;
    int len=0;
    View.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v){
            try {
                Intent intent=new Intent(getContext(),Details.class);
                intent.putExtra("details",details.get(Integer.valueOf(v.getId())-1));
                startActivity(intent);
            }
            catch (NumberFormatException e1){
                e1.printStackTrace();
            }
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        id=Settings.Secure.getString(getContext().getContentResolver(),
            Settings.Secure.ANDROID_ID);
        // Inflate the layout for this fragment
        pos=0;
        len=0;
         view= inflater.inflate(R.layout.fragment_listing, container, false);
        ((SearchView) view.findViewById(R.id.search_bar)).setQueryRefinementEnabled(false);
        ((SearchView) view.findViewById(R.id.search_bar)).setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                String se=((SearchView) view.findViewById(R.id.search_bar)).getQuery().toString();
                reset(se);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.matches("[ \\t\\n\\x0B\\f\\r]*"))
                    if(st!=""){
                    reset("");
                return true;}
                return false;
            }
        });
        mScrollView=view.findViewById(R.id.scroll_view);
        (mScrollView).setListing(this);
        l=(LinearLayout) view.findViewById(R.id.list);
        progressBar=new ProgressBar(getContext());
        progressBar.setVisibility(View.INVISIBLE);
        l.addView(progressBar);
        netJob();
        return view;
    }
    void reset(String test){
        l.removeAllViews();
        l.addView(progressBar);
        pos=0;
        len=0;
        st=test;
        netJob();
    }
    public void netJob(){
        if(switchb)
            return;
        switchb=true;
        progressBar.setVisibility(View.VISIBLE);
        final JSONObject jsonObject=new JSONObject();
        final JSONObject jsonObject1=new JSONObject();
        try {
            jsonObject.put("DeviceId",id  );
            jsonObject.put("AppVersion",1);
            jsonObject.put("OsType",1);
            jsonObject.put("UserId",0);
            jsonObject.put("LanguageType",1);
            jsonObject1.put("Country","BH");
            jsonObject1.put("BusinessTypeId","-1");
            jsonObject1.put("CityId","-1");
            jsonObject1.put("Categories","-1");
            jsonObject1.put("SortType",4);
            jsonObject1.put("StartPosition",pos+0);
            pos+=20;
            jsonObject1.put("ListCount",20);
            jsonObject1.put("SearchText",st);
            jsonObject1.put("DefaultCurrency","BHD");
            jsonObject1.put("DeviceId",id);
            jsonObject1.put("AppVersion","1");
            jsonObject1.put("OsType",1);
            jsonObject1.put("UserId",0);
            jsonObject1.put("LanguageType",1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AsyncTask asyncTask=new AsyncTask() {
            String result;

            @Override
            protected void onPostExecute(Object o) {
                // Toast.makeText(getContext(),result,Toast.LENGTH_LONG).show();
                System.out.println(result);
                if(result!=null) {
                    progressBar.setVisibility(View.INVISIBLE);
                    inflateList(result);
                    switchb = false;
                }
                else {
                    Toast.makeText(getContext(), "Error communicating with server", Toast.LENGTH_SHORT).show();
                    (view.findViewById(R.id.pic)).setVisibility(View.GONE);
                }

                super.onPostExecute(o);
            }

            @Override
            protected Object doInBackground(Object[] objects) {
                sendRequest("http://menatservice.menatbb.com/MenaServiceCommon.svc/RegisterDevice",jsonObject.toString());
                result=sendRequest("http://menatservice.menatbb.com/MenaServiceBusiness.svc/SearchBusiness",jsonObject1.toString());
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
    void inflateList( String json){
        try {
            JSONObject jsonObject=new JSONObject(json);
            if(jsonObject.getBoolean("Isvalid")==true && jsonObject.getString("ResponseCode").equals("111") && jsonObject.getString("ResponseMessage").equals("Success")) {
                JSONArray bList = jsonObject.getJSONArray("BusinessList");
                for (int i = 0; i < bList.length(); i++) {
                    JSONObject item=bList.getJSONObject(i);
                    details.add(item.toString());
                    View view = LayoutInflater.from(getContext()).inflate(R.layout.list_item, null);
                   try {
                        JSONArray images = item.getJSONArray("ImagesList");
                        if(images.length()>=1)
                        Picasso.get().load(images.getString(0)).into((ImageView) view.findViewById(R.id.pic));
                        else
                       ( view.findViewById(R.id.pic)).setVisibility(View.GONE);
                    }catch (JSONException e){

                    }
                    ((TextView) view.findViewById(R.id.title)).setText(item.getString("BusinessTitle"));
                    ((TextView) view.findViewById(R.id.summary)).setText(item.getString("Summary"));
                    ((TextView) view.findViewById(R.id.price)).setText(item.getString("AskingPrice"));
                    l.addView(view,l.getChildCount()-1);
                    len++;
                    view.setId(len);
                    view.setOnClickListener(onClickListener);
                }
            }
            else
                System.out.println("no list");
        } catch (JSONException e) {
            Toast.makeText(getContext(),"done not",Toast.LENGTH_LONG).show();

            e.printStackTrace();
        }
    }
}
