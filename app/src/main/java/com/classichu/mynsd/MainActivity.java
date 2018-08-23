package com.classichu.mynsd;

import android.content.Context;
import android.content.DialogInterface;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.classichu.mynsd.adpter.NSDAdapter;
import com.classichu.mynsd.nsd.NSDClient;
import com.classichu.mynsd.nsd.NSDInfo;
import com.classichu.mynsd.socket.TcpClient;
import com.classichu.mynsd.tool.ThreadTool;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    Context mContext;
    NSDClient mNSDClient;
    ListView id_lv;
    TextView id_tv_log;
    NSDAdapter mNSDAdapter;
    TcpClient tcpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;


        id_lv = findViewById(R.id.id_lv);
        id_tv_log = findViewById(R.id.id_tv_log);

        mNSDAdapter = new NSDAdapter();
        id_lv.setAdapter(mNSDAdapter);
        id_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final NSDInfo nsdInfo = mNSDAdapter.getItem(position);
                //
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        tcpClient = new TcpClient();
                        try {
                            tcpClient.connect(nsdInfo.ip, nsdInfo.port);
                            ThreadTool.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    final EditText editText = new EditText(mContext);
                                    new AlertDialog.Builder(mContext)
                                            .setView(editText)
                                            .setTitle("发送命令")
                                            .setPositiveButton("发送", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    tcpClient.send("测试信息:" + editText.getText());
                                                }
                                            })
                                            .create().show();
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                //
            }
        });

        mNSDClient = new NSDClient(this);
        mNSDClient.init();
        mNSDClient.setOnNsdServiceInfoStateListener(new NSDClient.OnNsdServiceInfoStateListener() {

            @Override
            public void onStateChange(final Map<String, NsdServiceInfo> nsdServiceInfoMap) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (nsdServiceInfoMap == null) {
                            Toast.makeText(mContext, "扫描没有结果", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        List<NSDInfo> nsdInfoList = new ArrayList<>();
                        for (String key : nsdServiceInfoMap.keySet()) {
                            NsdServiceInfo nsdServiceInfo = nsdServiceInfoMap.get(key);
                            NSDInfo nsdInfo = new NSDInfo();
                            nsdInfo.name = nsdServiceInfo.getServiceName();
                            nsdInfo.ip = nsdServiceInfo.getHost().getHostAddress();
                            nsdInfo.port = nsdServiceInfo.getPort();
                            nsdInfoList.add(nsdInfo);
                        }
                        mNSDAdapter.refreshData(nsdInfoList);
                    }
                });
            }

            @Override
            public void onShowLog(final String log) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        id_tv_log.setText(id_tv_log.getText() + "\n" + log);
                    }
                });
            }
        });
    }


    @Override
    protected void onPause() {
        mNSDClient.stopServiceDiscovery();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        id_tv_log.setText("start_" + simpleDateFormat.format(new Date()));
        mNSDClient.discoverServices();
    }


}
