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

import static android.content.ContentValues.TAG;

public class ResultDataHandler {
    public static String result(int userid) {
        String result = "";//注册返回的结果
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet request = new HttpGet(ConnectUtil.BASE_URL + "ResultDataServlet?userid=" + userid);
        Log.i(TAG, "result: ");
	/*	HttpGet request = new HttpGet(ConnectUtil.BASE_URL + "modifyServlet?userName=" + userName
				+ "&sex=" + "xxx" + "&age=" + "xxx"+ "&height=" + "xxx" + "&weight=" + "xxx");*/
        try {
            HttpResponse response = httpclient.execute(request);
            if(response.getStatusLine().getStatusCode()==200){
                HttpEntity entity = response.getEntity();
                result = EntityUtils.toString(entity,"utf-8");
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
