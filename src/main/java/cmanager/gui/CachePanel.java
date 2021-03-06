/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cmanager.gui;

import cmanager.geo.Geocache;
import cmanager.geo.GeocacheLog;
import cmanager.util.DesktopUtil;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import javax.swing.JViewport;

public class CachePanel extends javax.swing.JPanel {

    private static final long serialVersionUID = -4848832298041708795L;

    private Geocache geocache = null;

    public void setCache(Geocache geocache) {
        setCache(geocache, true);
    }

    public void setCache(Geocache geocache, boolean showLogs) {
        this.geocache = geocache;

        if (this.geocache == null) {
            panelListing.setVisible(false);
        } else {
            final String coordinates =
                    this.geocache.getCoordinate() != null
                            ? this.geocache.getCoordinate().toString()
                            : null;
            lblCoordinates.setText(coordinates);

            lblStatus.setText(this.geocache.getStatusAsString());
            labelName.setText(this.geocache.getName());
            lblCode.setText(this.geocache.getCode());
            lblOwner.setText(this.geocache.getOwner());
            lblType.setText(this.geocache.getType().asNiceType());
            lblDifficulty.setText(this.geocache.getDifficulty().toString());
            lblTerrain.setText(this.geocache.getTerrain().toString());
            lblContainer.setText(this.geocache.getContainer().asGc());

            String listing = this.geocache.getListingShort();
            if (listing != null && !listing.equals("")) {
                listing += "<br><br>";
            } else {
                listing = "";
            }
            listing = "<html>" + listing + this.geocache.getListing() + "</html>";

            editorListing.setContentType("text/html");
            editorListing.setText(listing);

            panelLogs.removeAll();

            if (showLogs) {
                final GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.weightx = 1;
                gbc.weighty = 1;
                gbc.anchor = GridBagConstraints.WEST;
                gbc.fill = GridBagConstraints.BOTH;
                for (final GeocacheLog log : this.geocache.getLogs()) {
                    panelLogs.add(new LogPanel(log), gbc);
                    gbc.gridy++;
                }
                panelLogs.validate();
            }

            adjustToOptimalWidth();

            panelListing.setVisible(true);
        }
    }

    public void colorize(Geocache geocache2) {
        if (geocache.getCoordinate() != null) {
            colorize(lblCoordinates, geocache.getCoordinate().equals(geocache2.getCoordinate()));
        }
        if (geocache.getStatusAsString() != null) {
            colorize(lblStatus, geocache.getStatusAsString().equals(geocache2.getStatusAsString()));
        }
        if (geocache.getName() != null) {
            colorize(labelName, geocache.getName().equals(geocache2.getName()));
        }
        if (geocache.getOwner() != null) {
            colorize(lblOwner, geocache.getOwner().equals(geocache2.getOwner()));
        }

        colorize(lblType, geocache.getType().equals(geocache2.getType()));
        colorize(
                lblDifficulty,
                Double.compare(geocache.getDifficulty(), geocache2.getDifficulty()) == 0);
        colorize(lblTerrain, Double.compare(geocache.getTerrain(), geocache2.getTerrain()) == 0);
        colorize(lblContainer, geocache.getContainer().equals(geocache2.getContainer()));
    }

    private void colorize(JLabel label, boolean good) {
        label.setOpaque(true);
        if (good) {
            label.setBackground(Color.GREEN);
        } else {
            label.setBackground(Color.RED);
        }
    }

    public void adjustToOptimalWidth() {
        int width = jScrollPane.getViewport().getVisibleRect().width;
        if (width <= 0) {
            return;
        }

        final int scroll = jScrollPane.getVerticalScrollBar().getWidth() + 20;
        width -= scroll;

        Dimension dimension = new Dimension(width, panelHeading.getSize().height);
        panelHeading.setSize(dimension);
        panelHeading.setPreferredSize(dimension);
        dimension = labelName.getMinimumSize();
        dimension.width += 20;
        panelHeading.setMinimumSize(dimension);
        panelHeading.validate();

        dimension = new Dimension(width, panelFooter.getSize().height);
        panelFooter.setSize(dimension);
        panelFooter.setPreferredSize(dimension);
        panelFooter.validate();

        panelListing.validate();
        panelLogs.validate();
    }

    /** Creates new form CachePanel2 */
    public CachePanel() {
        initComponents();

        this.addComponentListener(
                new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent componentEvent) {
                        adjustToOptimalWidth();
                    }
                });

        panelListingText.addComponentListener(
                new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent componentEvent) {
                        jScrollPane.getVerticalScrollBar().setValue(0);
                        jScrollPane.getHorizontalScrollBar().setValue(0);
                    }
                });
        jScrollPane.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);

        setCache(null);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT
     * modify this code. The content of this method is always regenerated by the Form Editor.
     */

    // <editor-fold defaultstate="collapsed" desc="Generated
    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane = new javax.swing.JScrollPane();
        panelListing = new javax.swing.JPanel();
        panelHeading = new javax.swing.JPanel();
        labelName = new javax.swing.JLabel();
        lblCode = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        lblType = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lblDifficulty = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        lblTerrain = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lblContainer = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lblOwner = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        lblCoordinates = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        panelFooter = new javax.swing.JPanel();
        btnOnline = new javax.swing.JButton();
        panelListingText = new javax.swing.JPanel();
        editorListing = new javax.swing.JEditorPane();
        panelLogs = new javax.swing.JPanel();

        setName("panelListing"); // NOI18N
        addComponentListener(
                new java.awt.event.ComponentAdapter() {
                    public void componentResized(java.awt.event.ComponentEvent evt) {
                        formComponentResized(evt);
                    }
                });
        setLayout(new java.awt.BorderLayout());

        jScrollPane.setAlignmentX(0.0F);
        jScrollPane.setAlignmentY(0.0F);
        jScrollPane.addComponentListener(
                new java.awt.event.ComponentAdapter() {
                    public void componentShown(java.awt.event.ComponentEvent evt) {
                        jScrollPaneComponentShown(evt);
                    }
                });

        panelListing.setAutoscrolls(true);
        panelListing.setMaximumSize(new java.awt.Dimension(400, 400));
        panelListing.addMouseListener(
                new java.awt.event.MouseAdapter() {
                    public void mouseClicked(java.awt.event.MouseEvent evt) {
                        panelListingMouseClicked(evt);
                    }
                });

        panelHeading.setAlignmentX(1.0F);
        panelHeading.setAlignmentY(0.0F);
        panelHeading.addComponentListener(
                new java.awt.event.ComponentAdapter() {
                    public void componentResized(java.awt.event.ComponentEvent evt) {
                        panelHeadingComponentResized(evt);
                    }
                });

        labelName.setBackground(new java.awt.Color(102, 255, 51));
        labelName.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        labelName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelName.setText("lblName");
        labelName.setName("lblName"); // NOI18N

        lblCode.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCode.setText("lblCode");

        jLabel1.setText("Type: ");

        lblType.setText("lblType");

        jLabel2.setText("Difficulty: ");

        lblDifficulty.setText("lblDifficulty");

        jLabel3.setText("Terrain: ");

        lblTerrain.setText("lblTerrain");

        jLabel4.setText("Container:");

        lblContainer.setText("lblContainer");

        jLabel5.setText("Owner:");

        lblOwner.setText("lblOwner");

        jLabel6.setText("Coordinates:");

        lblCoordinates.setText("lblCoordinates");

        jLabel7.setText("Status:");

        lblStatus.setText("lblStatus");

        javax.swing.GroupLayout panelHeadingLayout = new javax.swing.GroupLayout(panelHeading);
        panelHeading.setLayout(panelHeadingLayout);
        panelHeadingLayout.setHorizontalGroup(
                panelHeadingLayout
                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(
                                panelHeadingLayout
                                        .createSequentialGroup()
                                        .addGap(12, 12, 12)
                                        .addComponent(
                                                labelName,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                Short.MAX_VALUE))
                        .addGroup(
                                panelHeadingLayout
                                        .createSequentialGroup()
                                        .addGap(12, 12, 12)
                                        .addComponent(
                                                lblCode,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                512,
                                                Short.MAX_VALUE))
                        .addGroup(
                                panelHeadingLayout
                                        .createSequentialGroup()
                                        .addGap(20, 20, 20)
                                        .addGroup(
                                                panelHeadingLayout
                                                        .createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment
                                                                        .LEADING)
                                                        .addGroup(
                                                                panelHeadingLayout
                                                                        .createSequentialGroup()
                                                                        .addGroup(
                                                                                panelHeadingLayout
                                                                                        .createParallelGroup(
                                                                                                javax
                                                                                                        .swing
                                                                                                        .GroupLayout
                                                                                                        .Alignment
                                                                                                        .LEADING)
                                                                                        .addComponent(
                                                                                                jLabel4)
                                                                                        .addComponent(
                                                                                                jLabel2)
                                                                                        .addComponent(
                                                                                                jLabel1)
                                                                                        .addComponent(
                                                                                                jLabel3)
                                                                                        .addComponent(
                                                                                                jLabel7))
                                                                        .addContainerGap(
                                                                                javax.swing
                                                                                        .GroupLayout
                                                                                        .DEFAULT_SIZE,
                                                                                Short.MAX_VALUE))
                                                        .addGroup(
                                                                panelHeadingLayout
                                                                        .createSequentialGroup()
                                                                        .addGroup(
                                                                                panelHeadingLayout
                                                                                        .createParallelGroup(
                                                                                                javax
                                                                                                        .swing
                                                                                                        .GroupLayout
                                                                                                        .Alignment
                                                                                                        .LEADING)
                                                                                        .addComponent(
                                                                                                jLabel6)
                                                                                        .addComponent(
                                                                                                jLabel5))
                                                                        .addPreferredGap(
                                                                                javax.swing
                                                                                        .LayoutStyle
                                                                                        .ComponentPlacement
                                                                                        .UNRELATED)
                                                                        .addGroup(
                                                                                panelHeadingLayout
                                                                                        .createParallelGroup(
                                                                                                javax
                                                                                                        .swing
                                                                                                        .GroupLayout
                                                                                                        .Alignment
                                                                                                        .LEADING)
                                                                                        .addComponent(
                                                                                                lblOwner)
                                                                                        .addComponent(
                                                                                                lblType)
                                                                                        .addComponent(
                                                                                                lblCoordinates)
                                                                                        .addComponent(
                                                                                                lblDifficulty)
                                                                                        .addComponent(
                                                                                                lblTerrain)
                                                                                        .addComponent(
                                                                                                lblContainer)
                                                                                        .addComponent(
                                                                                                lblStatus))
                                                                        .addGap(
                                                                                0,
                                                                                0,
                                                                                Short
                                                                                        .MAX_VALUE)))));
        panelHeadingLayout.setVerticalGroup(
                panelHeadingLayout
                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(
                                panelHeadingLayout
                                        .createSequentialGroup()
                                        .addGap(12, 12, 12)
                                        .addComponent(labelName)
                                        .addGap(6, 6, 6)
                                        .addComponent(lblCode)
                                        .addGap(12, 12, 12)
                                        .addGroup(
                                                panelHeadingLayout
                                                        .createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment
                                                                        .BASELINE)
                                                        .addComponent(jLabel6)
                                                        .addComponent(lblCoordinates))
                                        .addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(
                                                panelHeadingLayout
                                                        .createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment
                                                                        .BASELINE)
                                                        .addComponent(jLabel1)
                                                        .addComponent(lblType))
                                        .addGap(6, 6, 6)
                                        .addGroup(
                                                panelHeadingLayout
                                                        .createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment
                                                                        .BASELINE)
                                                        .addComponent(jLabel2)
                                                        .addComponent(lblDifficulty))
                                        .addGap(6, 6, 6)
                                        .addGroup(
                                                panelHeadingLayout
                                                        .createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment
                                                                        .BASELINE)
                                                        .addComponent(jLabel3)
                                                        .addComponent(lblTerrain))
                                        .addGap(6, 6, 6)
                                        .addGroup(
                                                panelHeadingLayout
                                                        .createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment
                                                                        .BASELINE)
                                                        .addComponent(jLabel4)
                                                        .addComponent(lblContainer))
                                        .addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(
                                                panelHeadingLayout
                                                        .createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment
                                                                        .BASELINE)
                                                        .addComponent(jLabel7)
                                                        .addComponent(lblStatus))
                                        .addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(
                                                panelHeadingLayout
                                                        .createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment
                                                                        .BASELINE)
                                                        .addComponent(jLabel5)
                                                        .addComponent(lblOwner))
                                        .addContainerGap(
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                Short.MAX_VALUE)));

        btnOnline.setText("View Online");
        btnOnline.addActionListener(
                new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        btnOnlineActionPerformed(evt);
                    }
                });

        javax.swing.GroupLayout panelFooterLayout = new javax.swing.GroupLayout(panelFooter);
        panelFooter.setLayout(panelFooterLayout);
        panelFooterLayout.setHorizontalGroup(
                panelFooterLayout
                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(
                                javax.swing.GroupLayout.Alignment.TRAILING,
                                panelFooterLayout
                                        .createSequentialGroup()
                                        .addContainerGap(374, Short.MAX_VALUE)
                                        .addComponent(btnOnline)
                                        .addContainerGap()));
        panelFooterLayout.setVerticalGroup(
                panelFooterLayout
                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(
                                javax.swing.GroupLayout.Alignment.TRAILING,
                                panelFooterLayout
                                        .createSequentialGroup()
                                        .addContainerGap(
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                Short.MAX_VALUE)
                                        .addComponent(btnOnline)
                                        .addContainerGap()));

        editorListing.setEditable(false);

        javax.swing.GroupLayout panelListingTextLayout =
                new javax.swing.GroupLayout(panelListingText);
        panelListingText.setLayout(panelListingTextLayout);
        panelListingTextLayout.setHorizontalGroup(
                panelListingTextLayout
                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 548, Short.MAX_VALUE)
                        .addGroup(
                                panelListingTextLayout
                                        .createParallelGroup(
                                                javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(
                                                editorListing,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                524,
                                                Short.MAX_VALUE)));
        panelListingTextLayout.setVerticalGroup(
                panelListingTextLayout
                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 87, Short.MAX_VALUE)
                        .addGroup(
                                panelListingTextLayout
                                        .createParallelGroup(
                                                javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(
                                                editorListing,
                                                javax.swing.GroupLayout.Alignment.TRAILING,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                87,
                                                Short.MAX_VALUE)));

        panelLogs.setLayout(new java.awt.GridBagLayout());

        javax.swing.GroupLayout panelListingLayout = new javax.swing.GroupLayout(panelListing);
        panelListing.setLayout(panelListingLayout);
        panelListingLayout.setHorizontalGroup(
                panelListingLayout
                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(
                                panelListingLayout
                                        .createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                panelListingLayout
                                                        .createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment
                                                                        .LEADING)
                                                        .addComponent(
                                                                panelFooter,
                                                                javax.swing.GroupLayout
                                                                        .PREFERRED_SIZE,
                                                                javax.swing.GroupLayout
                                                                        .DEFAULT_SIZE,
                                                                javax.swing.GroupLayout
                                                                        .PREFERRED_SIZE)
                                                        .addComponent(
                                                                panelHeading,
                                                                javax.swing.GroupLayout
                                                                        .PREFERRED_SIZE,
                                                                javax.swing.GroupLayout
                                                                        .DEFAULT_SIZE,
                                                                javax.swing.GroupLayout
                                                                        .PREFERRED_SIZE)
                                                        .addComponent(
                                                                panelLogs,
                                                                javax.swing.GroupLayout
                                                                        .PREFERRED_SIZE,
                                                                523,
                                                                javax.swing.GroupLayout
                                                                        .PREFERRED_SIZE)
                                                        .addComponent(
                                                                panelListingText,
                                                                javax.swing.GroupLayout.Alignment
                                                                        .TRAILING,
                                                                javax.swing.GroupLayout
                                                                        .DEFAULT_SIZE,
                                                                javax.swing.GroupLayout
                                                                        .DEFAULT_SIZE,
                                                                Short.MAX_VALUE))
                                        .addContainerGap()));
        panelListingLayout.setVerticalGroup(
                panelListingLayout
                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(
                                panelListingLayout
                                        .createSequentialGroup()
                                        .addComponent(
                                                panelHeading,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(
                                                panelFooter,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement
                                                        .UNRELATED)
                                        .addComponent(
                                                panelListingText,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement
                                                        .UNRELATED)
                                        .addComponent(
                                                panelLogs,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                90,
                                                Short.MAX_VALUE)
                                        .addContainerGap()));

        jScrollPane.setViewportView(panelListing);

        add(jScrollPane, java.awt.BorderLayout.CENTER);
    } // </editor-fold>//GEN-END:initComponents

    private void formComponentResized(
            ComponentEvent componentEvent) { // GEN-FIRST:event_formComponentResized
    } // GEN-LAST:event_formComponentResized

    private void btnOnlineActionPerformed(
            ActionEvent actionEvent) { // GEN-FIRST:event_btnOnlineActionPerformed
        DesktopUtil.openUrl(geocache.getUrl());
    } // GEN-LAST:event_btnOnlineActionPerformed

    private void panelHeadingComponentResized(
            ComponentEvent componentEvent) { // GEN-FIRST:event_panelHeadingComponentResized
        jScrollPane.getVerticalScrollBar().setValue(0);
    } // GEN-LAST:event_panelHeadingComponentResized

    private void jScrollPaneComponentShown(
            ComponentEvent componentEvent) { // GEN-FIRST:event_jScrollPaneComponentShown
    } // GEN-LAST:event_jScrollPaneComponentShown

    private void panelListingMouseClicked(
            MouseEvent mouseEvent) { // GEN-FIRST:event_panelListingMouseClicked
    } // GEN-LAST:event_panelListingMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnOnline;
    private javax.swing.JEditorPane editorListing;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane;
    private javax.swing.JLabel lblCode;
    private javax.swing.JLabel lblContainer;
    private javax.swing.JLabel lblCoordinates;
    private javax.swing.JLabel lblDifficulty;
    private javax.swing.JLabel labelName;
    private javax.swing.JLabel lblOwner;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblTerrain;
    private javax.swing.JLabel lblType;
    private javax.swing.JPanel panelFooter;
    private javax.swing.JPanel panelHeading;
    private javax.swing.JPanel panelListing;
    private javax.swing.JPanel panelListingText;
    private javax.swing.JPanel panelLogs;
    // End of variables declaration//GEN-END:variables
}
