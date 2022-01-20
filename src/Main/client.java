package Main;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class client {
    public static void main(String[] args) throws Exception {
        Socket cli = new Socket("localhost", 23333);     //启动客户端，使用23333端口
        new process(cli).start();   //启动接收图片线程
        PrintStream prt1 = new PrintStream(cli.getOutputStream());
        prt1.println("Client1 connected.");   //向客户端发送连接成功消息
        while (true) {
            Scanner sc = new Scanner(System.in);
            String str = sc.next();
            if (str.indexOf("IMG:") == 0) {    //同服务端，此处输入文本开头包括IMG:时进入文件传输
                prt1.println("Thisisaimage".trim());   //发送Thisisaimage表示开始传文件
                prt1.println(str.substring(4));    //IMG:后跟文件名
                File img = new File("." + File.separator + str.substring(4));
                if(!img.exists()){   //如果文件不存在则发送文件长度0
                    prt1.println(0);
                }
                else{    //否则开始传文件
                    if(img.length() < 61858764){
                        prt1.println(img.length());    //发送文件大小
                        OutputStream os = cli.getOutputStream();   //使用输出流传文件
                        FileInputStream fis = new FileInputStream(img);
                        int i1;
                        byte b[] = new byte[61858764];    //暂时还没解决文件分块传输的问题，只能暂时采用把文件一次性加载进内存再传输的办法，最大60M
                        while ((i1 = fis.read(b)) != -1) {
                            os.write(b, 0, i1);
                        }
                    }
                    else {
                        prt1.println(0);
                    }
                }
            }
            else {      //否则发送文本
                prt1.println("Client1:" + str);
            }
        }
    }
}

class process extends Thread{      //这条线程用于接收图片，基本同服务端。
    Socket socket;
    process(Socket socket){
        this.socket = socket;
    }
    @Override
    public void run() {
        try {
            while(true){
                Scanner sc = new Scanner(socket.getInputStream());
                sc.useDelimiter("\n");
                if(sc.hasNext()) {
                    String str = sc.next();
                    if(str.trim().equals("Thisisaimage")){
                        System.out.println("接收到请求");
                        String wenjianming = sc.next().trim();
                        File image = new File("." + File.separator + wenjianming);
                        image.createNewFile();
                        System.out.println("接受中...");
                        long len = Long.valueOf(sc.next().trim());
                        System.out.println(len);
                        if(len != 0){
                            FileOutputStream fo = new FileOutputStream(image);
                            InputStream is = socket.getInputStream();
                            int i;
                            byte b[] = new byte[61858764];
                            while((i = is.read(b)) != -1 && len != 0){
                                //System.out.println(image.length());
                                fo.write(b,0,i);
                                if(image.length() >= len){
                                    break;
                                }
                            }
                            new PrintStream(socket.getOutputStream()).println("接收完了");
                        }
                        else{
                            new PrintStream(socket.getOutputStream()).println("接收失败，文件不存在");
                        }
                    }
                    else{
                        System.out.println(str);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
