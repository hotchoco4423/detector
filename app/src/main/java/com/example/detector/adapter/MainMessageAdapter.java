package com.example.detector.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Parcelable;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.detector.MainActivity;
import com.example.detector.R;
import com.example.detector.SubActivity;
import com.example.detector.model.MainMessage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainMessageAdapter extends RecyclerView.Adapter<MainMessageAdapter.ViewHolder> {

    public static final String TAG = "MESSAGE_ADAPTER";

    private ArrayList<MainMessage> mList;
    private Context mContext;

    public MainMessageAdapter(Context context) {
        this.mContext = context;
        mList = new ArrayList<MainMessage>();
    }

    @NonNull
    @Override
    public MainMessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_message_item, parent, false);
        return new MainMessageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder");
        MainMessage message = mList.get(position);

        GradientDrawable drawable = (GradientDrawable) ContextCompat.getDrawable(mContext, R.drawable.msg_background);

        switch (message.getResult()) {
            case "존재하지 않는 URL":
                drawable.setColor(Color.parseColor("#D98CE6"));
                break;
            case "위험":
                drawable.setColor(Color.parseColor("#E49594"));
                break;
            case "안전":
                drawable.setColor(Color.parseColor("#6CCF70"));
                break;
            default:
                break;
        }
        holder.layout.setBackground(drawable);
        holder.result.setText(message.getResult());
        holder.name.setText(message.getSender());
        holder.contents.setText(message.getMessage());
        holder.time.setText(message.getTime());
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount");
        return (mList == null) ? 0 : mList.size();
    }

    public void setList(ArrayList<MainMessage> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    public void setItem(MainMessage message) {
        mList.add(message);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView result;
        TextView name;
        TextView contents;
        TextView time;

        LinearLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "ViewHolder");

            layout = (LinearLayout) itemView.findViewById(R.id.item_layout);

            result = itemView.findViewById(R.id.result);
            name = itemView.findViewById(R.id.name);
            contents = itemView.findViewById(R.id.contents);
            time = itemView.findViewById(R.id.time);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    Log.d(TAG, "POS : " + pos);
                    Intent intent = new Intent(mContext, SubActivity.class);
                    intent.putExtra("item", (Serializable) mList.get(pos));
                    mContext.startActivity(intent);
                }
            });
        }
    }
}
