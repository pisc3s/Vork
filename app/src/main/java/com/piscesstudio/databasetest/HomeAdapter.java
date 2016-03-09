package com.piscesstudio.databasetest;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {
    private static List<User> data;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userid,fn,ln;
        ViewHolder(View v) {
            super(v);
            userid = (TextView) v.findViewById(R.id.userid);
            fn = (TextView) v.findViewById(R.id.fn);
            ln = (TextView) v.findViewById(R.id.ln);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(v.getContext(), EditActivity.class);
                    i.putExtra(Intent.EXTRA_TEXT, data.get(getAdapterPosition()).getUserID());
                    v.getContext().startActivity(i);
                }
            });
        }
    }

    public HomeAdapter(Context context, List<User> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public HomeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_home, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.userid.setText(data.get(position).getUserID());
        holder.fn.setText(data.get(position).getFirstName());
        holder.ln.setText(data.get(position).getLastName());
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void refreshAdapter(List<User> user) {
        data.clear();
        data.addAll(user);
        notifyDataSetChanged();
    }
}

