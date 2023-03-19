package general.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class AppFrame extends JFrame implements ActionListener {
    JButton contentButton;
    public AppFrame() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // exit out of application
        this.setTitle("Exite helper"); // sets title of frame
        this.setSize(420,420); // set the x-dimension, and y-dimension of frame
        this.setResizable(false); // prevent frame from being resized

        contentButton = new JButton("Select file");
        contentButton.addActionListener(this);
        this.add(contentButton);

        String imagePath = "C:\\dev\\work\\projects\\exiteHelper\\src\\main\\java\\appLogo.png";
        ImageIcon logo = new ImageIcon(imagePath); // create an ImageIcon
        this.setIconImage(logo.getImage()); // change icon frame
        this.getContentPane().setBackground(new Color(70, 50, 80)); // change color of background

        this.pack();
        this.setVisible(true); // make frame visible
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == contentButton) {
            JFileChooser fileChooser = new JFileChooser();
            int response = fileChooser.showOpenDialog(null);

            if (response == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
            }
        }
    }
}
