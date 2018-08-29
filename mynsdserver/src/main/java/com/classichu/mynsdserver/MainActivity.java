package com.classichu.mynsdserver;

import android.content.Context;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.classichu.mynsdserver.nsd.NSDServer;
import com.classichu.mynsdserver.socket.TcpServer;

import java.io.IOException;
import java.net.ServerSocket;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    Context mContext;
    NSDServer mNSDServer;
    int mPort;
    ServerSocket mServerSocket;

    TextView id_tv;
    TextView id_tv_log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;


        //
        id_tv = findViewById(R.id.id_tv);
        id_tv_log = findViewById(R.id.id_tv_log);
        //
        mNSDServer = new NSDServer(this);
        mNSDServer.init();
        mNSDServer.setOnNsdServiceInfoStateListener(new NSDServer.OnNsdServiceInfoStateListener() {
            @Override
            public void onServiceLost(NsdServiceInfo nsdServiceInfo) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //
                        Toast.makeText(mContext, "服务丢失", Toast.LENGTH_SHORT).show();

                    }
                });
            }

            @Override
            public void onServiceResolved(final NsdServiceInfo nsdServiceInfo) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //
                        id_tv.setText(nsdServiceInfo.getServiceName()
                                + "服务已启动："
                                + nsdServiceInfo.getHost().getHostAddress() + ":"
                                + nsdServiceInfo.getPort());
//                        Log.e(TAG, "服务启动成功 run: " + mNetNSDManager.getNsdServiceInfo().toString());


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

        try {
            //auto set port
            mServerSocket = new ServerSocket(0);
            mPort = mServerSocket.getLocalPort();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mNSDServer.registerService(mPort);
        //
        new Thread(new Runnable() {
            @Override
            public void run() {
                TcpServer tcpServer = new TcpServer();
                try {
                    tcpServer.startServer(mContext, mServerSocket);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "run: ", e);
                }
            }
        }).start();


    }


    @Override
    protected void onPause() {
//        mNSDServer.stopServiceDiscovery();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
      /*  SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        id_tv_log.setText("start_" + simpleDateFormat.format(new Date()));
        mNSDServer.discoverServices();*/
    }

    @Override
    protected void onDestroy() {
        mNSDServer.unregisterService();
        super.onDestroy();

    }
}
