import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ChatClient extends JFrame implements ActionListener, Runnable {

    // RETRO COLORS
    Color DARK_GREEN = new Color(20, 65, 55);
    Color GREEN = new Color(42, 120, 90);
    Color CREAM = new Color(239, 235, 214);
    Color CHAT_BG = new Color(245, 242, 220);
    Color LIGHT_GREEN = new Color(214, 232, 213);
    Color DARK_GRAY = new Color(55, 55, 55);
    Color BORDER = new Color(120, 120, 100);

    BufferedWriter writer;
    BufferedReader reader;
    Socket socket;
    String userName;
    JPanel chatPanel;
    JTextField messageField;
    JButton sendButton;
    JLabel usernameLabel;
    JLabel statusLabel;
    int onlineCount;

    // CONSTRUCTOR
    ChatClient(String name) {
        this.userName = name;
        createUI();
        try {
            socket = new Socket("localhost", 2005);
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            writer.write(userName);
            writer.write("\r\n");
            writer.flush();
            // Start receiving thread
            Thread t1 = new Thread(this);
            t1.start();
            setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Unable to connect to server.", "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // SEND MESSAGE
    @Override
    public void actionPerformed(ActionEvent e) {
        String message = messageField.getText().trim();

        // Don't send empty message
        if (message.isEmpty()) {
            return;
        }

        String out = "<html><p>" + userName + "<p><p>" + message + "<p><html>";

        // Display my message on the RIGHT
        addMessage(out, true);

        // Send to server
        try {
            writer.write(out);
            writer.write("\r\n");
            writer.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // Clear input field
        messageField.setText("");
        // Put cursor back into input field
        messageField.requestFocus();
    }

    // RECEIVE MESSAGE
    @Override
    public void run() {
        try {
            String msg = "";
            while (true) {
                msg = reader.readLine();
                // Server disconnected
                if (msg == null) {
                    break;
                }
                if (msg.contains("ONLINECOUNT-->")){
                    onlineCount = Integer.parseInt(msg.substring(14));
                    statusLabel.setText("● ONLINE - " + onlineCount +"  ");
                    continue;
                }
                addMessage(msg, false);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // CREATE USER INTERFACE
    private void createUI() {
        setUndecorated(true);
        setTitle("Chat Alpha");
        setSize(850, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // TOP TITLE BAR
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(DARK_GREEN);
        titleBar.setPreferredSize(new Dimension(0, 45));

        JLabel title = new JLabel("  CHAT ALPHA");
        title.setForeground(CREAM);
        title.setFont(new Font("Monospaced", Font.BOLD, 18));

        JPanel windowButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        windowButtons.setOpaque(false);

        JButton minimize = new JButton("-");
        JButton maximize = new JButton("F");
        JButton close = new JButton("X");

        minimize.setPreferredSize(new Dimension(45, 45));
        maximize.setPreferredSize(new Dimension(45, 45));
        close.setPreferredSize(new Dimension(45, 45));

        minimize.setForeground(CREAM);
        maximize.setForeground(CREAM);
        close.setForeground(CREAM);

        minimize.setBackground(DARK_GREEN);
        maximize.setBackground(DARK_GREEN);
        close.setBackground(DARK_GREEN);

        minimize.setBorderPainted(false);
        maximize.setBorderPainted(false);
        close.setBorderPainted(false);

        minimize.setFocusPainted(false);
        maximize.setFocusPainted(false);
        close.setFocusPainted(false);

        minimize.setFont(new Font("Monospaced", Font.BOLD, 16));
        maximize.setFont(new Font("Monospaced", Font.BOLD, 16));
        close.setFont(new Font("Monospaced", Font.BOLD, 16));

        titleBar.add(windowButtons, BorderLayout.EAST);

        minimize.addActionListener(e -> setState(JFrame.ICONIFIED));

        maximize.addActionListener(e -> {
            if (getExtendedState() == JFrame.MAXIMIZED_BOTH) {
                setExtendedState(JFrame.NORMAL);
            } else {
                setExtendedState(JFrame.MAXIMIZED_BOTH);
            }
        });

        close.addActionListener(e -> System.exit(0));

        windowButtons.add(minimize);
        windowButtons.add(maximize);
        windowButtons.add(close);

        titleBar.add(title, BorderLayout.WEST);
        add(titleBar, BorderLayout.NORTH);

        // USER INFO AT BOTTOM
        JPanel userPanel = new JPanel(new BorderLayout());
        userPanel.setBackground(new Color(225, 222, 200));
        userPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel loggedIn = new JLabel("USER: " + userName);
        loggedIn.setFont(new Font("Monospaced", Font.BOLD, 12));
        loggedIn.setForeground(DARK_GREEN);
        userPanel.add(loggedIn, BorderLayout.CENTER);

        // RIGHT SIDE
        JPanel rightSide = new JPanel(new BorderLayout());
        rightSide.setBackground(CHAT_BG);

        // CHAT HEADER
        JPanel chatHeader = new JPanel(new BorderLayout());
        chatHeader.setBackground(new Color(225, 222, 200));
        chatHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER));

        // LEFT SIDE OF HEADER
        JPanel headerLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        headerLeft.setOpaque(false);

        JLabel avatar = new JLabel("●");
        avatar.setFont(new Font("Monospaced", Font.BOLD, 24));
        avatar.setForeground(GREEN);

        usernameLabel = new JLabel("GROUP CHAT");
        usernameLabel.setFont(new Font("Monospaced", Font.BOLD, 15));
        usernameLabel.setForeground(DARK_GREEN);

        headerLeft.add(avatar);
        headerLeft.add(usernameLabel);

        // RIGHT SIDE OF HEADER
        statusLabel = new JLabel("● ONLINE - 1");
        statusLabel.setFont(new Font("Monospaced", Font.BOLD, 12));
        statusLabel.setForeground(GREEN);

        chatHeader.add(headerLeft, BorderLayout.WEST);
        chatHeader.add(statusLabel, BorderLayout.EAST);

        rightSide.add(chatHeader, BorderLayout.NORTH);

        // CHAT AREA
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(CHAT_BG);
        chatPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JScrollPane scrollPane = new JScrollPane(chatPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        rightSide.add(scrollPane, BorderLayout.CENTER);

        // MESSAGE INPUT AREA
        JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
        inputPanel.setBackground(new Color(225, 222, 200));
        inputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        messageField = new JTextField();
        messageField.setFont(new Font("Monospaced", Font.PLAIN, 14));
        messageField.setBackground(Color.WHITE);
        messageField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 2),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));

        // Press ENTER to send
        messageField.addActionListener(this);

        sendButton = new JButton("SEND");
        sendButton.setFont(new Font("Monospaced", Font.BOLD, 14));
        sendButton.setForeground(CREAM);
        sendButton.setBackground(DARK_GREEN);
        sendButton.setFocusPainted(false);
        sendButton.setBorder(BorderFactory.createRaisedBevelBorder());
        sendButton.addActionListener(this);

        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        rightSide.add(inputPanel, BorderLayout.SOUTH);

        // ADD RIGHT SIDE
        add(rightSide, BorderLayout.CENTER);
    }

    // CREATE CONTACT
    private JPanel createContact(String name, String status) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CREAM);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                new EmptyBorder(10, 12, 10, 12)));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Monospaced", Font.BOLD, 14));
        nameLabel.setForeground(DARK_GREEN);

        JLabel statusLabel = new JLabel("    " + status);
        statusLabel.setFont(new Font("Monospaced", Font.PLAIN, 11));
        statusLabel.setForeground(DARK_GRAY);

        panel.add(nameLabel, BorderLayout.NORTH);
        panel.add(statusLabel, BorderLayout.SOUTH);

        return panel;
    }

    // DISPLAY MESSAGE
    private void addMessage(String message, boolean mine) {
        SwingUtilities.invokeLater(() -> {
        JPanel messageContainer = new JPanel(new BorderLayout());
        messageContainer.setBackground(CHAT_BG);

        JPanel messagePanel = formatLabel(message, mine);

        if (mine) {
            messageContainer.add(messagePanel, BorderLayout.EAST);
        } else {
            messageContainer.add(messagePanel, BorderLayout.WEST);
        }

        chatPanel.add(messageContainer);
        chatPanel.add(Box.createVerticalStrut(10));

        chatPanel.revalidate();
        chatPanel.repaint();

            JScrollPane scrollPane =
                    (JScrollPane) chatPanel
                            .getParent()
                            .getParent();

            JScrollBar vertical =
                    scrollPane.getVerticalScrollBar();

            vertical.setValue(
                    vertical.getMaximum()
            );
        });
    }

    // MESSAGE BUBBLE
    private JPanel formatLabel(String out, boolean mine) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(mine ? LIGHT_GREEN : Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(8, 12, 8, 12)));

        // Message
        JLabel output = new JLabel("<html>" + out + "</html>");
        output.setFont(new Font("Monospaced", Font.PLAIN, 13));
        output.setForeground(DARK_GRAY);
        output.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(output);

        // TIME
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        JLabel time = new JLabel(sdf.format(cal.getTime()));
        time.setFont(new Font("Monospaced", Font.PLAIN, 10));
        time.setForeground(DARK_GRAY);
        time.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(Box.createVerticalStrut(3));
        panel.add(time);

        Dimension preferred = panel.getPreferredSize();
        panel.setMaximumSize(preferred);

        return panel;
    }

    // USERNAME SCREEN
    public static void welcomeScreen() {
        JFrame usernameFrame = new JFrame("CHAT ALPHA");
        usernameFrame.setSize(400, 300);
        usernameFrame.setLocationRelativeTo(null);
        usernameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        usernameFrame.setLayout(new BorderLayout());

        // TITLE
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(new Color(20, 65, 55));

        JLabel title = new JLabel("CHAT ALPHA");
        title.setForeground(new Color(239, 235, 214));
        title.setFont(new Font("Monospaced", Font.BOLD, 22));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        top.setPreferredSize(new Dimension(0, 60));
        top.add(title, BorderLayout.CENTER);

        usernameFrame.add(top, BorderLayout.NORTH);

        // CENTER
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(new Color(239, 235, 214));
        center.setBorder(new EmptyBorder(25, 45, 25, 45));

        JLabel label = new JLabel("ENTER USERNAME");
        label.setFont(new Font("Monospaced", Font.BOLD, 14));
        label.setForeground(new Color(20, 65, 55));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField usernameField = new JTextField();
        usernameField.setFont(new Font("Monospaced", Font.PLAIN, 14));
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(120, 120, 100), 2),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));

        JButton connectButton = new JButton("CONNECT");
        connectButton.setFont(new Font("Monospaced", Font.BOLD, 13));
        connectButton.setBackground(new Color(20, 65, 55));
        connectButton.setForeground(new Color(239, 235, 214));
        connectButton.setFocusPainted(false);
        connectButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        center.add(label);
        center.add(Box.createVerticalStrut(10));
        center.add(usernameField);
        center.add(Box.createVerticalStrut(20));
        center.add(connectButton);

        usernameFrame.add(center, BorderLayout.CENTER);
        usernameFrame.setVisible(true);

        // CONNECT BUTTON
        connectButton.addActionListener(e -> {
            String name = usernameField.getText().trim();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(usernameFrame, "Please enter a username");
                return;
            }

            usernameFrame.dispose();
            new TestUser(name);
        });

        // Press ENTER to connect
        usernameField.addActionListener(e -> connectButton.doClick());
    }

    // MAIN
    public static void main(String[] args) {
        welcomeScreen();
    }
}