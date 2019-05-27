package cosmetic.com.cosmetictable_server;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cosmetic.com.cosmetictable_server.R;

public class SearchActivity extends AppCompatActivity {

    private ListView listView;
    List fileList = new ArrayList<>();
    List urlList = new ArrayList<>();

    ArrayAdapter adapter;
    static boolean calledAlready = false;

    /*Day 날짜 개산 변수 */
    int day = 365;
    private int tYear;           //오늘 연월일 변수
    private int tMonth;
    private int tDay;

    private long d;
    private long t;
    private long r;

    private int resultNumber = 0;

    static final int DATE_DIALOG_ID=0;



    /*DB 업데이트 */
    String name;
    String image;
    int cnt=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        if (!calledAlready)
        {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true); // 다른 인스턴스보다 먼저 실행되어야 한다.
            calledAlready = true;
        }

        listView= (ListView)  findViewById(R.id.lv_fileList);

        adapter = new ArrayAdapter<String>(this, R.layout.listitem, fileList);
        listView.setAdapter(adapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseRef = database.getReference("bbs");

        // Read from the database
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                adapter.clear();

                // 클래스 모델이 필요?
                for (DataSnapshot fileSnapshot : dataSnapshot.getChildren()) {
                    String str = fileSnapshot.child("title").getValue(String.class);
                    //name=str;
                    String url = fileSnapshot.child("image").getValue(String.class);
                    //image = url;
                    Log.i("TAG: value is ", str);
                    fileList.add(str);
                    urlList.add(url);

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG: ", "Failed to read value", databaseError.toException());
            }
        });
        listView.setOnItemClickListener(new ListViewItemClickListener());




        // 검색 필터링
        EditText editTextFilter = (EditText)findViewById(R.id.editTextFilter) ;
        editTextFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable edit) {
                // TODO : item filtering

                String filterText = edit.toString();
                if (filterText.length() > 0 ) {
                    listView.setFilterText(filterText);
                } else {
                    listView.clearTextFilter();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        }) ;



    }//onCreate 종료





    //리스트 클릭시 팝업창 뜨게 하기
    private void DialogSelectOption() {
        day = 365;
        final String items[] = { "새로운 아이템", "기존 아이템"};
        AlertDialog.Builder ab = new AlertDialog.Builder(SearchActivity.this);
        ab.setTitle("사용기한 등록");


        ab.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // 각 리스트를 선택했을때
                switch (whichButton) {
                    case 1:
                        day = day + DialogDatePicker();
                        break;
                }
            }
        }).setPositiveButton("등록", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // OK 버튼 클릭시 ,
                // 1. 사용기한과 url을 서버에 올리기
                Toast.makeText(SearchActivity.this, "사용기한이 " + day + "일 남았습니다.", Toast.LENGTH_SHORT).show();

                //유저의 정보를 가져오는 메소드
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                //데이터베이스에 값을 넣을 준비 메소드
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference databaseuser = database.getReference("user");

                if (user != null) {
                    if (cnt==0) {

                        databaseuser.child(user.getUid()).setValue(name);
                        databaseuser.child(user.getUid()).child(name).child("leftday").setValue(day);
                        databaseuser.child(user.getUid()).child(name).child("image").setValue(image);
                        cnt++;

                    } else if(cnt!=0) {
                        databaseuser.child(user.getUid()).child(name).child("leftday").setValue(day);
                        databaseuser.child(user.getUid()).child(name).child("image").setValue(image);

                    }
                }

                // 2. 메인 activity로 넘기기
                Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                intent.putExtra("image_url", image);
                startActivity(intent);


            }
        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Cancel 버튼 클릭시
                dialog.dismiss();
            }
        });
        ab.show();
    }

    private int DialogDatePicker(){
        Calendar c = Calendar.getInstance();
        int cyear = c.get(Calendar.YEAR);
        int cmonth = c.get(Calendar.MONTH);
        int cday = c.get(Calendar.DAY_OF_MONTH);
        int daycount = 0;

        DatePickerDialog.OnDateSetListener mDateSetListener =
                new DatePickerDialog.OnDateSetListener() {
                    // onDateSet method
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String date_selected = String.valueOf(year)+
                                " / "+String.valueOf(monthOfYear+1)+" / "+String.valueOf(dayOfMonth);
                        Toast.makeText(SearchActivity.this,
                                "사용 시작 날짜 : "+date_selected, Toast.LENGTH_SHORT).show();
                    }
                };
        daycount = countdday(cyear, cmonth, cday);
        //Toast.makeText(this, "dday ======= " + daycount, Toast.LENGTH_SHORT).show();
        DatePickerDialog alert = new DatePickerDialog(this,  mDateSetListener,
                cyear, cmonth, cday);

        alert.show();

        return daycount;
    }

    public int countdday(int myear, int mmonth, int mday) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

            Calendar todaCal = Calendar.getInstance(); //오늘날자 가져오기
            Calendar ddayCal = Calendar.getInstance(); //오늘날자를 가져와 변경시킴

            mmonth -= 1; // 받아온날자에서 -1을 해줘야함.
            ddayCal.set(myear,mmonth,mday);// D-day의 날짜를 입력
            Log.e("테스트",simpleDateFormat.format(todaCal.getTime()) + "");
            Log.e("테스트",simpleDateFormat.format(ddayCal.getTime()) + "");

            long today = todaCal.getTimeInMillis()/86400000; //->(24 * 60 * 60 * 1000) 24시간 60분 60초 * (ms초->초 변환 1000)
            long dday = ddayCal.getTimeInMillis()/86400000;
            long count = dday - today; // 오늘 날짜에서 dday 날짜를 빼주게 됩니다.
            return (int) count;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return -1;
        }
    }



    private class ListViewItemClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            DialogSelectOption();

            String temp = (String) fileList.get(position);
            name=temp;
            String temp2 = (String) urlList.get(position);
            image = temp2;
        }
    }


    //url 를 bitmap 으로 변경
    public Bitmap getBitmapFromURL(String src) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(src);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true); connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }finally{
            if(connection!=null)connection.disconnect();
        }
    }



}
