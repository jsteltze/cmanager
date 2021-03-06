package cmanager.gui;

import cmanager.geo.Geocache;
import cmanager.geo.GeocacheComparator;
import cmanager.geo.GeocacheLog;
import cmanager.oc.ShadowList;
import cmanager.okapi.Okapi;
import cmanager.okapi.User;
import cmanager.okapi.responses.UnexpectedLogStatus;
import cmanager.settings.Settings;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;

public class CopyLogDialog extends JFrame {

    private static final long serialVersionUID = 363313395887255591L;

    private final CopyLogDialog THIS = this;
    private final JSplitPane splitPane1;
    private final JSplitPane splitPane2;
    private final JScrollPane scrollPane;

    /** Create the dialog. */
    public CopyLogDialog(
            final Geocache gc,
            final Geocache oc,
            final List<GeocacheLog> logsCopied,
            final ShadowList shadowList) {
        setResizable(true);
        Logo.setLogo(this);

        setTitle("Copy Logs");
        setBounds(100, 100, 850, 500);
        getContentPane().setLayout(new BorderLayout());
        final JPanel contentPanel = new JPanel();
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BorderLayout(0, 0));

        splitPane1 = new JSplitPane();
        contentPanel.add(splitPane1);

        splitPane2 = new JSplitPane();
        splitPane1.setRightComponent(splitPane2);

        CachePanel cachePanel = new CachePanel();
        cachePanel.setMinimumSize(new Dimension(100, 100));
        cachePanel.setCache(gc, false);
        cachePanel.colorize(oc);
        splitPane1.setLeftComponent(cachePanel);

        cachePanel = new CachePanel();
        cachePanel.setMinimumSize(new Dimension(100, 100));
        cachePanel.setCache(oc, false);
        cachePanel.colorize(gc);
        splitPane2.setLeftComponent(cachePanel);

        final JPanel panelLogs = new JPanel();
        panelLogs.setLayout(new GridBagLayout());

        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = java.awt.GridBagConstraints.BOTH;

        for (final GeocacheLog log : gc.getLogs()) {
            if (!log.isFoundLog()) {
                continue;
            }

            if (logsCopied.contains(log)) {
                continue;
            }

            final String gcUsername = Settings.getString(Settings.Key.GC_USERNAME);
            if (!log.isAuthor(gcUsername)) {
                continue;
            }

            final LogPanel logPanel = new LogPanel(log);
            panelLogs.add(logPanel, gbc);
            gbc.gridy++;

            final GridBagConstraints gbcButton = (GridBagConstraints) gbc.clone();
            gbcButton.weighty = 0;
            gbcButton.fill = 0;
            gbcButton.insets = new Insets(0, 10, 10, 0);
            gbc.gridy++;

            final JButton button = new JButton("Copy log to opencaching.de");
            if (GeocacheComparator.calculateSimilarity(gc, oc) != 1) {
                button.setBackground(Color.RED);
            }
            button.addActionListener(
                    new ActionListener() {

                        public void actionPerformed(ActionEvent actionEvent) {
                            MainWindow.actionWithWaitDialog(
                                    new Runnable() {
                                        public void run() {
                                            try {
                                                // Contribute to shadow list.
                                                shadowList.postToShadowList(gc, oc);

                                                // Retrieve the new log text.
                                                log.setText(logPanel.getLogText());

                                                // Copy the log.
                                                Okapi.postLog(User.getOKAPIUser(), oc, log);

                                                // Disable the button as this log must have been
                                                // posted successfully (otherwise an exception would
                                                // have occurred).
                                                button.setVisible(false);

                                                // Remember that we copied the log so the user can
                                                // not double post it by accident.
                                                logsCopied.add(log);
                                            } catch (UnexpectedLogStatus exception) {
                                                // Handle general log problems separately to provide
                                                // a better error message.
                                                ExceptionPanel.showErrorDialog(
                                                        THIS,
                                                        exception.getResponseMessage(),
                                                        "Unexpected log status");
                                            } catch (Throwable throwable) {
                                                ExceptionPanel.showErrorDialog(THIS, throwable);
                                            }
                                        }
                                    },
                                    THIS);
                        }

                        private Geocache oc;
                        private GeocacheLog log;

                        public ActionListener set(
                                Geocache oc, GeocacheLog log, boolean dagerousMatch) {
                            this.oc = oc;
                            this.log = log;
                            return this;
                        }
                    }.set(oc, log, false));
            panelLogs.add(button, gbcButton);
        }

        scrollPane = new JScrollPane(panelLogs);
        scrollPane.addComponentListener(
                new ComponentAdapter() {
                    public void componentResized(ComponentEvent componentEvent) {
                        scrollPane.getVerticalScrollBar().setValue(0);
                        scrollPane.getHorizontalScrollBar().setValue(0);
                    }
                });
        splitPane2.setRightComponent(scrollPane);

        final JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        getContentPane().add(buttonPane, BorderLayout.SOUTH);

        final JButton buttonReturn = new JButton("Return");
        buttonPane.add(buttonReturn);
        buttonReturn.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        THIS.setVisible(false);
                    }
                });

        THIS.addComponentListener(
                new ComponentAdapter() {
                    public void componentShown(ComponentEvent componentEvent) {
                        CacheListView.fixSplitPanes(splitPane1, splitPane2);
                    }

                    public void componentResized(ComponentEvent componentEvent) {
                        CacheListView.fixSplitPanes(splitPane1, splitPane2);
                    }
                });
    }
}
