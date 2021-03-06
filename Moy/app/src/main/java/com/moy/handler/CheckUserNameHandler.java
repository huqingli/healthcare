package com.moy.handler;

import android.util.Log;

import com.moy.util.ConnectUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class CheckUserNameHandler {
    public static String CheckUserName(String userName,int userid) {
        String result = "";//注册返回的结果
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet request = new HttpGet(ConnectUtil.BASE_URL + "CheckUsername1Servlet?userName=" + userName + "&userid=" + userid);
        try {
            HttpResponse response = httpclient.execute(request);
            System.out.print(response.getStatusLine().getStatusCode());
            if(response.getStatusLine().getStatusCode()==200){
                HttpEntity entity = response.getEntity();
                result = EntityUtils.toString(entity, "utf-8");
                Log.i("Login.result:",result);
                return result;
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
