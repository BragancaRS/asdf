
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Vector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class RMIClientGui extends JFrame implements ActionListener {

    Container contentPane;
    JPanel mainPanel, connectPanel, messagePanel, centerPanel;
    JTextField hostTf, serviceTf;
    DefaultTreeModel fileList;
    JTextArea messageTA;
    JTree tree;
    JLabel hostLbl, serviceLbl;
    JButton connectBtn, sendFileBtn, saveBtn, deleteBtn, renameBtn, quitBtn;
    RMIClient myClient;
    Vector<String> filesVector;

    public RMIClientGui() {
        contentPane = getContentPane();
        mainPanel = new JPanel(new BorderLayout());
        connectPanel = new JPanel(new GridLayout(2, 3));
        messagePanel = new JPanel();
        centerPanel = new JPanel();
        centerPanel.setBackground(Color.white);

        connectPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        messageTA = new JTextArea(5, 43);
        DefaultCaret caret = (DefaultCaret) messageTA.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        messageTA.setEditable(false);

        filesVector = new Vector<String>();
        tree = new JTree();

        hostLbl = new JLabel("Host: ");
        hostTf = new JTextField(5);

        serviceLbl = new JLabel("Service name: ");
        serviceTf = new JTextField(5);

        connectBtn = new JButton("Connect");
        connectBtn.addActionListener(this);
        connectBtn.setBackground(Color.green);

        sendFileBtn = new JButton("Send File");
        sendFileBtn.setEnabled(false);
        sendFileBtn.addActionListener(this);

        saveBtn = new JButton("Save file");
        saveBtn.setEnabled(false);
        saveBtn.addActionListener(this);

        deleteBtn = new JButton("Delete file");
        deleteBtn.setEnabled(false);
        deleteBtn.addActionListener(this);

        renameBtn = new JButton("Rename file");
        renameBtn.setEnabled(false);
        renameBtn.addActionListener(this);

        quitBtn = new JButton("Quit");
        quitBtn.addActionListener(this);
        quitBtn.setBackground(Color.red);

        messagePanel.add(new JScrollPane(messageTA, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS));

        connectPanel.add(hostLbl);
        connectPanel.add(hostTf);
        connectPanel.add(serviceLbl);
        connectPanel.add(serviceTf);
        connectPanel.add(connectBtn);
        connectPanel.add(sendFileBtn);
        connectPanel.add(saveBtn);
        connectPanel.add(renameBtn);
        connectPanel.add(deleteBtn);
        connectPanel.add(quitBtn);

        mainPanel.add(connectPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(centerPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);
        mainPanel.add(messagePanel, BorderLayout.SOUTH);
        contentPane.add(mainPanel);

        setSize(550, 400);
        setLocation(300, 200);
        setVisible(true);
    }

    public void append(String message) {
        messageTA.append(message);
        this.setVisible(true);
    }

    public void refreshPanels() {
        try {
            String[] files = myClient.getFiles();

            filesVector.removeAllElements();
            for (int i = 0; i < files.length; i++) {
                filesVector.add(files[i]);
            }
            centerPanel.removeAll();
            DefaultMutableTreeNode troot = new DefaultMutableTreeNode("Files");
            fileList = new DefaultTreeModel(troot);

            for (int i = 0; i < filesVector.size(); i++) {
                DefaultMutableTreeNode tn = new DefaultMutableTreeNode(filesVector.get(i));
                troot.add(tn);
            }
            fileList.reload();
            tree = new JTree(fileList);
            tree.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent();
                    if (node == null) {
                        return;
                    }

                    if (node.getUserObject() != "Files") {
                        saveBtn.setEnabled(true);
                        deleteBtn.setEnabled(true);
                        renameBtn.setEnabled(true);

                        if (e.getClickCount() == 2) {
                            saveFile();
                        }

                    } else {
                        saveBtn.setEnabled(false);
                        deleteBtn.setEnabled(false);
                        renameBtn.setEnabled(false);
                    }
                }
            });
            centerPanel.add(tree);
            centerPanel.repaint();
            centerPanel.revalidate();
        } catch (RemoteException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        mainPanel.repaint();
        mainPanel.revalidate();

        this.setVisible(true);
    }

    public void saveFile() {
        JFileChooser chooser = new JFileChooser();

        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int choice = chooser.showOpenDialog(null);

        if (choice != JFileChooser.APPROVE_OPTION) {
            return;
        }

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent();

        String filename = (String) node.getUserObject();

        try {
            myClient.saveFile(chooser.getSelectedFile().getPath(), filename);
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    public void sendFile() {
        JFileChooser chooser = new JFileChooser();

        int choice = chooser.showOpenDialog(null);

        if (choice != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File chosenFile = chooser.getSelectedFile();

        byte[] array;
        try {
            array = Files.readAllBytes(chosenFile.toPath());
            append("\nSending " + chosenFile.getName() + "...");
            myClient.sendFile(chosenFile.getName(), array);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        refreshPanels();
    }

    public void deleteFile() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent();

        String nodeInfo = (String) node.getUserObject();

        int answer = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete " + nodeInfo + "?");

        if (answer == 0) {
            try {
                myClient.deleteFile(nodeInfo);
            } catch (RemoteException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            refreshPanels();
        } else {

        }
    }

    public void renameFile() {

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent();

        String filename = (String) node.getUserObject();

        String newFilename = (String) JOptionPane.showInputDialog(this, "Rename file:", "Rename\n", JOptionPane.OK_OPTION, null, null, filename);

        while (newFilename == null) {
            newFilename = (String) JOptionPane.showInputDialog(this, "Please click OK or rename file:", "Rename\n", JOptionPane.OK_OPTION, null, null, filename);
        }

        try {
            myClient.rename(filename, newFilename);
        } catch (RemoteException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        refreshPanels();
    }

    public static void main(String[] args) {
        RMIClientGui myGui = new RMIClientGui();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == connectBtn) {
            if (connectBtn.getText() == "Connect") {
                myClient = new RMIClient(this, hostTf.getText(), serviceTf.getText());

                refreshPanels();

                connectBtn.setText("Disconnect");
                sendFileBtn.setEnabled(true);
            } else {
                myClient = null;

                append("\nDisconnected..");

                deleteBtn.setEnabled(false);
                renameBtn.setEnabled(false);
                saveBtn.setEnabled(false);
                sendFileBtn.setEnabled(false);

                connectBtn.setText("Connect");
            }
        }

        try {
            if (e.getSource() == sendFileBtn) {
                sendFile();
            }

            if (e.getSource() == saveBtn) {
                saveFile();
            }

            if (e.getSource() == deleteBtn) {
                deleteFile();
            }

            if (e.getSource() == renameBtn) {
                renameFile();
            }

            if (e.getSource() == quitBtn) {
                System.exit(0);
            }
        } catch (NullPointerException nullEx) {
            JOptionPane.showMessageDialog(this, "Please choose a file.");
        }

    }
}
