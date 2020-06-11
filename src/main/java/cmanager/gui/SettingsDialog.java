package cmanager.gui;

import cmanager.global.Constants;
import cmanager.okapi.Okapi;
import cmanager.okapi.User;
import cmanager.settings.Settings;
import cmanager.util.DesktopUtil;
import cmanager.util.ForkUtil;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class SettingsDialog extends JDialog {

    private static final long serialVersionUID = -6008083400079798934L;

    private final JDialog THIS = this;
    private final JLabel labelOkapiToken;
    private final JLabel labelUsernameOc;
    private final JButton buttonRequestNewToken;
    private final JTextField textUsernameGc = new JTextField(30);
    private final JTextField textHeapSize;
    private final JTextField textProxyHost = new JTextField();
    private final JTextField textProxyPort = new JTextField(4);
    private final JCheckBox checkUpdates = new JCheckBox("Check for Updates when Starting", true);

    /** Create the frame. */
    public SettingsDialog(JFrame owner) {
        super(owner);

        setTitle("Settings");

        final JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 0));

        final JPanel panelButtons = new JPanel();
        contentPane.add(panelButtons, BorderLayout.SOUTH);
        panelButtons.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));

        final JButton buttonSaveApply = new JButton("Save & Apply");
        buttonSaveApply.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        boolean changesWhichNeedRestart = false;

                        final String newHeapSize = textHeapSize.getText();
                        final String oldHeapSize = Settings.getString(Settings.Key.HEAP_SIZE);
                        if ((oldHeapSize != null && !oldHeapSize.equals(newHeapSize))
                                || (oldHeapSize == null && newHeapSize.length() > 0)) {
                            changesWhichNeedRestart = true;
                        }
                        
                        final String newProxyHost = textProxyHost.getText();
                        final String newProxyPort = textProxyPort.getText();
                        final boolean checkUpdate = checkUpdates.isSelected();

                        Settings.set(Settings.Key.GC_USERNAME, textUsernameGc.getText());
                        Settings.set(Settings.Key.HEAP_SIZE, newHeapSize);
                        Settings.set(Settings.Key.PROXY_HOST, newProxyHost);
                        Settings.set(Settings.Key.PROXY_PORT, newProxyPort);
                        Settings.set(Settings.Key.CHECK_UPDATES, checkUpdate ? "y" : "n");

                        if (changesWhichNeedRestart) {
                            final String message =
                                    "You have made changes which need cmanager to restart in order be applied.\n"
                                            + "Do you want to restart "
                                            + Constants.APP_NAME
                                            + " now?";
                            final int dialogResult =
                                    JOptionPane.showConfirmDialog(
                                            THIS,
                                            message,
                                            "Restart " + Constants.APP_NAME + " now?",
                                            JOptionPane.YES_NO_OPTION);
                            if (dialogResult == JOptionPane.YES_OPTION) {
                                try {
                                    ForkUtil.runCopyAndExit();
                                } catch (Throwable t) {
                                    ExceptionPanel.showErrorDialog(THIS, t);
                                }
                            }
                        }

                        THIS.setVisible(false);
                    }
                });
        panelButtons.add(buttonSaveApply);

        final JButton buttonDiscard = new JButton("Discard");
        buttonDiscard.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        THIS.setVisible(false);
                    }
                });
        panelButtons.add(buttonDiscard);

        final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        contentPane.add(tabbedPane, BorderLayout.CENTER);

        final JPanel panelOc = new JPanel(new BorderLayout());
        final JPanel panelOcInner = new JPanel(new GridLayout(3, 2));
        panelOcInner.setBackground(Color.white);
        panelOc.setBorder(new EmptyBorder(10, 10, 10, 10));
        tabbedPane.addTab("opencaching.de", null, panelOc, null);
        panelOc.setBackground(Color.white);
        panelOc.add(panelOcInner, BorderLayout.NORTH);

        final JLabel labelOkapiTokenText = new JLabel("OKAPI Token:   ", JLabel.RIGHT);
        panelOcInner.add(labelOkapiTokenText);

        labelOkapiToken = new JLabel("New label");
        panelOcInner.add(labelOkapiToken);
        panelOcInner.add(new JLabel());

        buttonRequestNewToken = new JButton("Request new token");
        buttonRequestNewToken.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        try {
                            User.getOKAPIUser()
                                    .requestOkapiToken(
                                            new Okapi.RequestAuthorizationCallbackI() {
                                                @Override
                                                public String getPin() {
                                                    return JOptionPane.showInputDialog(
                                                            null,
                                                            "Please look at your browser and enter the PIN from opencaching.de");
                                                }

                                                @Override
                                                public void redirectUrlToUser(String authUrl) {
                                                    DesktopUtil.openUrl(authUrl);
                                                }
                                            });
                            displayOkapiTokenStatus();
                        } catch (Throwable throwable) {
                            ExceptionPanel.showErrorDialog(THIS, throwable);
                        }
                    }
                });
        panelOcInner.add(buttonRequestNewToken);

        final JLabel labelUsernameOcText = new JLabel("OC Username:   ", JLabel.RIGHT);
        panelOcInner.add(labelUsernameOcText);

        labelUsernameOc = new JLabel("");
        labelUsernameOc.setHorizontalAlignment(SwingConstants.LEFT);
        labelUsernameOc.setText(Settings.getString(Settings.Key.OC_USERNAME));
        panelOcInner.add(labelUsernameOc);

        final JPanel panelGc = new JPanel(new BorderLayout());
        final JPanel panelGcInner = new JPanel(new GridLayout(3, 1));
        tabbedPane.addTab("geocaching.com", null, panelGc, null);
        panelGc.setBackground(Color.white);
        panelGc.setBorder(new EmptyBorder(10, 10, 10, 10));
        panelGc.add(panelGcInner, BorderLayout.NORTH);
        panelGcInner.setBackground(Color.white);

        final JLabel labelUsername = new JLabel("Username: ");
        panelGcInner.add(labelUsername);
        panelGcInner.add(textUsernameGc);

        displayOkapiTokenStatus();
        textUsernameGc.setText(Settings.getString(Settings.Key.GC_USERNAME));

        final JPanel panelGeneral = new JPanel();
        panelGeneral.setBorder(new EmptyBorder(10, 10, 10, 10));
        tabbedPane.addTab("General", null, panelGeneral, null);
        final GridBagLayout gblPanelGeneral = new GridBagLayout();
        gblPanelGeneral.rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0};
        gblPanelGeneral.columnWeights = new double[] {1.0, 1.0, 0.0};
        panelGeneral.setLayout(gblPanelGeneral);
        panelGeneral.setBackground(Color.white);

        final Component verticalStrut1 = Box.createVerticalStrut(20);
        final GridBagConstraints gbcVerticalStrut1 = new GridBagConstraints();
        gbcVerticalStrut1.weighty = 0.1;
        gbcVerticalStrut1.insets = new Insets(0, 0, 5, 5);
        gbcVerticalStrut1.gridx = 0;
        gbcVerticalStrut1.gridy = 0;
        panelGeneral.add(verticalStrut1, gbcVerticalStrut1);

        final JLabel labelCurrentHeapSize = new JLabel("$size");
        final GridBagConstraints gbcLabelCurrentHeapSize = new GridBagConstraints();
        gbcLabelCurrentHeapSize.insets = new Insets(0, 0, 5, 5);
        gbcLabelCurrentHeapSize.gridx = 1;
        gbcLabelCurrentHeapSize.gridy = 1;
        panelGeneral.add(labelCurrentHeapSize, gbcLabelCurrentHeapSize);
        labelCurrentHeapSize.setText(
                Long.valueOf(Runtime.getRuntime().maxMemory() / 1024 / 1024).toString());

        final Component verticalStrut = Box.createVerticalStrut(20);
        final GridBagConstraints gbcVerticalStrut = new GridBagConstraints();
        gbcVerticalStrut.weighty = 0.5;
        gbcVerticalStrut.insets = new Insets(0, 0, 5, 5);
        gbcVerticalStrut.gridx = 0;
        gbcVerticalStrut.gridy = 3;
        panelGeneral.add(verticalStrut, gbcVerticalStrut);

        final JLabel labelApplicationRestartRequired =
                new JLabel("(*) Application restart required.");
        labelApplicationRestartRequired.setFont(new Font("Dialog", Font.PLAIN, 11));
        final GridBagConstraints gbcLabelApplicationRestartRequired = new GridBagConstraints();
        gbcLabelApplicationRestartRequired.gridwidth = 3;
        gbcLabelApplicationRestartRequired.anchor = GridBagConstraints.ABOVE_BASELINE;
        gbcLabelApplicationRestartRequired.gridx = 0;
        gbcLabelApplicationRestartRequired.gridy = 4;
        panelGeneral.add(labelApplicationRestartRequired, gbcLabelApplicationRestartRequired);

        textHeapSize = new JTextField();
        textHeapSize.setHorizontalAlignment(SwingConstants.CENTER);
        final GridBagConstraints gbcTextHeapSize = new GridBagConstraints();
        gbcTextHeapSize.insets = new Insets(0, 0, 5, 0);
        gbcTextHeapSize.gridwidth = 2;
        gbcTextHeapSize.weightx = 1.0;
        gbcTextHeapSize.anchor = GridBagConstraints.NORTHWEST;
        gbcTextHeapSize.gridx = 1;
        gbcTextHeapSize.gridy = 2;
        gbcTextHeapSize.fill = GridBagConstraints.HORIZONTAL;
        panelGeneral.add(textHeapSize, gbcTextHeapSize);
        textHeapSize.setColumns(10);
        textHeapSize.setText(Settings.getString(Settings.Key.HEAP_SIZE));

        final JLabel labelHeapSizeText = new JLabel("Heap size* (MB):");
        labelHeapSizeText.setHorizontalAlignment(SwingConstants.CENTER);
        final GridBagConstraints gbcLabelHeapSizeText = new GridBagConstraints();
        gbcLabelHeapSizeText.insets = new Insets(0, 0, 5, 5);
        gbcLabelHeapSizeText.anchor = GridBagConstraints.NORTHWEST;
        gbcLabelHeapSizeText.gridy = 2;
        gbcLabelHeapSizeText.gridx = 0;
        gbcLabelHeapSizeText.fill = GridBagConstraints.HORIZONTAL;
        panelGeneral.add(labelHeapSizeText, gbcLabelHeapSizeText);

        final JLabel labelCurrentHeapSizeText = new JLabel("Current heap size (MB):");
        final GridBagConstraints gbcLabelCurrentHeapSizeText = new GridBagConstraints();
        gbcLabelCurrentHeapSizeText.insets = new Insets(0, 0, 5, 5);
        gbcLabelCurrentHeapSizeText.gridx = 0;
        gbcLabelCurrentHeapSizeText.gridy = 1;
        panelGeneral.add(labelCurrentHeapSizeText, gbcLabelCurrentHeapSizeText);
        
        final JPanel panelNetwork = new JPanel(new BorderLayout());
        final JPanel panelNetworkInner = new JPanel(new GridLayout(4, 1));
        panelNetworkInner.setBackground(Color.white);
        panelNetwork.setBorder(new EmptyBorder(10, 10, 10, 10));
        panelNetwork.setBackground(Color.white);
        panelNetwork.add(panelNetworkInner, BorderLayout.NORTH);
        
        textProxyHost.setText(Settings.getString(Settings.Key.PROXY_HOST));
        textProxyPort.setText(Settings.getString(Settings.Key.PROXY_PORT));
        checkUpdates.setSelected(Settings.getString(Settings.Key.CHECK_UPDATES) != "n");
        checkUpdates.setBackground(Color.white);
        
        panelNetworkInner.add(new JLabel("Proxy Host & Port:"));
        JPanel proxyPanel = new JPanel(new BorderLayout());
        proxyPanel.add(textProxyHost, BorderLayout.CENTER);
        proxyPanel.add(textProxyPort, BorderLayout.EAST);
        panelNetworkInner.add(proxyPanel);
        //panelNetworkInner.add(new JLabel("Example: http://myproxy:8080"));
        panelNetworkInner.add(new JLabel());
        panelNetworkInner.add(checkUpdates);
        tabbedPane.addTab("Network", null, panelNetwork, null);
        
        pack();
    }

    private void displayOkapiTokenStatus() {
        labelOkapiToken.setText("missing or offline");
        labelOkapiToken.setIcon(new ImageIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/nok.png"))
                .getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH)));
        Font font = labelOkapiToken.getFont();
        labelOkapiToken.setFont(font.deriveFont(font.getStyle() | Font.ITALIC));
        buttonRequestNewToken.setVisible(true);

        final User user = User.getOKAPIUser();
        try {
            if (user.getOkapiToken() != null && Okapi.getUuid(user) != null) {
                labelOkapiToken.setIcon(new ImageIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/okay.png"))
                        .getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH)));
                font = labelOkapiToken.getFont();
                labelOkapiToken.setText("");
                buttonRequestNewToken.setVisible(false);

                final String username = Okapi.getUsername(user);
                Settings.set(Settings.Key.OC_USERNAME, username);
                labelUsernameOc.setText(username);
            }
        } catch (Exception ignored) {
        }
    }
}
