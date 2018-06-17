package com.menatbb.menat;

import android.net.Uri;
import android.support.v4.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Set;

public class Request {
ArrayList<Pair<String,String>> data;
Request(){
    data=new ArrayList<>();
}
public void addData(String key,String value){
    data.add(new Pair(key,value));
}
public boolean removeData(String key){
    for(int i=0;i<data.size();i++){
        Pair<String,String> temp=data.get(i);
        if(temp.first==key){
            data.remove(temp);
            return true;
        }
    }
    return false;
}
public String request(String Url){
    if(Url==null)
        return null;
    StringBuilder s;
    try {
        URL url=new URL(Url);
        HttpURLConnection httpURLConnection= (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("POST");
        Uri.Builder builder=new Uri.Builder();
        for (Pair p:data
             ) {
             builder.appendQueryParameter((String)p.first,(String)p.second);

        }
        String query=builder.build().getEncodedQuery();
        OutputStreamWriter outputStreamWriter=new OutputStreamWriter(httpURLConnection.getOutputStream());
        outputStreamWriter.write(query);
        outputStreamWriter.flush();
        outputStreamWriter.close();
        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
        String line = null;
        s=new StringBuilder();
        while ((line=bufferedReader.readLine())!=null){
            s.append(line);
        }
        bufferedReader.close();

    } catch (MalformedURLException e) {
        e.printStackTrace();
        return null;
    } catch (IOException e) {
        e.printStackTrace();
        return null;
    }
    if(s!=null)
        return s.toString();
    else return null;
}

}