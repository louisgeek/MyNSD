package com.classichu.mynsd.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.Executors;

/**
 * Created by louisgeek on 2018/8/22.
 */
public class TcpClient {
    Socket socket;

    public void connect(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
    }

    public void send(final String text) {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                //
                InputStream is = null;
                InputStreamReader isr = null;
                BufferedReader br = null;
                OutputStream os = null;
                PrintWriter pw = null;
                try {
                    //
                    os = socket.getOutputStream();
                    pw = new PrintWriter(os);// 将输出流包装为打印流
                    pw.write(text);
                    pw.flush();// 刷新缓存
//pw.close();// 不能关闭输出流，会导致socket也关闭
                    socket.shutdownOutput();// 关闭输出流
//
                    is = socket.getInputStream();
                    isr = new InputStreamReader(is);// 转换成字符输入流
                    br = new BufferedReader(isr);// 字符输入流缓冲
                    String info = null;
                    while ((info = br.readLine()) != null) {// 循环读取客户端的信息
                        System.out.println("读取到服务端信息：" + info);
                    }
                    socket.shutdownInput();// 关闭输入流
                    //
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        br.close();
                        isr.close();
                        is.close();
                        pw.close();
                        os.close();
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }
}
