package Main;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Date;
import java.util.Scanner;


public class client {
    public static void main(String[] args) throws Exception{
        UIManager.setLookAndFeel(new FlatLightLaf());
        MainActivity mainActivity = new MainActivity();
        JFrame jFrame = new JFrame("数据传输助手-客户端");
        jFrame.setLocation(400,200);
        jFrame.setSize(800,600);
        jFrame.setContentPane(mainActivity.getRoot());
        jFrame.setVisible(true);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ProgressActivity progressActivity = new ProgressActivity();
        Socket cli;
        while(true){
            Scanner sc = new Scanner(new ByteArrayInputStream(mainActivity.byteArrayOutputStream1.toByteArray()));
            String ip;
            if (sc.hasNext()) {
                ip = sc.next();
                cli = new Socket(ip, 23333);     //启动客户端，使用23333端口
                mainActivity.byteArrayOutputStream1.reset();
                mainActivity.byteArrayOutputStream.reset();
                mainActivity.getTextArea1().setText("");
                mainActivity.getTextArea1().append("************已连接上服务端***********\n");
                break;
            }
            else {
                Thread.sleep(100);
            }
        }
        new process(cli,mainActivity).start();   //启动接收图片线程
        DataOutputStream dos = new DataOutputStream(cli.getOutputStream());
        dos.writeUTF(mainActivity.getTextField3().getText());
        dos.writeUTF("一个机智的客户端连上了");
        while (true) {
            JFrame jFrame1 = new JFrame();
            jFrame1.setSize(200,100);
            jFrame1.setLocation(400,200);
            jFrame1.setContentPane(progressActivity.getPanel1());
            //System.out.println("yes");
            JProgressBar jProgressBar;
            Scanner sc = new Scanner(new ByteArrayInputStream(mainActivity.byteArrayOutputStream.toByteArray()));
            String str;
            if (sc.hasNext()) {
                str = sc.next();
                mainActivity.byteArrayOutputStream.reset();
                if (str.indexOf("IMG:") == 0) {    //同服务端，此处输入文本开头包括IMG:时进入文件传输
                    dos.writeUTF("Thisisaimage".trim());   //发送Thisisaimage表示开始传文件
                    dos.writeUTF(sc.next());    //IMG:后跟文件
                    File img = new File(str.substring(4));
                    if (!img.exists()) {   //如果文件不存在则发送文件长度0
                        dos.writeLong(0);
                    } else {    //否则开始传文件
                        dos.writeLong(img.length());
                        FileInputStream fis = new FileInputStream(img);
                        int progress = 0;
                        int i1;
                        byte b[] = new byte[1024];
                        jProgressBar = progressActivity.getProgressBar1();
                        jFrame1.setVisible(true);
                        while ((i1 = fis.read(b)) != -1) {
                            dos.write(b, 0, i1);
                            progress += i1;
                            jProgressBar.setValue((int)(progress / img.length() * 100));
                        }
                        jProgressBar.setValue(0);
                        jFrame1.dispose();
                    }
                } else {      //否则发送文本
                    dos.writeUTF(str);
                }
            }
        }
    }
}

class process extends Thread{      //这条线程用于接收图片，基本同服务端。
    Socket socket;
    MainActivity mainActivity;
    StringBuffer stringBuffer = new StringBuffer();
    process(Socket socket,MainActivity mainActivity){
        this.socket = socket;
        this.mainActivity = mainActivity;
    }
    @Override
    public void run() {
        ProgressActivity progressActivity = new ProgressActivity();
        JProgressBar jProgressBar = progressActivity.getProgressBar1();
        try {
            while(true){
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                    String str = dis.readUTF();
                    if(str.trim().equals("Thisisaimage")){
                        JFrame jFrame1 = new JFrame();
                        jFrame1.setSize(200,100);
                        jFrame1.setLocation(400,200);
                        jFrame1.setContentPane(progressActivity.getPanel1());
                        mainActivity.getTextArea1().append("接收到文件传输请求\n");
                        String wenjianming = dis.readUTF().trim();
                        File image = new File("." + File.separator + wenjianming);
//                        System.out.println("接受中...");
                        long len = dis.readLong();
                        mainActivity.getTextArea1().append("[" + new Date().toString() + "]\n" + "  <" + "系统" +">  文件大小：" + len +"字节，文件名：" + wenjianming +"\n");
                        if(len != 0){
                            image.createNewFile();
                            FileOutputStream fo = new FileOutputStream(image);
                            int i;
                            byte b[] = new byte[1024];
                            jFrame1.setVisible(true);
                            while((i = dis.read(b)) != -1 && len != 0){
                                jProgressBar.setValue((int)((image.length() / len) * 100));
                                fo.write(b,0,i);
                                if(image.length() >= len){
                                    jFrame1.dispose();
                                    break;
                                }
                            }
                            new DataOutputStream(socket.getOutputStream()).writeUTF("接收完了");
                            //stringBuffer.append("接收完了\n");
                            mainActivity.getTextArea1().append("[" + new Date().toString() + "]\n" + "  <" + "服务器" + ">  接收完了\n");
                        }
                        else{
                            new DataOutputStream(socket.getOutputStream()).writeUTF("接收失败，文件不存在");
                        }
                    }
                    else{
                        mainActivity.getTextArea1().append("[" + new Date().toString() + "]\n" + "  <" + "服务器" + ">  " + str +"\n");
                    }
                }
        } catch (SocketException se){
            mainActivity.getTextArea1().append("[" + new Date().toString() + "]\n" + "  <" + "服务器" + ">  " + "与服务端连接已失效" +"\n");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}