package com.example.wechat.Activity.Login_Register.ChatActivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.wechat.Activity.BaseActivity;
import com.example.wechat.Chat.Message;
import com.example.wechat.R;
import com.example.wechat.Table.Chat;
import com.example.wechat.Uitls.LogHelper;
import com.example.wechat.Uitls.ToastHelper;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobRealTimeData;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.ValueEventListener;

/**
 * Created by 铖哥 on 2017/4/24.
 */

public class ChatActivity extends BaseActivity{
    List<Message> messageList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_box);

        messageList = new ArrayList<>();
        final RecyclerView rv_message = (RecyclerView) findViewById(R.id.rv_message);

        final ChatAdapter chatAdapter = new ChatAdapter(messageList);
        rv_message.setAdapter(chatAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        rv_message.setLayoutManager(llm);

        final BmobRealTimeData rtd = new BmobRealTimeData();
        rtd.start(this, new ValueEventListener() {
            @Override
            public void onConnectCompleted() {
                ToastHelper.Toast("ConnectCompleted");
                rtd.subTableUpdate("Chat");
            }

            @Override
            public void onDataChange(JSONObject jsonObject) {
                Message message ;
                Gson gson = new Gson();
                LogHelper.e(jsonObject.optString("data"));
                message = gson.fromJson(jsonObject.optString("data"), Message.class);
                messageList.add(message);
                chatAdapter.notifyItemInserted(messageList.size()-1);
                rv_message.scrollToPosition(messageList.size()-1);
            }
        });

        final EditText et_message = (EditText) findViewById(R.id.et_message);
        Button bt_send = (Button) findViewById(R.id.bt_send);
        bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Chat chat = new Chat();
                BmobUser bmobUser = BmobUser.getCurrentUser(ChatActivity.this);
                chat.setMessage(et_message.getText().toString());
                chat.setFrom(bmobUser.getUsername());
                chat.save(ChatActivity.this, new SaveListener() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFailure(int i, String s) {
                        ToastHelper.Toast("failure");
                    }
                });
            }
        });

    }
}
