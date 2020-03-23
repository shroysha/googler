package dev.shroysha.widgets.googler;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GooglerWidgetMain {

    private TrayIcon icon;
    private BufferedImage iconImage;
    private boolean oneOpen = false;

    public GooglerWidgetMain() {
        super();

        init();
    }


    public static void main(String[] args) {
        try {
            GooglerWidgetMain googler = new GooglerWidgetMain();
            googler.show();
        } catch (AWTException ex) {
            Logger.getLogger(GooglerWidgetMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void init() {
        try {
            iconImage = ImageIO.read(GooglerWidgetMain.class.getResource("icon.jpg"));

            icon = new TrayIcon(iconImage);
            /*icon.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent ae) {
                    showFrame();
                }
            });*/
            icon.addMouseListener(new MouseAdapter() {

                public void mouseClicked(MouseEvent me) {
                    super.mouseClicked(me);
                    showFrame(me);
                }

            });

            icon.setPopupMenu(new MyPopUpMenu());

        } catch (IOException ex) {
            Logger.getLogger(GooglerWidgetMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void showFrame(MouseEvent me) {

        if (!oneOpen) {
            GooglerFrame frame = new GooglerFrame();
            int x = me.getXOnScreen() - frame.getWidth() / 2;
            int y = (int) SystemTray.getSystemTray().getTrayIconSize().getHeight();

            frame.setBounds(x, y, frame.getWidth(), frame.getHeight());
            frame.searchBox.requestFocusInWindow();
            frame.searchBox.requestFocus();
            frame.setVisible(true);
            oneOpen = true;
        }

    }

    private void show() throws AWTException {
        SystemTray.getSystemTray().add(icon);
    }

    private static class MyPopUpMenu extends PopupMenu {

        public MyPopUpMenu() {
            super();
            init();
        }

        private void init() {
            MenuItem goToItem = new MenuItem("Go");
            MenuItem quitItem = new MenuItem("Quit");

            goToItem.addActionListener(ae -> {
                try {
                    URL google = new URL("http://www.google.com");
                    Desktop.getDesktop().browse(google.toURI());
                } catch (Exception ex) {
                    Logger.getLogger(GooglerWidgetMain.class.getName()).log(Level.SEVERE, null, ex);
                }
            });


            quitItem.addActionListener(ae -> System.exit(42));

            this.add(goToItem);
            this.add(quitItem);

        }
    }

    private class GooglerFrame extends JFrame {

        private JTextField searchBox;

        public GooglerFrame() {
            super();
            init();
        }

        private void init() {
            this.setAlwaysOnTop(true);
            this.setLayout(new BorderLayout());
            this.setUndecorated(true);
            this.addWindowListener(new WindowAdapter() {


                public void windowClosed(WindowEvent we) {
                    super.windowClosed(we);
                    oneOpen = false;
                }


                public void windowLostFocus(WindowEvent we) {
                    super.windowLostFocus(we);
                    GooglerFrame.this.dispose();
                    oneOpen = false;
                }


                public void windowDeactivated(WindowEvent we) {
                    super.windowDeactivated(we);
                    GooglerFrame.this.dispose();
                    oneOpen = false;
                }


            });

            JPanel contentPanel = new JPanel(new BorderLayout());
            contentPanel.setBorder(new EmptyBorder(5, 10, 5, 10));
            contentPanel.setBackground(new Color(0, 0, 0, 255));

            iconImage = resizeImage(iconImage, iconImage.getType());
            ImageIcon googleIcon = new ImageIcon(iconImage);
            JLabel labe = new JLabel(googleIcon);
            labe.setBorder(new EmptyBorder(5, 5, 5, 5));

            searchBox = new JTextField(20);
            searchBox.addActionListener(ae -> searchBoxListener());

            JLabel infoLabel = new JLabel("Search");
            infoLabel.setHorizontalTextPosition(SwingConstants.CENTER);
            infoLabel.setVerticalTextPosition(SwingConstants.CENTER);

            JButton closeButton = new JButton("Close");
            closeButton.addActionListener(ae -> GooglerFrame.this.dispose());

            JPanel temp = new JPanel(new BorderLayout());
            temp.setBackground(contentPanel.getBackground());

            temp.add(closeButton, BorderLayout.EAST);
            temp.add(searchBox, BorderLayout.NORTH);

            contentPanel.add(labe, BorderLayout.EAST);
            contentPanel.add(temp, BorderLayout.SOUTH);
            contentPanel.add(infoLabel, BorderLayout.WEST);

            this.add(contentPanel, BorderLayout.CENTER);

            this.pack();

        }

        private void searchBoxListener() {
            try {
                URL dest = createURL(searchBox.getText().trim());
                if (dest != null) {
                    Desktop.getDesktop().browse(dest.toURI());
                    this.dispose();
                }
            } catch (Exception ex) {
                JOptionPane.showConfirmDialog(GooglerFrame.this, ex);
            }
        }

        private BufferedImage resizeImage(BufferedImage originalImage, int type) {
            final int IMG_WIDTH = 30;
            final int IMG_HEIGHT = 30;
            BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, type);
            Graphics2D g = resizedImage.createGraphics();
            g.drawImage(originalImage, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
            g.dispose();
            return resizedImage;
        }

        private URL createURL(String searched) throws MalformedURLException {
            if (searched.equals(""))
                return null;

            final String start = "http://www.google.com/search?as_q=";
            final String end = "";

            searched = searched.replaceAll(" ", "+");

            return new URL(start + searched + end);
        }

    }
}
