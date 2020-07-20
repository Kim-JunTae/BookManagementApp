package com.example.user.bookmanagementapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements Button.OnClickListener {
    //맴버 변수 선언

    //SQLite 관련 변수
    DBHelper        helper;
    SQLiteDatabase  db;

    //View 관련 변수
    EditText        edit_book, edit_writer;
    Button          btn_add, btn_edit, btn_delete, btn_search, btn_bookList;

    //ListView 관련 변수
    ListView        list;
    NotesDBAdapter  adapter;
    String          book_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //DB 설정
        helper = new DBHelper(MainActivity.this);
        try {
            //mybooklists.db에 "쓰기"가 가능하도록 DB 오픈
            db = helper.getWritableDatabase();
        }catch (SQLiteException e){
            db = helper.getReadableDatabase();
        }

        edit_book = (EditText)findViewById(R.id.edit_book);
        edit_writer = (EditText)findViewById(R.id.edit_writer);

        btn_add = (Button)findViewById(R.id.btn_add);
        btn_edit = (Button)findViewById(R.id.btn_edit);
        btn_delete = (Button)findViewById(R.id.btn_delete);
        btn_search = (Button)findViewById(R.id.btn_search);
        btn_bookList = (Button)findViewById(R.id.btn_bookList);

        //버튼 이벤트 연결
        btn_add.setOnClickListener(this);
        btn_edit.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
        btn_search.setOnClickListener(this);
        btn_bookList.setOnClickListener(this);

        //ListView
        list = (ListView)findViewById(R.id.list);
        adapter = new NotesDBAdapter(this);

        //DB에 있는 데이터들 cursor를 통해 가져와서 adapter를 통해서 ArrayList 배열에 넣는다.
        Cursor cursor = db.rawQuery("select * from bookTable;", null);

        while (cursor.moveToNext()){
            adapter.addData(cursor.getString(0),
                            cursor.getString(1),
                            cursor.getString(2));
        }

        //어댑터 연결
        list.setAdapter(adapter);

        //ListView에 대한 이벤트 처리
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NotesDBAdapter.NoteDO notedo = (NotesDBAdapter.NoteDO)adapter.getItem(position);
                book_id = notedo.getId();
                edit_book.setText(notedo.getBook());
                edit_writer.setText(notedo.getWriter());
            }
        });

    }//end onCreate


    //버튼에 대한 이벤트 처리
    @Override
    public void onClick(View v) {
        String book = edit_book.getText().toString();
        String writer = edit_writer.getText().toString();
        Cursor cursor;

        switch (v.getId()){
            case R.id.btn_add :  //"추가" 버튼에서 이벤트 발생인 경우
                db.execSQL("insert into bookTable values(null, '" + book + "', '" + writer + "');");

                Toast.makeText(MainActivity.this, book+" 추가되었습니다", Toast.LENGTH_SHORT).show();

                updateListView();
                break;

            case R.id.btn_edit :    //"수정" 버튼
                db.execSQL("update bookTable set book ='" + book + "', writer='" + writer + "' where id='"+ book_id + "';");

                Toast.makeText(MainActivity.this, book+" 수정되었습니다", Toast.LENGTH_SHORT).show();

                updateListView();
                break;

            case R.id.btn_delete :  //"삭제" 버튼
                db.execSQL("delete from bookTable where id='" + book_id +"';");

                Toast.makeText(MainActivity.this, book+" 삭제되었습니다", Toast.LENGTH_SHORT).show();

                adapter.removeData(book_id);
                updateListView();
                break;

            case R.id.btn_search :  //"검색" 버튼

                cursor = db.rawQuery("select * from bookTable where book like '%" + book + "%' order by id asc;",null);

                //OR  writer like '%" + writer + "%' 어떻게 하면 둘다 될지 고민
                //Toast.makeText(MainActivity.this, writer+" 작가의 책은 다음과 같습니다.", Toast.LENGTH_SHORT).show();

                adapter = new NotesDBAdapter(this);
                list.setAdapter(adapter);

                while (cursor.moveToNext()){
                    adapter.addData(cursor.getString(0),
                            cursor.getString(1),
                            cursor.getString(2));
                }

                //어뎁터에 변경을 통보
                adapter.notifyDataSetChanged();

                //사용자 UX
                edit_book.setText(null);
                edit_writer.setText(null);
                edit_book.requestFocus();
                break;

            case R.id.btn_bookList :    //"목록보기" 버튼
                updateListView();

        }//end switch

    }//end Onclick()


    //반복되는 ListView 새로고침를 사용자 정의 메소드로 구현 - 검색은 못씀
    private void updateListView() {
        //ListView에 추가
        adapter = new NotesDBAdapter(this);
        list.setAdapter(adapter);

        Cursor cursor = db.rawQuery("select * from bookTable;", null);

        while (cursor.moveToNext()){
            adapter.addData(cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2));
        }

        //어뎁터에 변경을 통보
        adapter.notifyDataSetChanged();

        //사용자 UX
        edit_book.setText(null);
        edit_writer.setText(null);
        edit_book.requestFocus();

    }//end updateListView()


    //내부 클래스 구현: 데이터 베이스
    class DBHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "bookDBmanage.db";
        private static final int DATABASE_VERSION = 2;

        //생성자
        public DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            //테이블 생성
            db.execSQL("create table bookTable(id INTEGER primary key autoincrement, book TEXT, writer TEXT);");
        }//end onCreate()

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table if exists bookTable");
            onCreate(db);
        }//end onUpgrade()
    }//end DBHelper 클래스 : 내부 클래스

}//end MainActivity 클래스 : 외부 클래스
