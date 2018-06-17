package com.menatbb.menat;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.AppCompatImageView;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Details extends AppCompatActivity {

    private ArrayList<String> lists = new ArrayList<>();
    private int retVal = 0;
    private AppCompatImageView[] radioButtons;
    LinearLayout linearLayout;

    public ArrayList<String> getLists() {
        return lists;
    }

    public int getRetVal() {
        retVal++;
        if (retVal == lists.size())
            retVal = 1;
        return (retVal - 1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ViewPager viewPager = findViewById(R.id.image_switcher);
        Adapter adapter = new Adapter(getSupportFragmentManager());
        linearLayout = findViewById(R.id.radio_group);
        String details = getIntent().getStringExtra("details");
        setSupportActionBar((Toolbar) findViewById(R.id.action_bar));
        // ((Toolbar) findViewById(R.id.action_bar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        try {
            JSONObject jsonObject = new JSONObject(details);
            JSONArray images = jsonObject.getJSONArray("ImagesList");
            radioButtons = new AppCompatImageView[images.length()];
            if(images.length()>0)
            for (int j = 0; j < images.length(); j++) {
                radioButtons[j] = new AppCompatImageView(getApplicationContext());
                radioButtons[j].setClickable(false);
                radioButtons[j].setPadding(5,5,5,5);
                linearLayout.addView(radioButtons[j]);
                if (j == 0)
                    radioButtons[j].setImageResource(R.drawable.radio_button_selected);
                else
                    radioButtons[j].setImageResource(R.drawable.radio_button_unselected);
                // Toast.makeText(getApplicationContext(),j+"",Toast.LENGTH_SHORT).show();
                //Picasso.get().load(images.getString(j)).into(imageFragment1.getImageView());
                lists.add(images.getString(j));
                imageFragment imageFragment1 = new imageFragment();
                adapter.addFragment(imageFragment1, j + "");

            }
            else
                viewPager.setVisibility(View.GONE);

            ((TextView) findViewById(R.id.title)).setText(jsonObject.getString("BusinessTitle"));
            ((TextView) findViewById(R.id.summary)).setText(jsonObject.getString("Summary"));
            ((TextView) findViewById(R.id.asking_price1)).setText(jsonObject.getString("AskingPrice"));
            ((TextView) findViewById(R.id.country1)).setText(jsonObject.getString("CountryName"));
            ((TextView) findViewById(R.id.annual_sales1)).setText(jsonObject.getString("AnnualSales"));
            ((TextView) findViewById(R.id.business_type1)).setText(jsonObject.getString("BusinessType"));
            JSONArray categoryList=jsonObject.getJSONArray("CategoryList");
            String temp="";
            for(int i=0;i<categoryList.length();i++){
                JSONObject category=categoryList.getJSONObject(i);
                if(i!=0)
                    temp+="/";
                temp+=category.getString("CategoryName");
            }
            ((TextView) findViewById(R.id.category1)).setText("temp");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for(int i=0;i<radioButtons.length;i++)
                    radioButtons[i].setImageResource(R.drawable.radio_button_unselected);
                radioButtons[position].setImageResource(R.drawable.radio_button_selected);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}