package com.example.zhouge.opencv;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {


    ArrayList<BookInfo> bookList;
    public BookAdapter(ArrayList<BookInfo> bookList)
    {
        this.bookList=bookList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.bookitem,parent,false);

        ViewHolder holder=new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BookInfo bookInfo= bookList.get(position);
        holder.idView.setText(bookInfo.id+"");
        holder.BookNameView.setText(bookInfo.name);
        holder.authorView.setText(bookInfo.author);
        holder.publishView.setText(bookInfo.publicName);
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView idView,BookNameView,authorView,publishView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView=(ImageView)itemView.findViewById(R.id.bookimage);
            idView=(TextView)itemView.findViewById(R.id.bookid);
            BookNameView=(TextView)itemView.findViewById(R.id.bookname);
            authorView=(TextView)itemView.findViewById(R.id.bookauthor);
            publishView=(TextView)itemView.findViewById(R.id.bookpublish);

        }
    }

}
