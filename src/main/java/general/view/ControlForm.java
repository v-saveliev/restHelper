package general.view;

import org.springframework.beans.factory.annotation.Autowired;
import general.service.ExiteService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

@Component
public class ControlForm extends JFrame{
    private JTextField tokenField;
    private JTextField textField1;
    private JButton getTokenButton;
    private JButton generateTicketButton;
    private JPanel panel;
    @Value("${logo}")
    String imagePath;
    @Autowired
    private ExiteService exiteService;

    public ControlForm() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // exit out of application
        this.setContentPane(panel);
        this.setTitle("Exite helper"); // sets title of frame

        ImageIcon logo = new ImageIcon(imagePath); // create an ImageIcon
        this.setIconImage(logo.getImage()); // change icon frame

        this.pack();
        this.setVisible(true); // make frame visible;

        getTokenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getTokenButtonListener(e);
            }
        });
    }

    private void getTokenButtonListener(ActionEvent e) {
        try {
            tokenField.setText(exiteService.getApiToken());
        } catch (Exception ex) {

        }
    }

    private void fileButtonListener(ActionEvent e) {
        if (e.getSource() == generateTicketButton) {
            JFileChooser fileChooser = new JFileChooser();
            int response = fileChooser.showOpenDialog(null);

            if (response == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
            }
        }
    }



}
