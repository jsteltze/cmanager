package cmanager.gui;

import cmanager.CacheListController;
import cmanager.CacheListFilterCacheName;
import cmanager.CacheListFilterDifficulty;
import cmanager.CacheListFilterDistance;
import cmanager.CacheListFilterNotFoundBy;
import cmanager.CacheListFilterTerrain;
import cmanager.FileHelper;
import cmanager.geo.Geocache;
import cmanager.geo.Location;
import cmanager.geo.LocationList;
import cmanager.global.Compatibility;
import cmanager.global.Constants;
import cmanager.network.Updates;
import cmanager.okapi.Okapi;
import cmanager.okapi.User;
import cmanager.settings.Settings;
import cmanager.settings.Settings.Key;
import cmanager.util.DesktopUtil;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MenuEvent;
import javax.swing.filechooser.FileNameExtensionFilter;

public class MainWindow extends JFrame {

    private static final long serialVersionUID = 6384767256902991990L;

    private final JFrame THIS = this;
    private final JDesktopPane desktopPane;
    private final JMenu menuWindows;
    private final JComboBox<Location> comboBox;

    /** Create the frame. */
    public MainWindow() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(1050, 550));
        setLocationRelativeTo(null);
        Logo.setLogo(this);

        final JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        final JMenu menu = new JMenu("Menu");
        menuBar.add(menu);

        menuWindows = new JMenu("Windows");

        final JMenuItem menuItemOpen = new JMenuItem("Open", 
                new ImageIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/open.png"))
                        .getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
        menuItemOpen.setAccelerator(KeyStroke.getKeyStroke('O', Compatibility.SHORTCUT_KEY_MASK));
        menuItemOpen.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        openFile(true);
                    }
                });

        final JMenuItem menuItemNew = new JMenuItem("New", 
                new ImageIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/new.png"))
                        .getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
        menuItemNew.setAccelerator(KeyStroke.getKeyStroke('N', Compatibility.SHORTCUT_KEY_MASK));
        menuItemNew.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        try {
                            CacheListController.newCacheListController(
                                    desktopPane,
                                    menuWindows,
                                    (Location) comboBox.getSelectedItem(),
                                    null,
                                    geocache -> openLocationDialog(geocache));
                        } catch (Throwable ignored) {
                        }
                    }
                });
        menu.add(menuItemNew);
        menu.add(menuItemOpen);

        final JMenuItem menuItemExit = new JMenuItem("Exit");
        menuItemExit.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        THIS.dispatchEvent(new WindowEvent(THIS, WindowEvent.WINDOW_CLOSING));
                    }
                });

        final JMenuItem menuItemSettings = new JMenuItem("Settings", 
                new ImageIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/settings.png"))
                        .getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
        menuItemSettings.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        final SettingsDialog settingsDialog = new SettingsDialog(THIS);
                        settingsDialog.setModalityType(ModalityType.APPLICATION_MODAL);
                        settingsDialog.setLocationRelativeTo(THIS);
                        settingsDialog.setVisible(true);
                    }
                });

        final JMenuItem menuItemSave = new JMenuItem("Save", 
                new ImageIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/save.png"))
                        .getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
        menuItemSave.setAccelerator(KeyStroke.getKeyStroke('S', Compatibility.SHORTCUT_KEY_MASK));
        menuItemSave.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        saveFile(false);
                    }
                });
        menuItemSave.setEnabled(false);
        menu.add(menuItemSave);

        final JMenuItem menuItemSaveAs = new JMenuItem("Save As");
        menuItemSaveAs.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        saveFile(true);
                    }
                });
        menuItemSaveAs.setEnabled(false);
        menu.add(menuItemSaveAs);

        final JSeparator separator = new JSeparator();
        menu.add(separator);
        menu.add(menuItemSettings);

        final JSeparator separator1 = new JSeparator();
        menu.add(separator1);
        menu.add(menuItemExit);

        final JMenu menuList = new JMenu("List");
        menuList.setEnabled(false);
        menuBar.add(menuList);

        final JMenuItem menuItemFindOnOc = new JMenuItem("Find on OC", 
                new ImageIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/search.png"))
                        .getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
        menuItemFindOnOc.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        findOnOc(null, null);
                    }
                });
        menuList.add(menuItemFindOnOc);

        final JMenuItem menuItemSyncWithOc = new JMenuItem("Sync with OC", 
                new ImageIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/sync.png"))
                        .getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
        menuItemSyncWithOc.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        try {
                            User user;
                            String uuid;
                            try {
                                user = User.getOKAPIUser();
                                uuid = Okapi.getUuid(user);
                                if (uuid == null) {
                                    throw new NullPointerException();
                                }
                            } catch (Exception exception) {
                                JOptionPane.showMessageDialog(
                                        THIS,
                                        "Testing the OKAPI token failed. Check your settings!",
                                        "Error",
                                        JOptionPane.ERROR_MESSAGE);
                                return;
                            }

                            findOnOc(user, uuid);
                        } catch (Throwable throwable) {
                            ExceptionPanel.showErrorDialog(THIS, throwable);
                        }
                    }
                });
        menuList.add(menuItemSyncWithOc);

        final JSeparator separator2 = new JSeparator();
        menuList.add(separator2);

        final JMenuItem menuItemCopy = new JMenuItem("Copy", 
                new ImageIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/copy.png"))
                        .getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
        menuItemCopy.setAccelerator(KeyStroke.getKeyStroke('C', Compatibility.SHORTCUT_KEY_MASK));
        menuItemCopy.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        CacheListController.getTopViewCacheController(desktopPane).copySelected();
                    }
                });

        final JMenuItem menuItemSelectAll = new JMenuItem("Select All / none");
        menuItemSelectAll.setAccelerator(
                KeyStroke.getKeyStroke('A', Compatibility.SHORTCUT_KEY_MASK));
        menuItemSelectAll.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        CacheListController.getTopViewCacheController(desktopPane)
                                .getView()
                                .tableSelectAllNone();
                    }
                });
        menuList.add(menuItemSelectAll);

        final JMenuItem menuItemInvertSelection = new JMenuItem("Invert Selection");
        menuItemInvertSelection.setAccelerator(
                KeyStroke.getKeyStroke('I', Compatibility.SHORTCUT_KEY_MASK));
        menuItemInvertSelection.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        CacheListController.getTopViewCacheController(desktopPane)
                                .getView()
                                .invertTableSelection();
                    }
                });
        menuList.add(menuItemInvertSelection);

        final JSeparator separator6 = new JSeparator();
        menuList.add(separator6);
        menuList.add(menuItemCopy);

        final JMenuItem menuItemPaste = new JMenuItem("Paste", 
                new ImageIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/paste.png"))
                        .getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
        menuItemPaste.setAccelerator(KeyStroke.getKeyStroke('V', Compatibility.SHORTCUT_KEY_MASK));
        menuItemPaste.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        CacheListController.getTopViewCacheController(desktopPane).pasteSelected();
                    }
                });

        final JMenuItem menuItemCut = new JMenuItem("Cut", 
                new ImageIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/cut.png"))
                        .getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
        menuItemCut.setAccelerator(KeyStroke.getKeyStroke('X', Compatibility.SHORTCUT_KEY_MASK));
        menuItemCut.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        CacheListController.getTopViewCacheController(desktopPane).cutSelected();
                    }
                });
        menuList.add(menuItemCut);
        menuList.add(menuItemPaste);

        final JMenuItem menuItemDeleteSelectedCaches = new JMenuItem("Delete", 
                new ImageIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/delete.png"))
                        .getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
        menuItemDeleteSelectedCaches.setAccelerator(
                KeyStroke.getKeyStroke('D', Compatibility.SHORTCUT_KEY_MASK));
        menuItemDeleteSelectedCaches.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        CacheListController.getTopViewCacheController(desktopPane)
                                .removeSelectedCaches();
                    }
                });
        menuList.add(menuItemDeleteSelectedCaches);

        final JSeparator separator7 = new JSeparator();
        menuList.add(separator7);

        final JMenuItem menuItemUndo = new JMenuItem("Undo", 
                new ImageIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/undo.png"))
                        .getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
        menuItemUndo.setAccelerator(KeyStroke.getKeyStroke('Z', Compatibility.SHORTCUT_KEY_MASK));
        menuItemUndo.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        CacheListController.getTopViewCacheController(desktopPane)
                                .replayLastUndoAction();
                    }
                });
        menuList.add(menuItemUndo);

        // only enable undo menu when undo actions are available
        menuList.addMenuListener(
                new MenuAdapter() {
                    public void menuSelected(MenuEvent menuEvent) {
                        menuItemUndo.setEnabled(
                                CacheListController.getTopViewCacheController(desktopPane)
                                                .getUndoActionCount()
                                        > 0);
                    }
                });

        final JSeparator separator3 = new JSeparator();
        menuList.add(separator3);

        final JSeparator separator4 = new JSeparator();
        menuList.add(separator4);

        final JMenuItem menuItemAddFromFile = new JMenuItem("Add from File");
        menuItemAddFromFile.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        openFile(false);
                    }
                });
        menuList.add(menuItemAddFromFile);

        final JMenu menuFilter = new JMenu("Filter");
        menuFilter.setEnabled(false);
        menuBar.add(menuFilter);

        final JMenu menuItemFilterAdd = new JMenu("Add");
        menuFilter.add(menuItemFilterAdd);

        final JMenuItem menuItemFilterTerrain = new JMenuItem("Terrain");
        menuItemFilterTerrain.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        CacheListController.getTopViewCacheController(desktopPane)
                                .addFilter(new CacheListFilterTerrain());
                    }
                });
        menuItemFilterAdd.add(menuItemFilterTerrain);

        final JMenuItem menuItemFilterName = new JMenuItem("Cache name");
        menuItemFilterName.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        CacheListController.getTopViewCacheController(desktopPane)
                                .addFilter(new CacheListFilterCacheName());
                    }
                });
        menuItemFilterAdd.add(menuItemFilterName);

        final JMenuItem menuItemFilterDifficulty = new JMenuItem("Difficulty");
        menuItemFilterDifficulty.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        CacheListController.getTopViewCacheController(desktopPane)
                                .addFilter(new CacheListFilterDifficulty());
                    }
                });
        menuItemFilterAdd.add(menuItemFilterDifficulty);

        final JMenuItem menuItemFilterNotFoundBy = new JMenuItem("Not Found By");
        menuItemFilterNotFoundBy.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        CacheListController.getTopViewCacheController(desktopPane)
                                .addFilter(new CacheListFilterNotFoundBy());
                    }
                });
        menuItemFilterAdd.add(menuItemFilterNotFoundBy);

        final JSeparator separator5 = new JSeparator();
        menuFilter.add(separator5);

        final JMenuItem menuItemDeleteCachesNotInFilter =
                new JMenuItem("Delete Caches NOT in Filter");
        menuItemDeleteCachesNotInFilter.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        CacheListController.getTopViewCacheController(desktopPane)
                                .removeCachesNotInFilter();
                    }
                });
        menuFilter.add(menuItemDeleteCachesNotInFilter);

        final JMenuItem menuItemFilterDistance = new JMenuItem("Distance");
        menuItemFilterDistance.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        final CacheListFilterDistance filter = new CacheListFilterDistance();
                        CacheListController.getTopViewCacheController(desktopPane)
                                .addFilter(filter);

                        // Set current location.
                        filter.setLocation((Location) comboBox.getSelectedItem());

                        // Update filter on location change.
                        final ActionListener actionListener =
                                new ActionListener() {
                                    public void actionPerformed(ActionEvent actionEvent1) {
                                        filter.setLocation((Location) comboBox.getSelectedItem());
                                    }
                                };
                        comboBox.addActionListener(actionListener);

                        // Remove update hook on removal.
                        filter.addRemoveAction(
                                new Runnable() {
                                    public void run() {
                                        comboBox.removeActionListener(actionListener);
                                    }
                                });
                    }
                });
        menuItemFilterAdd.add(menuItemFilterDistance);

        menuWindows.setEnabled(false);
        menuBar.add(menuWindows);

        final JMenu menuInfo = new JMenu("Information");
        final JMenuItem menuItemAbout = new JMenuItem("About", 
                new ImageIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/info.png"))
                        .getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
        menuItemAbout.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        final AboutDialog dialog = new AboutDialog();
                        dialog.setLocationRelativeTo(THIS);
                        dialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
                        dialog.setVisible(true);
                    }
                });
        menuInfo.add(menuItemAbout);
        menuBar.add(menuInfo);

        final JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        desktopPane = new JDesktopPane();
        contentPane.add(desktopPane, BorderLayout.CENTER);
        desktopPane.addContainerListener(
                new ContainerListener() {
                    public void componentRemoved(ContainerEvent containerEvent) {
                        updateVisibility(desktopPane.getAllFrames().length != 0);
                    }

                    public void componentAdded(ContainerEvent containerEvent) {
                        updateVisibility(desktopPane.getAllFrames().length != 0);
                    }

                    private void updateVisibility(boolean visible) {
                        menuItemSave.setEnabled(visible);
                        menuItemSaveAs.setEnabled(visible);
                        menuList.setEnabled(visible);
                        menuFilter.setEnabled(visible);
                        menuWindows.setEnabled(visible);
                    }
                });
        desktopPane.setMinimumSize(new Dimension(200, 200));

        final JPanel panelSouth = new JPanel();
        panelSouth.setLayout(new BorderLayout(0, 0));
        contentPane.add(panelSouth, BorderLayout.SOUTH);

        final ExceptionPanel panelException = ExceptionPanel.getPanel();
        panelSouth.add(panelException, BorderLayout.NORTH);

        final JPanel panelUpdate = new JPanel();
        panelSouth.add(panelUpdate, BorderLayout.SOUTH);

        final JButton buttonUpdate = new JButton("");
        buttonUpdate.setVisible(false);
        buttonUpdate.setBorderPainted(false);
        buttonUpdate.setOpaque(false);
        buttonUpdate.setContentAreaFilled(false);
        buttonUpdate.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        DesktopUtil.openUrl(
                                "https://github.com/FriedrichFroebel/cmanager/releases");
                    }
                });
        panelUpdate.add(buttonUpdate);

        if (Settings.getString(Key.CHECK_UPDATES) != "n") {
            new SwingWorker<Void, Boolean>() {
                @Override
                protected Void doInBackground() {
                    publish(Updates.updateAvailable_block());
                    return null;
                }
    
                @Override
                protected void process(List<Boolean> chunks) {
                    // Display update message if there is another version available.
                    if (chunks.get(0)) {
                        setText(
                                "Version "
                                        + Updates.getNewVersion()
                                        + " of "
                                        + Constants.APP_NAME
                                        + " is available. Click here for updates!");
                        buttonUpdate.setVisible(true);
                    }
                }
    
                private void setText(String text) {
                    buttonUpdate.setText(
                            "<HTML><FONT color=\"#008000\"><U>" + text + "</U></FONT></HTML>");
                }
            }.execute();
        }

        final JPanel panelNorth = new JPanel();
        contentPane.add(panelNorth, BorderLayout.NORTH);
        panelNorth.setLayout(new BorderLayout(0, 0));

        final JPanel panel = new JPanel();
        panelNorth.add(panel, BorderLayout.EAST);

        final JLabel labelLocation = new JLabel("Location:");
        labelLocation.setFont(new Font("Dialog", Font.BOLD, 10));
        panel.add(labelLocation);

        comboBox = new JComboBox<>();
        comboBox.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        propagateSelectedLocationComboboxEntry();
                    }
                });
        comboBox.setFont(new Font("Dialog", Font.BOLD, 10));
        panel.add(comboBox);

        final JButton buttonEdit = new JButton("Edit");
        buttonEdit.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        openLocationDialog(null);
                    }
                });
        buttonEdit.setFont(new Font("Dialog", Font.BOLD, 10));
        panel.add(buttonEdit);

        updateLocationCombobox();
        propagateSelectedLocationComboboxEntry();

        // Store and reopen cache lists.

        this.addWindowListener(
                new WindowAdapter() {
                    public void windowClosing(WindowEvent windowEvent) {
                        try {
                            CacheListController.storePersistenceInfo(desktopPane);
                        } catch (IOException exception) {
                            ExceptionPanel.showErrorDialog(THIS, exception);
                        }
                    }
                });

        SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {
                        actionWithWaitDialog(
                                new Runnable() {
                                    public void run() {
                                        CacheListController.reopenPersistantCacheListControllers(
                                                desktopPane,
                                                menuWindows,
                                                (Location) comboBox.getSelectedItem(),
                                                new CacheListView.RunLocationDialogI() {
                                                    public void openDialog(Geocache geocache) {
                                                        openLocationDialog(geocache);
                                                    }
                                                });
                                    }
                                },
                                THIS);
                    }
                });
    }

    private void updateLocationCombobox() {
        List<Location> locations = LocationList.getList().getLocations();

        comboBox.removeAllItems();
        for (final Location location : locations) {
            comboBox.addItem(location);
        }
    }

    private void openLocationDialog(Geocache geocache) {
        final LocationDialog locationDialog = new LocationDialog(this);
        if (geocache != null) {
            locationDialog.setGeocache(geocache);
        }
        locationDialog.setLocationRelativeTo(THIS);
        locationDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        locationDialog.setVisible(true);

        if (locationDialog.modified) {
            updateLocationCombobox();
            propagateSelectedLocationComboboxEntry();
        }
    }

    private void propagateSelectedLocationComboboxEntry() {
        CacheListController.setAllRelativeLocations((Location) comboBox.getSelectedItem());
        repaint(); // Update front-most table.
    }

    private void saveFile(boolean saveAs) {
        final CacheListController cacheListController =
                CacheListController.getTopViewCacheController(desktopPane);

        String pathString = null;
        try {
            pathString = cacheListController.getPath().toString();
        } catch (Exception exception) {
            saveAs = true;
        }

        if (saveAs) {
            if (pathString == null) {
                pathString = Settings.getString(Settings.Key.FILE_CHOOSER_LOAD_GPX);
            }
            final JFileChooser chooser = new JFileChooser(pathString);
            chooser.setFileFilter(new FileNameExtensionFilter("ZIP Archive", "zip"));

            if (chooser.showSaveDialog(THIS) != JFileChooser.APPROVE_OPTION) {
                return;
            }

            pathString = chooser.getSelectedFile().getAbsolutePath();
        }

        if (!FileHelper.getFileExtension(pathString).equals("zip")) {
            pathString += ".zip";
        }

        if (saveAs) {
            final File file = new File(pathString);
            if (file.exists() && !file.isDirectory()) {
                final int dialogResult =
                        JOptionPane.showConfirmDialog(
                                THIS,
                                "The choosen file already exists. Overwrite it?",
                                "Warning",
                                JOptionPane.YES_NO_OPTION);
                if (dialogResult == JOptionPane.NO_OPTION) {
                    return;
                }
            }

            Settings.set(
                    Settings.Key.FILE_CHOOSER_LOAD_GPX,
                    Paths.get(pathString).getParent().toString());
        }

        actionWithWaitDialog(
                new Runnable() {
                    private Path path;

                    public Runnable setPath(Path path) {
                        this.path = path;
                        return this;
                    }

                    public void run() {
                        try {
                            cacheListController.store(path);
                        } catch (Throwable throwable) {
                            ExceptionPanel.showErrorDialog(THIS, throwable);
                        }
                    }
                }.setPath(Paths.get(pathString)),
                THIS);
    }

    private void openFile(final boolean createNewList) {
        String lastPath = Settings.getString(Settings.Key.FILE_CHOOSER_LOAD_GPX);
        final JFileChooser chooser = new JFileChooser(lastPath);
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setFileFilter(
                new FileNameExtensionFilter("GPS Exchange Format | ZIP Archive", "gpx", "zip"));

        if (chooser.showOpenDialog(THIS) == JFileChooser.APPROVE_OPTION) {
            lastPath = chooser.getSelectedFile().getPath();
            Settings.set(
                    Settings.Key.FILE_CHOOSER_LOAD_GPX, Paths.get(lastPath).getParent().toString());

            actionWithWaitDialog(
                    new Runnable() {
                        public void run() {
                            try {
                                if (createNewList) {
                                    CacheListController.newCacheListController(
                                            desktopPane,
                                            menuWindows,
                                            (Location) comboBox.getSelectedItem(),
                                            chooser.getSelectedFile().getAbsolutePath(),
                                            new CacheListView.RunLocationDialogI() {
                                                public void openDialog(Geocache geocache) {
                                                    openLocationDialog(geocache);
                                                }
                                            });
                                } else {
                                    CacheListController.getTopViewCacheController(desktopPane)
                                            .addFromFile(
                                                    chooser.getSelectedFile().getAbsolutePath());
                                }
                            } catch (Throwable throwable) {
                                ExceptionPanel.showErrorDialog(THIS, throwable);
                            }
                        }
                    },
                    THIS);
        }
    }

    /**
     * Display a "Please stand-by" dialog with a static delay of 25 milliseconds.
     *
     * @param task The task to execute with this wait dialog.
     * @param parent The parent component.
     */
    public static void actionWithWaitDialog(final Runnable task, Component parent) {
        MainWindow.actionWithWaitDialog(task, parent, 25);
    }

    /**
     * Display a "Please stand-by" dialog with a configurable delay.
     *
     * @param task The task to execute with this wait dialog.
     * @param parent The parent component.
     * @param delayMilliseconds The delay in milliseconds. If this time has not passed, the dialog
     *     will not be closed.
     */
    public static void actionWithWaitDialog(
            final Runnable task, Component parent, final int delayMilliseconds) {
        final WaitDialog wait = new WaitDialog();

        wait.setModalityType(ModalityType.APPLICATION_MODAL);
        wait.setLocationRelativeTo(parent);

        new Thread(
                        new Runnable() {
                            public void run() {
                                while (!wait.isVisible()) {
                                    try {
                                        Thread.sleep(delayMilliseconds);
                                    } catch (InterruptedException ignored) {
                                    }
                                }

                                task.run();
                                wait.setVisible(false);
                            }
                        })
                .start();

        wait.setVisible(true);
        wait.repaint();
    }

    private void findOnOc(User user, String uuid) {
        final DuplicateDialog duplicateDialog =
                new DuplicateDialog(
                        CacheListController.getTopViewCacheController(desktopPane).getModel(),
                        user,
                        uuid);

        FrameHelper.showModalFrame(duplicateDialog, THIS);
    }
}
