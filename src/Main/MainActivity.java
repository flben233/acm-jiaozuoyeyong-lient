package Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.*;
import java.util.Date;

public class MainActivity {
    ByteArrayOutputStream byteArrayOutputStream;
    ByteArrayOutputStream byteArrayOutputStream1 = new ByteArrayOutputStream();
    private JTextArea textArea1;
    private JTextField textField1;
    private JButton fileButton;
    private JButton textButton;
    private JPanel root;
    private JTextField textField2;
    private JButton 连接Button;
    private JTextField textField3;
    private JProgressBar progressBar1;

    public JProgressBar getProgressBar1() {
        return progressBar1;
    }

    JFileChooser jFileChooser;

    public JTextField getTextField3() {
        return textField3;
    }

    public MainActivity() {
        byteArrayOutputStream = new ByteArrayOutputStream();
        textButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PrintStream prt = new PrintStream(byteArrayOutputStream);
                String str = textField1.getText();
                prt.println(str);
                textField1.setText("");
                prt.close();
                textArea1.append("[" + new Date().toString() + "]\n" + "  <" + "我" + ">  " + str + "\n");
            }
        });
        fileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jFileChooser = new JFileChooser();
                jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                jFileChooser.setMultiSelectionEnabled(false);
                jFileChooser.showDialog(root,"选择文件");
                if(jFileChooser.getSelectedFile() != null) {
                    PrintStream prt = new PrintStream(byteArrayOutputStream);
                    prt.println("IMG:" + jFileChooser.getSelectedFile().getAbsolutePath() + "\n" + jFileChooser.getSelectedFile().getName());
                    textArea1.append("[" + new Date().toString() + "]\n" + "  <" + "我" + ">  " + "[" + jFileChooser.getSelectedFile().getName() + "]\n");
                    prt.close();
                }
            }
        });
        连接Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!textField2.getText().equals("要连接的IP")) {
                    PrintStream prt = new PrintStream(byteArrayOutputStream1);
                    prt.println(textField2.getText());
                }
            }
        });
        textField3.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if(textField3.getText().equals("你的名字")) {
                    textField3.setText("");
                    textField3.setForeground(Color.getColor("BBBBBB"));
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if(textField3.getText().equals("")) {
                    textField3.setForeground(Color.gray);
                    textField3.setText("你的名字");
                }
            }
        });
        textField2.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if(textField2.getText().equals("要连接的IP")) {
                    textField2.setForeground(Color.getColor("BBBBBB"));
                    textField2.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if(textField2.getText().equals("")) {
                    textField2.setForeground(Color.gray);
                    textField2.setText("要连接的IP");
                }
            }
        });
    }

    public JButton get连接Button() {
        return 连接Button;
    }

    public JTextArea getTextArea1() {
        return textArea1;
    }

    public JPanel getRoot() {
        return root;
    }
}
