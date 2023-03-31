package general.view;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import general.dto.GenerateTicketRequest;
import general.dto.GetTicketRequest;
import general.service.EdsService;
import general.service.FileService;
import general.service.MotpService;
import general.utils.ConfigLoader;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import general.service.ExiteService;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

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
    private JCheckBox withSignCheckBox;
    private JTabbedPane tabbedPane1;
    private JPanel checkpane;
    private JTextArea textConfigJson;
    private JButton saveConfigButton;
    private JButton fillButton;
    private JTextField textShard;
    private JTextField textDoc;
    private JButton motpButton;
    private JTextField textTicketId;
    private JButton buttonGetTicket;
    private JTextField textGetTicketCount;
    private JTextField textShardEds;
    private JTextField textDocEds;
    private JTextField textTypeEds;
    private JButton getTicketEdsButton;
    @Autowired
    private ExiteService exiteService;
    @Autowired
    private EdsService edsService;
    @Autowired
    private MotpService motpService;
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
        setMinimumSize(new Dimension(550, 500));
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

        saveConfigButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    saveButtonListener(e);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        fillButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    fillTextFieldsByDefault();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        motpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    GetTicketRequest ticketRequest = new GetTicketRequest();
                    ticketRequest.setShardUUID(textShard.getText());
                    ticketRequest.setDocUUID(textDoc.getText());
                    if (edsService.getTicketPackageForMotp(ticketRequest)) {
                        String result = motpService.sendCRPT(new FileInputStream("eds_service_content.zip").readAllBytes());
                        textArea1.setText(result);
                    }
                } catch (Exception e) {

                }
            }
        });
        buttonGetTicket.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    int attempts = Integer.parseInt(textGetTicketCount.getText().equals("") ? "0" : textGetTicketCount.getText());
                    attempts = attempts == 0 ? 1 : attempts;
                    for (int i = 0; i < attempts; i++) {
                        System.out.println(motpService.getTicket(textTicketId.getText()));


                        GetTicketRequest ticketRequest = new GetTicketRequest();
                        ticketRequest.setShardUUID(textShard.getText());
                        ticketRequest.setDocUUID(textDoc.getText());
                        if (edsService.getTicketPackageForMotp(ticketRequest)) {
                            String result = motpService.sendCRPT(new FileInputStream("eds_service_content.zip").readAllBytes());
                            textArea1.setText(result);
                        }

                    }
                } catch (Exception e) {

                }
            }
        });
        getTicketEdsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    saveTicketButtonListener(actionEvent);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
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
            ticketRequest.setRecall(isRecallCheckBox.isSelected());
            ticketRequest.setSigner_inn(textSignerInn.getText());
            ticketRequest.setSigner_fname(textSignerFirstname.getText());
            ticketRequest.setSigner_sname(textSignerSurname.getText());
            ticketRequest.setSigner_position(textSignerPosition.getText());

            String content = exiteService.generateTicket(ticketRequest);
            textArea1.setLineWrap(true);
            textArea1.setText("Base64 content: " + content);
            String pathSavedContent = fileService.saveFileFromBase64(content);
            textArea1.setText("content saved to file: " + pathSavedContent + "\n\n" + textArea1.getText());
            if (withSignCheckBox.isSelected())
                fileService.signContent(pathSavedContent);
        } catch (Exception ex) {

        }
    }

    private void fillTextFieldsByDefault() throws Exception {
        textConfigJson.setText(configLoader.getConfigAsString());

        textSignerFirstname.setText(configLoader.getProperty("signerFirstname"));
        textSignerSurname.setText(configLoader.getProperty("signerSurname"));
        textSignerFirstname.setText(configLoader.getProperty("signerFirstname"));
        textSignerInn.setText(configLoader.getProperty("signerInn"));
        textSignerPosition.setText(configLoader.getProperty("signerPosition"));

        textShard.setText(configLoader.getProperty("shard"));
        textDoc.setText(configLoader.getProperty("doc"));
        textShardEds.setText(configLoader.getProperty("shard"));
        textDocEds.setText(configLoader.getProperty("doc"));
    }

    private void saveButtonListener(ActionEvent e) throws Exception {
        fileService.updateConfig(textConfigJson.getText());
    }

    private void saveTicketButtonListener(ActionEvent e) throws Exception {
        List<Map<String, byte[]>> tickets = new ArrayList<>();
        GetTicketRequest ticketRequest = new GetTicketRequest();
        ticketRequest.setShardUUID(textShardEds.getText());

        ticketRequest.setTransactionType(textTypeEds.getText());
        String[] docsUuids = textDocEds.getText().split(";");
        for (String uuid : docsUuids) {
                ticketRequest.setDocUUID(uuid.trim());
                Map<String, byte[]> content = edsService.getTicketBodiesAsByteArrays(ticketRequest);
                if (content != null && content.size() > 0)
                    tickets.add(content);
        }


        String fileName = null;
        if (tickets.size() > 0)
            fileName = fileService.saveZipFromMapContent(tickets, "eds_content_" + ticketRequest.getDocUUID());

        if (fileName != null)
            textArea1.setText("content zip archive is saved: " + fileName);
        else {
            textArea1.setText("something went wrong");
        }
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel = new JPanel();
        panel.setLayout(new GridLayoutManager(5, 7, new Insets(5, 5, 5, 5), -1, -1));
        panel.setToolTipText("");
        tabbedPane1 = new JTabbedPane();
        panel.add(tabbedPane1, new GridConstraints(0, 0, 1, 7, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        checkpane = new JPanel();
        checkpane.setLayout(new GridLayoutManager(5, 3, new Insets(5, 5, 0, 3), -1, -1));
        tabbedPane1.addTab("Start & Config", checkpane);
        tokenField = new JTextField();
        tokenField.setText("");
        checkpane.add(tokenField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        getTokenButton = new JButton();
        getTokenButton.setText("Get Token");
        checkpane.add(getTokenButton, new GridConstraints(0, 2, 2, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Token");
        checkpane.add(label1, new GridConstraints(0, 0, 2, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("config:");
        checkpane.add(label2, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textConfigJson = new JTextArea();
        textConfigJson.setLineWrap(false);
        textConfigJson.setRows(10);
        textConfigJson.setText("");
        checkpane.add(textConfigJson, new GridConstraints(3, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        saveConfigButton = new JButton();
        saveConfigButton.setText("Save");
        checkpane.add(saveConfigButton, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fillButton = new JButton();
        fillButton.setText("Fill by default");
        checkpane.add(fillButton, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(10, 6, new Insets(5, 5, 0, 3), -1, -1));
        tabbedPane1.addTab("Ticket Generate", panel1);
        final JLabel label3 = new JLabel();
        label3.setText("Identifier");
        panel1.add(label3, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("comment");
        panel1.add(label4, new GridConstraints(1, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textComment = new JTextField();
        panel1.add(textComment, new GridConstraints(1, 3, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        textIndentifier = new JTextField();
        panel1.add(textIndentifier, new GridConstraints(0, 3, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("firstname");
        panel1.add(label5, new GridConstraints(5, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textSignerFirstname = new JTextField();
        panel1.add(textSignerFirstname, new GridConstraints(5, 3, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("surname");
        panel1.add(label6, new GridConstraints(6, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textSignerSurname = new JTextField();
        textSignerSurname.setText("");
        panel1.add(textSignerSurname, new GridConstraints(6, 3, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("inn");
        panel1.add(label7, new GridConstraints(7, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textSignerInn = new JTextField();
        panel1.add(textSignerInn, new GridConstraints(7, 3, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("position");
        panel1.add(label8, new GridConstraints(8, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textSignerPosition = new JTextField();
        textSignerPosition.setText("");
        panel1.add(textSignerPosition, new GridConstraints(8, 3, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(3, 3, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JLabel label9 = new JLabel();
        label9.setText("signer info:");
        panel1.add(label9, new GridConstraints(4, 0, 1, 6, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        generateTicketButton = new JButton();
        generateTicketButton.setText("Generate Ticket");
        panel1.add(generateTicketButton, new GridConstraints(9, 5, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        withSignCheckBox = new JCheckBox();
        withSignCheckBox.setText("with sign");
        panel1.add(withSignCheckBox, new GridConstraints(9, 4, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        isRecallCheckBox = new JCheckBox();
        isRecallCheckBox.setText("isRecall");
        panel1.add(isRecallCheckBox, new GridConstraints(2, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new BorderLayout(0, 0));
        tabbedPane1.addTab("Send", panel2);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(8, 3, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("motp-service", panel3);
        textShard = new JTextField();
        panel3.add(textShard, new GridConstraints(1, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel3.add(spacer2, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JLabel label10 = new JLabel();
        label10.setText("shardUUID");
        panel3.add(label10, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textDoc = new JTextField();
        panel3.add(textDoc, new GridConstraints(2, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label11 = new JLabel();
        label11.setText("docUUID");
        panel3.add(label11, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        motpButton = new JButton();
        motpButton.setHorizontalAlignment(0);
        motpButton.setHorizontalTextPosition(11);
        motpButton.setText("Send");
        panel3.add(motpButton, new GridConstraints(3, 1, 1, 2, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label12 = new JLabel();
        label12.setText("container :");
        panel3.add(label12, new GridConstraints(0, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JSeparator separator1 = new JSeparator();
        panel3.add(separator1, new GridConstraints(4, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JLabel label13 = new JLabel();
        label13.setText("ticket:");
        panel3.add(label13, new GridConstraints(5, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label14 = new JLabel();
        label14.setText("ticket id");
        panel3.add(label14, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textTicketId = new JTextField();
        panel3.add(textTicketId, new GridConstraints(6, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        buttonGetTicket = new JButton();
        buttonGetTicket.setText("Get");
        panel3.add(buttonGetTicket, new GridConstraints(7, 2, 1, 1, GridConstraints.ANCHOR_NORTHEAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textGetTicketCount = new JTextField();
        panel3.add(textGetTicketCount, new GridConstraints(7, 1, 1, 1, GridConstraints.ANCHOR_NORTHEAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(5, 2, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("EDS", panel4);
        final JLabel label15 = new JLabel();
        label15.setText("shard UUID");
        panel4.add(label15, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel4.add(spacer3, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        textShardEds = new JTextField();
        textShardEds.setText("");
        panel4.add(textShardEds, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label16 = new JLabel();
        label16.setText("docUUID");
        panel4.add(label16, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textDocEds = new JTextField();
        panel4.add(textDocEds, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label17 = new JLabel();
        label17.setText("transaction type");
        panel4.add(label17, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textTypeEds = new JTextField();
        panel4.add(textTypeEds, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        getTicketEdsButton = new JButton();
        getTicketEdsButton.setText("Get");
        panel4.add(getTicketEdsButton, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel.add(scrollPane1, new GridConstraints(3, 0, 1, 7, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        textArea1 = new JTextArea();
        textArea1.setDragEnabled(false);
        textArea1.setLineWrap(false);
        textArea1.setRows(5);
        textArea1.setText("");
        textArea1.setWrapStyleWord(false);
        scrollPane1.setViewportView(textArea1);
        final Spacer spacer4 = new Spacer();
        panel.add(spacer4, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer5 = new Spacer();
        panel.add(spacer5, new GridConstraints(1, 2, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JLabel label18 = new JLabel();
        label18.setText("Result:");
        panel.add(label18, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }

}
