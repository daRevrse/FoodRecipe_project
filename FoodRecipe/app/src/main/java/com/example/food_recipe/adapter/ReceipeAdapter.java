package com.example.pj_off.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pj_off.R;
import com.example.pj_off.intfc.FetchDataListener;
import com.example.pj_off.intfc.ViewDetail;
import com.example.pj_off.model.Result;
import com.squareup.picasso.Picasso;

import java.util.List;


public class ReceipeAdapter extends RecyclerView.Adapter<ReceipeAdapter.myHolder> {

    private Context context;
    private List<Result> headlines;
    private FetchDataListener listener;
    public ViewDetail viewDetail;


    public ReceipeAdapter(Context context, List<Result> headlines, FetchDataListener listener, ViewDetail viewDetail) {
        this.context = context;
        this.headlines = headlines;
        this.listener = listener;
        this.viewDetail = viewDetail;
    }

    @NonNull
    @Override
    public myHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new myHolder(LayoutInflater.from(context).inflate(R.layout.data, parent, false));

    }


    @Override
    public void onBindViewHolder(@NonNull myHolder holder, int position) {

        holder.text_title.setText(headlines.get(position).getTitle());
        Picasso.get().load(headlines.get(position).getImage()).into(holder.img_headline);

    }

    @Override
    public int getItemCount() {
        return headlines.size();
    }


    public class myHolder extends RecyclerView.ViewHolder {
        TextView text_title;
        ImageView img_headline;
        CardView cardView;

        TextView linearLayout;

        public myHolder(@NonNull View itemView) {
            super(itemView);

            text_title = itemView.findViewById(R.id.text_title);
            img_headline = itemView.findViewById(R.id.img_headline);
            cardView = itemView.findViewById(R.id.main_container);
//
            linearLayout = itemView.findViewById(R.id.viewDetail_layout);

            linearLayout.setOnClickListener(view -> {
                viewDetail.viewItemDetail(headlines.get(getAbsoluteAdapterPosition()));
            });
        }
    }


}

