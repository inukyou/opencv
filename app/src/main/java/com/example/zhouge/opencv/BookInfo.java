package com.example.zhouge.opencv;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public final class BookInfo {

    int id;
    String name="";
    String author="";
    String publicName="";


    public static ArrayList<BookInfo> JSONtoBookInfoList(String JSONString)
    {

        ArrayList<BookInfo> bookList=new ArrayList<BookInfo>();
        try {
            JSONArray jarry=new JSONArray(JSONString);
            for(int i=0;i<jarry.length();i++)
            {
                JSONObject jobj=new JSONObject();
                BookInfo bookInfo=new BookInfo();
                bookInfo.id=jobj.getInt("id");
                bookInfo.name=jobj.getString("BookName");
                bookInfo.author=jobj.getString("author");
                bookInfo.publicName=jobj.getString("publish");
                bookList.add(bookInfo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return bookList;
        }

        return bookList;
    }

}
