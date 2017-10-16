package com.hooooong.jsondata;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.hooooong.jsondata.model.User;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private ArrayList userList;

    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        load();
    }

    private void load(){
        new AsyncTask<Void, Void,String>(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected String doInBackground(Void... voids) {
                return Remote.getData("https://api.github.com/users");
            }

            @Override
            protected void onPostExecute(String jsonString) {
                progressBar.setVisibility(View.GONE);
                //JSONString 을 Parsing 하여 List 에 넣어둔다.
                gsonParse(jsonString);
                setList();
            }

        }.execute();
    }

    /**
     * 수동으로 JSON 을 Parsing 하는 메소드
     *
     * @param jsonString
     */
    private void parse(String jsonString){
        userList = new ArrayList<>();
        // 앞에 문자 2개 없애기 [, {
        jsonString = jsonString.substring(jsonString.indexOf("{")+1);
        // 뒤에 문자 2개 없애기 }, ]
        jsonString = jsonString.substring(0, jsonString.lastIndexOf("}"));

        // 문자열 분리하기
        String array[] = jsonString.split("\\},\\{");

        for(String item : array){
            User user = new User();
            // item 문자열을 분리해서 user 의 변수로 넣는다.
            String subArray[] = item.split(",");

            HashMap<String, String> hashItem = new HashMap<>();

            for(String subItem : subArray){
                subItem = subItem.substring(subItem.indexOf("\"")+1);
                String temp[] = subItem.split("\":");
                if(temp[1].startsWith("\"")){
                    temp[1] = temp[1].substring(1,temp[1].lastIndexOf("\""));
                }
                hashItem.put(temp[0], temp[1]);
            }

            user.setId(Integer.parseInt(hashItem.get("id")));
            user.setLogin(hashItem.get("login"));
            user.setAvatar_url(hashItem.get("avatar_url"));

            userList.add(user);
        }
    }

    /**
     * GSON Library 를 사용하여, JSON 을 Parsing 하는 메소드 (하나의 문단)
     *
     * @param jsonString
     */
    private void gsonParse(String jsonString){
        userList = new ArrayList<>();
        jsonString = jsonString.substring(2, jsonString.length()-3);
        // 문자열 분리하기
        String array[] = jsonString.split("\\},\\{");
        Gson gson = new Gson();
        for(String item : array) {
            item = "{" + item + "}";
            User user = gson.fromJson(item, User.class);
            userList.add(user);
        }
    }

    /**
     * GSON Library 를 사용하여, JSON 을 Parsing 하는 메소드 (전체 JSON)
     *
     * @param jsonString
     */
    /*
    private void simpleGsonParse(String jsonString){
        userList = new ArrayList<>();
        Gson gson = new Gson();
        userList = gson.toJson(jsonString);
    }
    */

    private void setList(){
        ListAdapter listAdapter = new ListAdapter(userList);
        recyclerView.setAdapter(listAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

}
