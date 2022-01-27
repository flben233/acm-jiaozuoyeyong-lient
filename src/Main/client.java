package Main;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class client {
    public static void main(String[] args) throws Exception {
        Socket cli = new Socket("localhost", 23333);     //启动客户端，使用23333端口
        new process(cli).start();   //启动接收图片线程
        DataOutputStream dos = new DataOutputStream(cli.getOutputStream());
        dos.writeUTF("一个机智的客户端连上了");
        while (true) {
            Scanner sc = new Scanner(System.in);
            String str = sc.next();
            if (str.indexOf("IMG:") == 0) {    //同服务端，此处输入文本开头包括IMG:时进入文件传输
                dos.writeUTF("Thisisaimage".trim());   //发送Thisisaimage表示开始传文件
                dos.writeUTF(str.substring(4));    //IMG:后跟文件名
                File img = new File("." + File.separator + str.substring(4));
                if(!img.exists()){   //如果文件不存在则发送文件长度0
                    dos.writeLong(0);
                }
                else{    //否则开始传文件
                    dos.writeLong(img.length());
                    FileInputStream fis = new FileInputStream(img);
                    int i1;
                    byte b[] = new byte[1024];
                    while ((i1 = fis.read(b)) != -1) {
                        dos.write(b, 0, i1);
                    }
                }
            }
            else {      //否则发送文本
                dos.writeUTF(str);
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
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                    String str = dis.readUTF();
                    if(str.trim().equals("Thisisaimage")){
                        System.out.println("接收到请求");
                        String wenjianming = dis.readUTF().trim();
                        File image = new File("." + File.separator + wenjianming);
                        System.out.println("接受中...");
                        long len = dis.readLong();
                        System.out.println(len);
                        if(len != 0){
                            image.createNewFile();
                            FileOutputStream fo = new FileOutputStream(image);
                            int i;
                            byte b[] = new byte[1024];
                            while((i = dis.read(b)) != -1 && len != 0){
                                fo.write(b,0,i);
                                if(image.length() >= len){
                                    break;
                                }
                            }
                            new DataOutputStream(socket.getOutputStream()).writeUTF("接收完了");
                            System.out.println("接收完了");
                        }
                        else{
                            new DataOutputStream(socket.getOutputStream()).writeUTF("接收失败，文件不存在");
                        }
                    }
                    else{
                        System.out.println(str);
                    }
                }
        } catch (SocketException se){
            System.out.println("与服务端的连接已失效");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}