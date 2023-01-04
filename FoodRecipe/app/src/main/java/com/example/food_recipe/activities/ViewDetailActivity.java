package com.example.pj_off.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.pj_off.R;
import com.example.pj_off.databinding.ActivityViewDetailBinding;
import com.squareup.picasso.Picasso;

public class ViewDetailActivity extends AppCompatActivity {

    ActivityViewDetailBinding viewDetailBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewDetailBinding= DataBindingUtil.setContentView(this,R.layout.activity_view_detail);

        viewDetailBinding.textTitle.setText(getIntent().getStringExtra("title"));
        viewDetailBinding.type.setText(getIntent().getStringExtra("imageType"));
        Picasso.get().load(getIntent().getStringExtra("image")).into(viewDetailBinding.imgHeadline);

    }
}