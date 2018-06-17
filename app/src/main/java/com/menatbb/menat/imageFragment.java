package com.menatbb.menat;


import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class imageFragment extends Fragment {

ImageView imageView;
    public imageFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        imageView = new ImageView(getActivity());
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Picasso.get().load(((Details) getActivity()).getLists().get(((Details) getActivity()).getRetVal())).into(imageView);
        return imageView;
    }
}
