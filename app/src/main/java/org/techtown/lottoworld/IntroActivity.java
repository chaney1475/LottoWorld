package org.techtown.lottoworld;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class IntroActivity extends AppCompatActivity {

    static public List<NumberQuery> numberQueryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //인트로를 보여준 후 intent 를 사용해서
                //MainActivity 로 넘어가도록 함
                loadDB();


                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                finish();
            }
            //2초 딜레이 후 Runner 객체 실행
        },1500);
    }

    @Override
    protected void onPause(){
        super.onPause();
        finish();
    }
    public void loadDB(){
        try {
            DataAdapter mDbHelper = new DataAdapter(getApplicationContext());
            mDbHelper.open();

            // db에 있는 값들을 model을 적용해서 넣는다.
            numberQueryList = mDbHelper.getWinningData();
            Collections.reverse(numberQueryList);
            // db 닫기
            mDbHelper.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}