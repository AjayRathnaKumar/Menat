package com.menatbb.menat;

import android.util.ArraySet;
import android.util.Pair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Set;

public class Form {
    private String boundary;
    private ArrayList<Pair <String,String>> data;
    private ArrayList<Pair<String,Pair<File,String>>> files;


    public Form(){
        boundary="****--****";
        data=new ArrayList<>();
        files=new ArrayList<>();
    }
    public void setBoundary(String boundary) {
        this.boundary = boundary;
    }

    public String getBoundary() {
        return boundary;
    }

    public int addFile(String name,File file, String mimeType){
        for (Pair x:files) {
            if(x.first.equals(name))
                return 0;
        }
        files.add(new Pair<String, Pair<File,String>>(name,new Pair(file,mimeType)));
        return 1;
    }

    public int removeFile(String name){
        for (Pair x:files) {
            if(x.first.equals(name)) {
                files.remove(x);
                return 1;
            }
        }
        return 0;
    }

    public int removeData(String name){
        for (int i=0;i<data.size();i++) {
            Pair x=data.get(i);
            if(x.first.equals(name))
            {
                data.remove(i);
                return 1;
            }
        }
        return 0;
    }


    public int addData(String name, String value){
        for (Pair x:data) {
            if(x.first.equals(name))
                return 0;
        }
        data.add(new Pair<String, String>(name,value));
        return 1;
    }

    public String getData(String name){
        for (Pair x:data) {
            if(x.first.equals(name))
                return (String) x.second;
        }
        return null;
    }

    public String submit(String url) throws IOException {
        boundary="****"+Long.toString(System.currentTimeMillis())+"****";
        String twoHyphens = "--";
        String lineEnd = "\r\n";
        URL url1 = new URL(url);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url1.openConnection();
        httpURLConnection.setDoInput(true);
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setUseCaches(false);
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("Connection","Keep-Alive");
        httpURLConnection.setRequestProperty("ENCTYPE","multipart/form-data");
        httpURLConnection.setRequestProperty("Content-Type","multipart/form-data;boundary="+boundary);

        //httpURLConnection.connect();
        DataOutputStream dos=new DataOutputStream(httpURLConnection.getOutputStream());
        for (Pair x:data) {
            dos.writeBytes(twoHyphens+boundary+lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"" + x.first.toString() + "\"" + lineEnd);
            dos.writeBytes("Content-Type: text/plain; charset=utf-8"+lineEnd);
            dos.writeBytes(lineEnd);
          //  dos.writeBytes(x.second.toString());
            System.out.println("testing arabic:    "+java.net.URLDecoder.decode(URLEncoder.encode("pppناتناتانا","utf-8"),"utf-8"));

           String temp= URLEncoder.encode(x.second.toString(),"UTF-8");
          dos.writeBytes(temp);

        //    dos.writeBytes(new String((Charset.forName("UTF-8").encode(x.second.toString())).array(),"utf-8"));
            dos.writeBytes(lineEnd);
        }
        for (Pair x:files) {
            Pair fileData=(Pair)x.second;
            File file= (File) fileData.first;
            dos.writeBytes(twoHyphens+boundary+lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"" + x.first.toString() + "\"; filename=\""+file.getAbsolutePath()+"\"" + lineEnd);
            dos.writeBytes("Content-Type: "+fileData.second+lineEnd);
            dos.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);
            dos.writeBytes(lineEnd);
            FileInputStream fileInputStream=new FileInputStream(file);
            final int MAX_BUFFER_SIZE=1*1024*1024;
            int bytesAvailable=fileInputStream.available();
            int bufferSize=Math.min(bytesAvailable,MAX_BUFFER_SIZE);
            byte[] buffer=new byte[bufferSize];
            int bytesRead=fileInputStream.read(buffer,0,bufferSize);
            while(bytesRead>0){
                dos.write(buffer,0,bufferSize);
                bytesAvailable=fileInputStream.available();
                bufferSize=Math.min(bytesAvailable,MAX_BUFFER_SIZE);
                bytesRead=fileInputStream.read(buffer,0,bufferSize);

            }
            dos.writeBytes(lineEnd);
        }
        dos.writeBytes(twoHyphens+boundary+lineEnd);
        dos.flush();
        dos.close();
      //  if(httpURLConnection.getResponseCode()!=HttpURLConnection.HTTP_OK ||httpURLConnection.getResponseCode()!=HttpURLConnection.HTTP_ACCEPTED)
        //    return null;

        InputStream inputStream=httpURLConnection.getInputStream();
        String result= convertStreamToString(inputStream);
        System.out.println(result);
return result;
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
