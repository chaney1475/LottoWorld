package org.techtown.lottoworld;

import static org.techtown.lottoworld.IntroActivity.numberQueryList;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NumAnalysisActivity extends AppCompatActivity {
    int pages; // 전체 페이지 수
    int totalItem;
    int page = 0; // 현재 페이지

    List<WinningHistory> historyList = new ArrayList<>();
    List<NumberQuery> first = new ArrayList<>();
    List<NumberQuery> second = new ArrayList<>();
    List<NumberQuery> third = new ArrayList<>();
    List<NumberQuery> fourth = new ArrayList<>();
    List<NumberQuery> fifth = new ArrayList<>();

    TextView winningNum;
    TextView total;
    TextView even;



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_num_analysis);

        // 번호를 불러오는 코드로 대체될 예정
        int[] nums = {5, 16, 28, 30, 35, 45, -1};

        winningNum = findViewById(R.id.winningNum);
        total = findViewById(R.id.total);
        even = findViewById(R.id.analysis);

        NumberQuery numberQuery = new NumberQuery();
        numberQuery.setNums(nums);

        Date dateNow = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-M-dd", Locale.getDefault());
        String date = format.format(dateNow);

        total.setText("총합:" + numberQuery.getTotal());
        even.setText("짝홀:" + numberQuery.getEven()+"/"+( 6 - numberQuery.getEven()) );
        winningNum.setText(numberQuery.numberString());

        //history 리스트를 만들음
        compareNums(nums);
        addWinningNums();

        totalItem = historyList.size();

        if(totalItem % 10 == 0){ // 전체 페이지 계산
            pages = totalItem / 10;
        }else{  pages = totalItem / 10 + 1; }


        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertData(date, numberQuery);
            }
        });

        RecyclerView recyclerView2 = findViewById(R.id.recyclerView2);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        recyclerView2.setLayoutManager(layoutManager);

        NumAnalysisAdapter adapter = new NumAnalysisAdapter();

        addNumItem(adapter);

        recyclerView2.setAdapter(adapter);

        recyclerView2.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int totalCount = recyclerView.getAdapter().getItemCount();

                if(lastPosition == totalCount -1 ){
                    //아이템 추가!
                    addNumItem(adapter);
                    adapter.notifyDataSetChanged();}
            }
        });


    }

    public void compareNums(int[] nums){

        int cnt; // 같은 숫자의 갯수

        for(NumberQuery wNum : numberQueryList){
            cnt = 0;
            int win_idx, cmp_idx;
            win_idx = cmp_idx = 0;

            while(win_idx <= 5 && cmp_idx <= 5){
                if(wNum.getNums()[win_idx] == nums[cmp_idx]){
                    cnt++;
                    win_idx++;
                    cmp_idx++;
                }else if(wNum.getNums()[win_idx] < nums[cmp_idx]){
                    win_idx++;
                }else{
                    cmp_idx++;
                }
            }
            setRank(cnt, wNum, nums);
        }
    }
    public void setRank(int cnt, NumberQuery wNum, int[] nums ){ // 등수 지정해주는 메소드

        switch(cnt){
            case 6 : // 6개 다 맞을 때 1등
                first.add(wNum);
                break;
            case 5: // 보너스 번호를 포함한 경우 2등, 아 경우 3등
                if(Arrays.asList(nums).contains(wNum.getNums()[6])){
                    second.add(wNum);
                }
                else{
                    third.add(wNum);
                }
                break;
            case 4:// 4개를 맞춘 경우 4등
                fourth.add(wNum);
                break;
            case 3: // 3개를 맞춘 경우 5등
                fifth.add(wNum);
                break;
            default:
                break;
        }
        // 각각의 등수들 회차 빠른 순으로 정렬
        Collections.sort(first);
        Collections.sort(second);
        Collections.sort(third);
        Collections.sort(fourth);
        Collections.sort(fifth);
    }

    public void addNumItem(NumAnalysisAdapter adapter){

        int start = page * 10;
        int end;

        if( totalItem < (page + 1) * 10){
            end = totalItem;
        }else{
            end = (page + 1) * 10;
        }

        for(int i = start; i < end; i++){
            adapter.addItem(historyList.get(i));

        }
        page ++;

    }
    public void addWinningNums(){
        for(NumberQuery wn : first){
            historyList.add(new WinningHistory(wn,1));
        }
        for(NumberQuery wn : second){
            historyList.add(new WinningHistory(wn,2));
        }
        for(NumberQuery wn : third){
            historyList.add(new WinningHistory(wn,3));
        }
        for(NumberQuery wn : fourth){
            historyList.add(new WinningHistory(wn,4));
        }
        for(NumberQuery wn : fifth){
            historyList.add(new WinningHistory(wn,5));
        }
    }
    public void insertData(String date, NumberQuery wn){
        try {
            DataAdapter mDbAdapter = new DataAdapter(getApplicationContext());
            mDbAdapter.open();

            mDbAdapter.insertWinningNum(date, wn);

            // db 닫기
            mDbAdapter.close();
            Log.d("insertData", "성공함");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.d("insertData", "실패함");
        }
    }
}