package bashtalkclient.ui;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.regex.*;

import bashtalkclient.core.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.JTextComponent;

public class ChatUI extends JFrame {

    private static final int WIDTH = 89;
    private static final int HEIGHT = 49;

    private Window window;
    public String username = "#username: ";
    private Font font;
    private JPanel contentPane, inputPanel;
    private JTextArea terminal, input;
    private JLabel tag;

    private BashTalkClient client;

    public ChatUI(String usr, BashTalkClient client) {
        username = "<" + usr + "> ";
        this.client = client;

        // Calculates scaling unique to each resolution screen
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int uiscale = (int) Math.round((gd.getDisplayMode().getHeight() / 100.0) + 0.5);
        int scale = (gd.getDisplayMode().getHeight() + gd.getDisplayMode().getWidth()) / 200;

        window = this;

        // Sets up intial text and accessibility features
        setTitle("BashTalk");
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResolution(uiscale, scale);
        setLocationRelativeTo(null);

        // Initializes the login components
        initComponents(uiscale, scale);
        setContentPane(contentPane);

        // Runs the KeyListener to check if Enter is pressed
        try {
            keyPress();
        } catch (Exception e) {
            System.out.println("IO Error");
        }

    }

    /* Sets the resolution of the UI */
    public void setResolution(int uiscale, int scale) {
        setSize(WIDTH * uiscale, HEIGHT * uiscale);
        font = new Font("Consolas", Font.PLAIN, scale);
    }

    /* Initializes all JComponents to displayed to the client */
    private void initComponents(int uiscale, int scale) {
        // Creates the program icon and sets it to the JFrame
        ImageIcon img = new ImageIcon("src/bashTalk.png");
        this.setIconImage(img.getImage());

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

        // Settings for the username tag
        tag = new JLabel(username);
        tag.setFont(font);
        tag.setOpaque(true);
        tag.setBackground(Color.BLACK);
        tag.setForeground(Color.GREEN);
        tag.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        ;

        // Settings for the input text area
        input = new JTextArea(2, 50);
        input.setFont(font);
        input.setBackground(Color.BLACK);
        input.setForeground(Color.WHITE);
        input.setCaretColor(Color.WHITE);
        input.setLineWrap(true);
        input.setWrapStyleWord(true);
        input.setCaret(new MyCaret());

        // Settings for the inputPanel that holds the username tag and the input
        // textArea
        inputPanel = new JPanel(new BorderLayout());
        inputPanel.setOpaque(true);
        inputPanel.setBackground(Color.black);
        inputPanel.add(tag, BorderLayout.BEFORE_LINE_BEGINS);
        inputPanel.add(new JScrollPane(input), BorderLayout.CENTER);

        // Settings for the terminal textArea
        terminal = new JTextArea(29, 131);
        terminal.setFont(font);
        terminal.setBackground(Color.BLUE);
        terminal.setForeground(Color.GREEN);
        terminal.setEditable(false);

        // Settings for the terminalPanel that holds the terminal textArea
        JPanel tpan = new JPanel(new BorderLayout());
        tpan.add(new JScrollPane(terminal), BorderLayout.CENTER);

        // ContentPane holds the terminal panel and the inputPanel
        contentPane.add(tpan, "Center");
        contentPane.add(inputPanel, "South");

    }

    /* Appends the provided message to the terminal screen */
    public void addMessage(String msg) {
        terminal.setEditable(true);
        terminal.append(msg + "\n");
        terminal.setEditable(false);
    }

    /* Clears the terminal screen */
    public void clear() {
        terminal.setEditable(true);
        terminal.setText("");
        terminal.setEditable(false);
    }

    /*
     * Checks if Enter is pressed and runs pre-set commands or broadcasts the
     * message to other clients
     */
    public void keyPress() throws IOException {
        input.addKeyListener(new KeyListener() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.isShiftDown() && e.getKeyCode() == KeyEvent.VK_ENTER)
                    input.append("\n");
                else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    // Forwards the message to the server when enter is pressed
                    if (input.getText().equals("/clear"))
                        clear();
                    else 
                        client.sendMessage(username + input.getText());
                    input.setText(null);
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    input.setText(null);
            }
        });
    }

    /* Creates a custom caret that gives a terminal feel */
    public class MyCaret extends DefaultCaret {

        private String mark = "<";
        private java.awt.Image img = null;

        public MyCaret() {
            setBlinkRate(500);
            try {
                ImageIcon icon = new ImageIcon("src/cursor.png");
                img = icon.getImage();
            } catch (Exception e) {
            }
        }

        @Override
        protected synchronized void damage(Rectangle r) {
            if (r == null) {
                return;
            }

            JTextComponent comp = getComponent();
            FontMetrics fm = comp.getFontMetrics(comp.getFont());
            int textWidth = 35;
            int textHeight = 57;
            x = r.x;
            y = r.y;
            width = textWidth;
            height = textHeight;
            repaint(); // calls getComponent().repaint(x, y, width, height)
        }

        @Override
        public void paint(Graphics g) {
            JTextComponent comp = getComponent();
            if (comp == null) {
                return;
            }

            int dot = getDot();
            Rectangle r = null;
            try {
                r = comp.modelToView(dot);
            } catch (BadLocationException e) {
                return;
            }
            if (r == null) {
                return;
            }

            if ((x != r.x) || (y != r.y)) {
                repaint(); // erase previous location of caret
                damage(r);
            }

            if (isVisible()) {
                FontMetrics fm = comp.getFontMetrics(comp.getFont());
                g.drawImage(img, x, y + 5, tag.getFont().getSize() / 2, tag.getFont().getSize(), Color.white, window);
            }
        }

    }

}