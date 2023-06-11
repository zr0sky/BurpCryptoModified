package burp.aes;

import burp.BurpExtender;
import burp.utils.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.ArrayList;

public class AesUIHandler {
    private BurpExtender parent;
    private JPanel mainPanel;
    private JComboBox<String> aesAlgSelector;
    private JComboBox<String> aesKeyFormatSelector;
    private JComboBox<String> aesIVFormatSelector;
    private JComboBox<String> aesOutFormatSelector;
    private JTextField aesKeyText;
    private JTextField aesIVText;
    private JButton applyBtn, testBtn,deleteBtn;

    public AesUIHandler(BurpExtender parent) {
        this.parent = parent;
    }

    public JPanel getPanel() {
        final JSeparator separator = new JSeparator(0);
        separator.setMaximumSize(new Dimension(separator.getMaximumSize().width, separator.getPreferredSize().height));
        separator.setAlignmentX(0.0f);

        mainPanel = new JPanel();
        mainPanel.setAlignmentX(0.0f);
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setLayout(new BoxLayout(mainPanel, 1));

        final JLabel label1 = new JLabel("AES Setting");
        label1.setForeground(new Color(249, 130, 11));
        label1.setFont(new Font("Nimbus", 1, 16));
        label1.setAlignmentX(0.0f);

        final JPanel panel1 = UIUtil.GetXJPanel();
        final JPanel panel2 = UIUtil.GetXJPanel();
        final JPanel panel3 = UIUtil.GetXJPanel();
        final JPanel panel4 = UIUtil.GetXJPanel();
        final JPanel panel5 = UIUtil.GetXJPanel();

        final JLabel label2 = new JLabel("AES Alg: ");
        aesAlgSelector = new JComboBox(GetAesAlgs());
        aesAlgSelector.setMaximumSize(aesAlgSelector.getPreferredSize());
        aesAlgSelector.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String item = (String) e.getItem();
                CipherInfo cipherInfo = new CipherInfo(item);
                panel3.setVisible(!cipherInfo.Mode.equals("ECB"));
            }
        });
        aesAlgSelector.setSelectedIndex(0);

        final JLabel label3 = new JLabel("AES Key: ");
        aesKeyFormatSelector = new JComboBox(Utils.GetKeyFormats());
        aesKeyFormatSelector.setMaximumSize(aesKeyFormatSelector.getPreferredSize());
        aesKeyFormatSelector.setSelectedIndex(0);
        aesKeyText = new JTextField(200);
        aesKeyText.setMaximumSize(aesKeyText.getPreferredSize());

        final JLabel label4 = new JLabel("AES IV: ");
        aesIVFormatSelector = new JComboBox(Utils.GetKeyFormats());
        aesIVFormatSelector.setMaximumSize(aesIVFormatSelector.getPreferredSize());
        aesIVFormatSelector.setSelectedIndex(0);
        aesIVText = new JTextField(200);
        aesIVText.setMaximumSize(aesIVText.getPreferredSize());

        final JLabel label5 = new JLabel("Output Format: ");
        aesOutFormatSelector = new JComboBox(Utils.GetOutFormats());
        aesOutFormatSelector.setMaximumSize(aesOutFormatSelector.getPreferredSize());
        aesOutFormatSelector.setSelectedIndex(0);

        applyBtn = new JButton("Add processor");
        applyBtn.setMaximumSize(applyBtn.getPreferredSize());
        applyBtn.addActionListener(e -> {
            AesAlgorithms alg = AesAlgorithms.valueOf(aesAlgSelector.getSelectedItem().toString().replace('/', '_'));
            KeyFormat keyFormat = KeyFormat.valueOf(aesKeyFormatSelector.getSelectedItem().toString());
            KeyFormat ivFormat = KeyFormat.valueOf(aesIVFormatSelector.getSelectedItem().toString());
            OutFormat outFormat = OutFormat.valueOf(aesOutFormatSelector.getSelectedItem().toString());
            AesConfig aesConfig = new AesConfig();
            CipherInfo cipherInfo = new CipherInfo(aesAlgSelector.getSelectedItem().toString());
            aesConfig.Algorithms = alg;
            aesConfig.OutFormat = outFormat;
            try {
                aesConfig.Key = Utils.StringKeyToByteKey(aesKeyText.getText(), keyFormat);
            } catch (Exception ex) {
                System.out.println(ex);
                JOptionPane.showMessageDialog(mainPanel, "Key format error!");
                return;
            }
            if (!cipherInfo.Mode.equals("ECB"))
                try {
                    aesConfig.IV = Utils.StringKeyToByteKey(aesIVText.getText(), ivFormat);
                } catch (Exception ex) {
                    System.out.println(ex);
                    JOptionPane.showMessageDialog(mainPanel, "IV format error!");
                    return;
                }
            String extName = JOptionPane.showInputDialog("Please give this processor a special name:");
            if (extName != null) {
                if (extName.length() == 0) {
                    JOptionPane.showMessageDialog(mainPanel, "name empty!");
                    return;
                }
            } else return;
            if (parent.RegIPProcessor(extName, new AesIntruderPayloadProcessor(parent, extName, aesConfig)))
                JOptionPane.showMessageDialog(mainPanel, "Apply processor success!");
        });
        testBtn = new JButton("Test processor");
        testBtn.setMaximumSize(testBtn.getPreferredSize());
        testBtn.addActionListener(e -> {
            AesAlgorithms alg = AesAlgorithms.valueOf(aesAlgSelector.getSelectedItem().toString().replace('/', '_'));
            KeyFormat keyFormat = KeyFormat.valueOf(aesKeyFormatSelector.getSelectedItem().toString());
            KeyFormat ivFormat = KeyFormat.valueOf(aesIVFormatSelector.getSelectedItem().toString());
            OutFormat outFormat = OutFormat.valueOf(aesOutFormatSelector.getSelectedItem().toString());
            AesConfig aesConfig = new AesConfig();
            CipherInfo cipherInfo = new CipherInfo(aesAlgSelector.getSelectedItem().toString());
            aesConfig.Algorithms = alg;
            aesConfig.OutFormat = outFormat;
            try {
                aesConfig.Key = Utils.StringKeyToByteKey(aesKeyText.getText(), keyFormat);
            } catch (Exception ex) {
                System.out.println(ex);
                JOptionPane.showMessageDialog(mainPanel, "Key format error!");
                return;
            }
            if (!cipherInfo.Mode.equals("ECB"))
                try {
                    aesConfig.IV = Utils.StringKeyToByteKey(aesIVText.getText(), ivFormat);
                } catch (Exception ex) {
                    System.out.println(ex);
                    JOptionPane.showMessageDialog(mainPanel, "IV format error!");
                    return;
                }
            String testStr = JOptionPane.showInputDialog("Please give this test string:");
            if (testStr != null) {
                if (testStr.length() == 0) {
                    JOptionPane.showMessageDialog(mainPanel, "test string empty!");
                    return;
                }
            } else return;
            byte[] processPayload = (new AesIntruderPayloadProcessor(parent, testStr, aesConfig)).processPayload(testStr.getBytes(), testStr.getBytes(), testStr.getBytes());
            JOptionPane.showMessageDialog(mainPanel, processPayload.toString());
        });

        deleteBtn = new JButton("Remove processor");
        deleteBtn.setMaximumSize(deleteBtn.getPreferredSize());
        deleteBtn.addActionListener(e -> {
            String extName = JOptionPane.showInputDialog("Please enter the special name you want to delete:");
            if (extName.length() == 0) {
                JOptionPane.showMessageDialog(mainPanel, "name empty!");
                return;
            }
            parent.RemoveIPProcessor(extName);
            JOptionPane.showMessageDialog(mainPanel, "Remove success!");
        });



        panel1.add(label2);
        panel1.add(aesAlgSelector);
        panel2.add(label3);
        panel2.add(aesKeyFormatSelector);
        panel2.add(aesKeyText);
        panel3.add(label4);
        panel3.add(aesIVFormatSelector);
        panel3.add(aesIVText);
        panel4.add(label5);
        panel4.add(aesOutFormatSelector);
        panel5.add(applyBtn);
        panel5.add(testBtn);
        panel5.add(deleteBtn);

        mainPanel.add(label1);
        mainPanel.add(panel1);
        mainPanel.add(panel2);
        mainPanel.add(panel3);
        mainPanel.add(panel4);
        mainPanel.add(panel5);
        return mainPanel;
    }

    private String[] GetAesAlgs() {
        ArrayList<String> algStrs = new ArrayList<String>();
        AesAlgorithms[] algs = AesAlgorithms.values();
        for (AesAlgorithms alg : algs) {
            algStrs.add(alg.name().replace('_', '/'));
        }
        return algStrs.toArray(new String[algStrs.size()]);
    }

}
