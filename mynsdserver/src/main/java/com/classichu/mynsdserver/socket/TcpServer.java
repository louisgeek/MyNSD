package com.classichu.mynsdserver.socket;

import android.content.Context;
import android.widget.Toast;

import com.classichu.mynsdserver.tool.ThreadTool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

/**
 * Created by louisgeek on 2018/8/22.
 */
public class TcpServer {

    Socket socket;

    public void startServer(final Context context,ServerSocket serverSocket) throws IOException {
        while (true) {
            //
            socket = serverSocket.accept();

            Executors.newSingleThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {

                    InputStream is = null;
                    InputStreamReader isr = null;
                    BufferedReader br = null;
                    OutputStream os = null;
                    PrintWriter pw = null;

                    try {
                        //
                        is = socket.getInputStream();
                        isr = new InputStreamReader(is);
                        br = new BufferedReader(isr);
                        String info;
                        while ((info = br.readLine()) != null) {
//                            System.out.println("*******客户端信息：" + info);
                            final String finalInfo = info;
                            ThreadTool.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, "收到客户端信息:"+finalInfo, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        //
                        socket.shutdownInput();// 关闭输入流

                        //
                        os = socket.getOutputStream();
                        pw = new PrintWriter(os);
                        pw.write("发送给服务端：你好");
                        pw.flush();// 刷新缓存，将缓存输出
                        socket.shutdownOutput();// 关闭输出流

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        // 关闭资源
                        try {
                            pw.close();
                            os.close();
                            br.close();
                            isr.close();
                            is.close();
                            socket.close();
//                            serverSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }


                }
            });


        }
    }

}
