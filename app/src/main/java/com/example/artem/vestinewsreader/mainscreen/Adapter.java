package com.example.artem.vestinewsreader.mainscreen;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.artem.vestinewsreader.R;
import com.example.artem.vestinewsreader.database.Article;
import java.util.ArrayList;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.NewViewHolder> {

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    private Context context;
    private List<Article> articles = new ArrayList();
    private OnItemClickListener mListener;

    public Adapter(Context context, List<Article> articles){
        this.context = context;
        this.articles = articles;
    }

    public static class NewViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView date;

        public NewViewHolder(View viewItem, final OnItemClickListener listener) {
            super(viewItem);
            title = itemView.findViewById(R.id.item_view_holder__titleTxt);
            date = itemView.findViewById(R.id.item_view_holder__articleDate);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onBindViewHolder(NewViewHolder holder, int position) {
        Article currentItem = articles.get(position);

        String date = currentItem.getDate();
        String title = currentItem.getTitle();
        holder.date.setText(date);
        holder.title.setText(title);
    }

    @Override
    public NewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_view_holder,parent,false);
        NewViewHolder newViewHolder = new NewViewHolder(view,mListener);
        return newViewHolder;
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }
}
