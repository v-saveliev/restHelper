package general.view;

import general.dto.GenerateTicketRequest;
import general.service.FileService;
import general.utils.ConfigLoader;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import general.service.ExiteService;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@Component
@Data
public class ControlForm extends JFrame {
    private JTextField tokenField;
    private JTextField textIndentifier;
    private JButton getTokenButton;
    private JButton generateTicketButton;
    private JPanel panel;
    private JTextField textComment;
    private JTextField textSignerFirstname;
    private JTextField textSignerSurname;
    private JTextField textSignerInn;
    private JTextField textSignerPosition;
    private JCheckBox isRecallCheckBox;
    private JTextArea textArea1;
    @Autowired
    private ExiteService exiteService;
    @Autowired
    FileService fileService;
    private ConfigLoader configLoader;
    private JScrollPane jScrollPane;

    @Autowired
    public ControlForm(ConfigLoader configLoader) {
        this.configLoader = configLoader;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // exit out of application
        setContentPane(panel);
        this.setTitle("Exite helper"); // sets title of frame
        setResizable(false);
        setMinimumSize(new Dimension(420, 420));
        setLocationRelativeTo(null); // set JFrame in the center

        ImageIcon logo = new ImageIcon("appLogo.png"); // create an ImageIcon
        setIconImage(logo.getImage()); // change icon frame

        pack();
        setVisible(true); // make frame visible;

        try {
            fillTextFieldsByDefault();
        } catch (Exception e) {

        }
        getTokenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getTokenButtonListener(e);
            }
        });

        generateTicketButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateTicketButtonListener(e);
            }
        });

    }

    private void getTokenButtonListener(ActionEvent e) {
        try {
            tokenField.setText(exiteService.getApiToken());
        } catch (Exception ex) {
            tokenField.setText("error");
        }
    }

    private void generateTicketButtonListener(ActionEvent e) {
        try {
            GenerateTicketRequest ticketRequest = new GenerateTicketRequest();
            ticketRequest.setIdentifier(textIndentifier.getText());
            ticketRequest.setComment(textComment.getText());
            ticketRequest.setRecall(isRecallCheckBox.isBorderPaintedFlat());
            ticketRequest.setSigner_inn(textSignerInn.getText());
            ticketRequest.setSigner_fname(textSignerFirstname.getText());
            ticketRequest.setSigner_sname(textSignerSurname.getText());
            ticketRequest.setSigner_position(textSignerPosition.getText());

            String content = exiteService.generateTicket(ticketRequest);
            textArea1.setLineWrap(true);
            textArea1.setText("Base64 content: " + content);
            textArea1.setText("content saved to file: " + fileService.saveFileFromBase64(content) + "\n\n" + textArea1.getText());
        } catch (Exception ex) {

        }
    }

    private void fillTextFieldsByDefault() throws Exception {
        textSignerFirstname.setText(configLoader.getProperty("signerFirstname"));
        textSignerSurname.setText(configLoader.getProperty("signerSurname"));
        textSignerFirstname.setText(configLoader.getProperty("signerFirstname"));
        textSignerInn.setText(configLoader.getProperty("signerInn"));
        textSignerPosition.setText(configLoader.getProperty("signerPosition"));
    }

}
