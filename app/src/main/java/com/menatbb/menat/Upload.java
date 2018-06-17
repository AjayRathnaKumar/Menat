package com.menatbb.menat;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import static android.os.Build.VERSION.SDK_INT;


/**
 * A simple {@link Fragment} subclass.
 */
public class Upload extends Fragment {
View view;
ArrayList<String> paths=new ArrayList<>();
    public Upload() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_upload, container, false);
        view.findViewById(R.id.select).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(SDK_INT<23){
                    readImages();
                }
                    else
                        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) {
                    readImages();
                }
                else
                {
                    if(shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){
                        Toast.makeText(getContext(),"Please grant this permission to read images",Toast.LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                        }
                        else {
                        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                    }
                }
            }
        });
        view.findViewById(R.id.upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(paths.size()==0){
                    Toast.makeText(getContext(),"No photos loaded !",Toast.LENGTH_SHORT).show();
                    return;
                }
                LinearLayout linearLayout=new LinearLayout(getContext());
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.addView(new ProgressBar(getContext()));
                linearLayout.setGravity(Gravity.CENTER);
                TextView textView=new TextView(getContext());
                textView.setText("Please wait...");
                textView.setGravity(Gravity.CENTER);
                textView.setTextColor(getResources().getColor(R.color.colorAccent));
                linearLayout.addView(textView);
                linearLayout.setBackgroundColor(Color.argb(200,0,0,0));

                final PopupWindow popUpWindow=new PopupWindow(linearLayout, LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
                popUpWindow.showAtLocation(view, Gravity.CENTER,0,0);
                final Form form=new Form();
                for(int i=0;i<paths.size();i++){
                   /* String path="";
                            String[] p={MediaStore.Images.Media.DATA};
                    Cursor cursor=getContext().getContentResolver().query(uris.get(i),p,null,null,null);
                    if(cursor.moveToFirst()){
                        int col_index=cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA);
                        path=cursor.getString(col_index);
                    }
                    cursor.close();*/
                   String path=paths.get(i);
                    File f=new File(path);

                    String mime="image/*";
                    String ext= MimeTypeMap.getFileExtensionFromUrl(path);
                    if(ext!=null)
                        mime=MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
                    form.addFile("image[]",f,mime);
                }
                form.addData("ListId","1111");
                form.addData("ListType","1");
                AsyncTask asyncTask=new AsyncTask() {
                    String result;

                    @Override
                    protected void onPostExecute(Object o) {
                        if(result.equals("-1")){

                            Toast.makeText(getContext(),"Error communicating with server",Toast.LENGTH_SHORT).show();

                        }
                        else
                        if(result!=null) {
                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                if (jsonObject.getBoolean("Isvalid") == true && jsonObject.getString("ResponseCode").equals("111") && jsonObject.getString("ResponseMessage").equals("Success")) {
                                    Toast.makeText(getContext(), "Upload Success", Toast.LENGTH_SHORT).show();
                                } else
                                    Toast.makeText(getContext(), "Upload Failed", Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                Toast.makeText(getContext(), "Invalid response from server, Upload may have failed!", Toast.LENGTH_SHORT).show();


                                e.printStackTrace();
                            }
                        }
                        popUpWindow.dismiss();
                        super.onPostExecute(o);
                    }

                    @Override
                    protected Object doInBackground(Object[] objects) {
                        try {
                            result=form.submit("http://menatservice.menatbb.com/MenaServiceCommon.svc/TestUploadImage");
                        } catch (IOException e) {
                            result="-1";
                            e.printStackTrace();
                        }
                        return null;
                    }
                };
                asyncTask.execute();
            }
        });

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==1){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
               readImages();
            }
        else{
                Toast.makeText(getContext(),"Permission denied!",Toast.LENGTH_SHORT).show();
            }
        }
        else
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if(requestCode==1 && resultCode== Activity.RESULT_OK){

     /////   Toast.makeText(getContext(),"1",Toast.LENGTH_LONG).show();
        if(data.getClipData()!=null){
            int count=data.getClipData().getItemCount();
     /////       Toast.makeText(getContext(),count+"..",Toast.LENGTH_SHORT).show();

            for(int i=0;i<paths.size();i++) {
                paths.remove(i);
            }

            ((LinearLayout)view.findViewById(R.id.img_list)).removeAllViews();
            for(int i=0;i<count;i++){
                Uri uri=data.getClipData().getItemAt(i).getUri();
                    String path="";
                    String[] p={MediaStore.MediaColumns.DATA};
                    Cursor cursor=getContext().getContentResolver().query(uri,p,null,null,null);
                    if(cursor.moveToFirst()){
                        int col_index=cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                        if(col_index==-1)
                            path=null;
                        else
                        path=cursor.getString(col_index);
                    }
                    cursor.close();

                if(path!=null) {
                    ((ConstraintLayout)view).removeView(view.findViewById(R.id.remove));
                    paths.add(path);
                    ImageView iv = new ImageView(getContext());
                    LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    layoutParams.setMargins(5,5,5,5);
                    iv.setLayoutParams(layoutParams);
                    Picasso.get().load(new File(path)).into(iv);
                    ((LinearLayout) view.findViewById(R.id.img_list)).addView(iv);
                }

            }
        }
        else if(data!=null){
            ((ConstraintLayout)view).removeView(view.findViewById(R.id.remove));

            for(int i=0;i<paths.size();i++) {
                paths.remove(i);
            }


            ((LinearLayout)view.findViewById(R.id.img_list)).removeAllViews();

            String path=null;
            String[] p={MediaStore.MediaColumns.DATA};
            Cursor cursor=getContext().getContentResolver().query(data.getData(),p,null,null,null);
            if(cursor.moveToFirst()){
                int col_index=cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                path=cursor.getString(col_index);
            }
            cursor.close();

            if(path!=null) {
                paths.add(path);
                ImageView iv = new ImageView(getContext());
                LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                layoutParams.setMargins(5,5,5,5);
                iv.setLayoutParams(layoutParams);
                Picasso.get().load(new File(path)).into(iv);
                ((LinearLayout) view.findViewById(R.id.img_list)).addView(iv);
            }

        }
    }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void readImages(){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Pick images"), 1);

    }
}