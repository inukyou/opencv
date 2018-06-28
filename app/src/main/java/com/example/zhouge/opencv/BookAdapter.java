package com.example.zhouge.opencv;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {


    ArrayList<BookInfo> bookList;
    Activity activity;

    public BookAdapter(ArrayList<BookInfo> bookList, Activity activity)
    {
        this.activity=activity;
        this.bookList=bookList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.bookitem,parent,false);

        final ViewHolder holder=new ViewHolder(view);

        holder.ViewL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int p=holder.getAdapterPosition();
                BookInfo bookInfo=bookList.get(p);
                Intent intent=new Intent(view.getContext(),BookShowActivity.class);
                intent.putExtra("id",bookInfo.id);
                intent.putExtra("name",bookInfo.name);
                intent.putExtra("author",bookInfo.author);
                intent.putExtra("publish",bookInfo.publicName);
                Toast.makeText(view.getContext(),"wwww",Toast.LENGTH_LONG).show();
                view.getContext().startActivity(intent);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BookInfo bookInfo= bookList.get(position);

        holder.id=bookInfo.id;
        holder.name=bookInfo.name;
        holder.author=bookInfo.author;
        holder.publishName=bookInfo.publicName;

        holder.idView.setText("id:  "+bookInfo.id);
        holder.BookNameView.setText(bookInfo.name);
        holder.authorView.setText(bookInfo.author);
        holder.publishView.setText(bookInfo.publicName);

    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        int id=0;
        String name="",author="",publishName="";

        View ViewL;
        ImageView imageView;
        TextView idView,BookNameView,authorView,publishView;

        public ViewHolder(View itemView) {
            super(itemView);
            ViewL=itemView;


            imageView=(ImageView)itemView.findViewById(R.id.bookimage);
            idView=(TextView)itemView.findViewById(R.id.bookid);
            BookNameView=(TextView)itemView.findViewById(R.id.bookname);
            authorView=(TextView)itemView.findViewById(R.id.bookauthor);
            publishView=(TextView)itemView.findViewById(R.id.bookpublish);

        }
    }

}
