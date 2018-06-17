package com.menatbb.menat;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class MScrollView extends ScrollView {
    Listing listing;
    public MScrollView(Context context) {
        super(context);
    }

    public MScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if(listing!=null){
            if(listing.l.getBottom()-(getHeight()+getScrollY())==0)
                listing.netJob();
        }
    }

    public void setListing(Listing listing) {
        this.listing = listing;
    }
}