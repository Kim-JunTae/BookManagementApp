package com.example.user.bookmanagementapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class NotesDBAdapter extends BaseAdapter {

    private ArrayList<NoteDO> noteList = new ArrayList<NoteDO>();   //기본 10개의 가변 배열 생성
    Context mContext;   // Application Level에서 System Level에 접근하기 위해서

    //생성자 구현 : 생성자 생성시 context를 받아서 DataBase에 접근
    public NotesDBAdapter(Context mContext){
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return noteList.size();
    }

    @Override
    public Object getItem(int position) {
        return noteList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater =
                (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.note_row, null);

        TextView textID = (TextView) view.findViewById(R.id.text_id);
        TextView textBook = (TextView) view.findViewById(R.id.text_book);
        TextView textWriter = (TextView) view.findViewById(R.id.text_writer);

        textID.setText(noteList.get(position).getId());
        textBook.setText(noteList.get(position).getBook());
        textWriter.setText(noteList.get(position).getWriter());

        return view;
    }//end getView()

    //사용자 정의 메소드 1 : 추가
    public void addData(String id, String book, String writer){
        NoteDO notedo = new NoteDO();

        notedo.setId(id);
        notedo.setBook(book);
        notedo.setWriter(writer);

        noteList.add(notedo);
    }//end addData()

    //사용자 정의 메소드2 : 삭제
    public void removeData(String id){
        for(int i=0; i<noteList.size(); i++){
            if(noteList.get(i).getId().equals(id)){
                noteList.remove(i);
            }
        }
    }

    //내부 클래스 구현 : NoteDO, POJO
    public class NoteDO{
        //중간 저장소 : 프로퍼티
        private String id;
        private String book;
        private String writer;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getBook() { return book; }
        public void setBook(String book) { this.book = book; }

        public String getWriter() { return writer; }
        public void setWriter(String writer) { this.writer = writer; }

    }//end NoteDO 클래스

}//end NotesDBAdapter
