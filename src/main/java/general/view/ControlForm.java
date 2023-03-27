package general.view;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import general.dto.GenerateTicketRequest;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import general.service.ExiteService;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.charset.StandardCharsets;

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

    public ControlForm() {
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
            textArea1.setText(content);
        } catch (Exception ex) {

        }
    }

    private void fillTextFieldsByDefault() throws Exception {
        FileInputStream inputStream = new FileInputStream("config.json");

        JsonObject jsonConfig = new JsonObject();
        String check = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        jsonConfig = (JsonObject) JsonParser.parseString(check);

        if (jsonConfig.get("signerSurname") != null)
            textSignerSurname.setText(jsonConfig.get("signerSurname").getAsString());
        if (jsonConfig.get("signerFirstname") != null)
            textSignerFirstname.setText(jsonConfig.get("signerFirstname").getAsString());
        if (jsonConfig.get("signerInn") != null)
            textSignerInn.setText(jsonConfig.get("signerInn").getAsString());
        if (jsonConfig.get("signerPosition") != null)
            textSignerPosition.setText(jsonConfig.get("signerPosition").getAsString());
    }

}
