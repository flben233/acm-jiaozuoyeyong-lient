package Main;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DownloadActivity {
    private JTextField textField1;
    private JPanel panel1;
    private JButton OKButton;
    private JButton ExitButton;
    private JButton PathButton;
    private JFrame jFrame;

    public void setjFrame(JFrame jFrame) {
        this.jFrame = jFrame;
    }

    public JPanel getPanel1() {
        return panel1;
    }

    public JTextField getTextField1() {
        return textField1;
    }

    public DownloadActivity() {
        PathButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                jFileChooser.setMultiSelectionEnabled(false);
                jFileChooser.showDialog(panel1,"选择");
                if(jFileChooser.getSelectedFile() != null){
                    textField1.setText(jFileChooser.getSelectedFile().getAbsolutePath());
                }
            }
        });
        OKButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jFrame.dispose();
            }
        });

        ExitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jFrame.dispose();
            }
        });
    }

}
