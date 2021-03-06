package com.moy.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.Window;
import android.widget.Toast;

import com.moy.database.MySqliteOpenHelper;
import com.moy.database.UserDao;
import com.moy.handler.LoginHandler;
import com.moy.pojo.UserPOJO;

import org.json.JSONException;
import org.json.JSONObject;

import static com.moy.util.MyApplication.setUser;

/**
 * Created by Administrator on 2017/10/7.
 */

public class SplashActivity extends Activity {

    private Context mContext;
    private String phoneNum;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()   // or .detectAll() for all detectable problems
                .penaltyLog()
                .build());
        //设置虚拟机的策略
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                //.detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash);

        mContext=this;

        SharedPreferences sp = getSharedPreferences("Auto_Login",Context.MODE_PRIVATE);
        phoneNum = sp.getString("username","");
        password = sp.getString("password","");

        initDB();
        login();

    }

    public void initDB(){
        MySqliteOpenHelper mySqliteOpenHelper = new MySqliteOpenHelper(mContext);
        SQLiteDatabase db = mySqliteOpenHelper.getReadableDatabase();
        db.close();
    }

    public void login(){
        Intent menu_intent = new Intent(mContext, MenuActivity.class);
        if(phoneNum.equals("")||password.equals("")){
            Intent intent = new Intent(mContext,LoginActivity.class);
            startActivity(intent);
            SplashActivity.this.finish();
            return;
        }
        String result = LoginHandler.login(phoneNum,password);
        try{
            JSONObject json = new JSONObject(result);
            //JSON一种轻量级的数据交换格式
            if(json.getString("message").equals("fail")){
                Intent intent = new Intent(mContext,LoginActivity.class);
                startActivity(intent);
                SplashActivity.this.finish();
            }else if(json.getString("message").equals("success")){
                Toast.makeText(SplashActivity.this, "登陆成功", Toast.LENGTH_LONG).show();
                UserPOJO user = new UserPOJO();
                user.setUserId(json.getInt("userId"));
                user.setUserName(json.getString("userName"));
                user.setPassword(json.getString("password"));
                user.setPhoneNum(json.getString("phoneNum"));
                if((json.getString("sex").equals("1"))){
                    user.setSex("男");
                }else if((json.getString("sex").equals("0"))){
                    user.setSex("女");
                }
                user.setAge(json.getString("age"));
                user.setHeight(json.getString("height"));
                user.setWeight(json.getString("weight"));
                setUser(user);

                SharedPreferences sp = getSharedPreferences("Auto_Login",Context.MODE_PRIVATE);
                sp.edit().putString("username",user.getPhoneNum()).putString("password",user.getPassword()).apply();

                ContentValues values = new ContentValues();
                values.put("username",user.getUserName());
                values.put("password",user.getPassword());
                values.put("phonenum",user.getPhoneNum());
                values.put("sex",user.getSex());
                values.put("age",user.getAge());
                values.put("height",user.getHeight());
                values.put("weight",user.getWeight());
                UserDao userDao = new UserDao(mContext);
                Cursor cursor = userDao.quard("users",new String[]{"phonenum"},user.getPhoneNum());
                if(cursor != null && cursor.getCount()>0){
                    long UpdateResult = userDao.update("users",values,user.getPhoneNum());
                    if(UpdateResult == 1){
                        startActivity(menu_intent);
                        SplashActivity.this.finish();
                    }else{
                        Toast.makeText(mContext, "错误代码:"+UpdateResult, Toast.LENGTH_SHORT).show();
                    }
                }else{
                    boolean AddResult = userDao.add("users",values);
                    if(AddResult){
                        startActivity(menu_intent);
                        SplashActivity.this.finish();
                    }else{
                        Toast.makeText(mContext, "错误代码:0", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

}



