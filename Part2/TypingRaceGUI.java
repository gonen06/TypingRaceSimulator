import java.awt.*;
import javax.swing.*;

public class TypingRaceGUI extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainContainer;
    private JPanel customizePanel;
    private JSpinner seatSpinner;

    public TypingRaceGUI() {
        setTitle("Typing Race Simulator - Ultimate Edition");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

   
        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);


        JPanel configPanel = createConfigPanel();
        JPanel racePanel = createRacePanel();
        JPanel statsPanel = createStatsPanel();
        customizePanel = new JPanel(new BorderLayout());

   
        mainContainer.add(configPanel, "CONFIG");
        mainContainer.add(customizePanel, "CUSTOMIZE");
        mainContainer.add(racePanel, "RACE");
        mainContainer.add(statsPanel, "STATS");

        add(mainContainer);

      
        cardLayout.show(mainContainer, "CONFIG");
    }


private JPanel createConfigPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel title = new JLabel("Race Configuration", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        panel.add(title, BorderLayout.NORTH);

        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));

        // Passage Section
        JPanel passagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        passagePanel.add(new JLabel("Passage Length/Type: "));
        JComboBox<String> passageBox = new JComboBox<>(new String[]{"Short Passage", "Medium Passage", "Long Passage", "Custom Text..."});
        passagePanel.add(passageBox);
        JTextField customTextField = new JTextField(20);
        customTextField.setEnabled(false);
        passagePanel.add(customTextField);
        passageBox.addActionListener(e -> customTextField.setEnabled("Custom Text...".equals(passageBox.getSelectedItem())));
        settingsPanel.add(passagePanel);

        // Seat Count 
        JPanel seatPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        seatPanel.add(new JLabel("Number of Typists (2-6): "));
        seatSpinner = new JSpinner(new SpinnerNumberModel(2, 2, 6, 1));
        ((JSpinner.DefaultEditor) seatSpinner.getEditor()).getTextField().setEditable(false);
        seatPanel.add(seatSpinner);
        settingsPanel.add(seatPanel);

        // Modifiers
        JPanel modWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel modPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        modPanel.setBorder(BorderFactory.createTitledBorder("Modifiers"));
        modPanel.add(new JCheckBox("Autocorrect (Slideback halved)"));
        modPanel.add(new JCheckBox("Caffeine Mode (Speed boost, high burnout)"));
        modPanel.add(new JCheckBox("Night Shift (Lower Accuracy)"));
        modWrapper.add(modPanel);
        settingsPanel.add(modWrapper);

        panel.add(settingsPanel, BorderLayout.CENTER);

        JButton nextBtn = new JButton("Next: Customize Typists >>");
        nextBtn.setPreferredSize(new Dimension(0, 50));
        nextBtn.setFont(new Font("Arial", Font.BOLD, 18));
        nextBtn.addActionListener(e -> {
            int count = (Integer) seatSpinner.getValue();
            prepareCustomizePanel(count);
            cardLayout.show(mainContainer, "CUSTOMIZE");
        });
        panel.add(nextBtn, BorderLayout.SOUTH);

        return panel;
    }


    private JPanel createRacePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(223, 230, 233));

        JLabel title = new JLabel("Step 2: LIVE RACE", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(title, BorderLayout.NORTH);


        JButton finishBtn = new JButton("FINISH RACE & SHOW STATS >>");
        finishBtn.setFont(new Font("Arial", Font.BOLD, 20));
        finishBtn.addActionListener(e -> cardLayout.show(mainContainer, "STATS"));
        panel.add(finishBtn, BorderLayout.SOUTH);

        return panel;
    }


    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(9, 132, 227));

        JLabel title = new JLabel("Step 3: Post-Race Analytics (WPM & Accuracy)", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(title, BorderLayout.NORTH);

    
        JButton restartBtn = new JButton("<< SETUP NEW RACE");
        restartBtn.setFont(new Font("Arial", Font.BOLD, 20));
        restartBtn.addActionListener(e -> cardLayout.show(mainContainer, "CONFIG"));
        panel.add(restartBtn, BorderLayout.SOUTH);

        return panel;
    }

    private void prepareCustomizePanel(int count) {
        customizePanel.removeAll();
        
        JPanel container = new JPanel(new GridLayout(1, count, 10, 10));
        container.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (int i = 1; i <= count; i++) {
            JPanel p = new JPanel();
            p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
            p.setBorder(BorderFactory.createTitledBorder("Typist #" + i));

            p.add(new JLabel("Color:"));
            JComboBox<String> colorBox = new JComboBox<>(new String[]{"Red", "Blue", "Green", "Yellow", "Magenta"});
            p.add(colorBox);

            p.add(new JLabel("Typing Style:"));
            JComboBox<String> styleBox = new JComboBox<>(new String[]{"Touch Typist", "Hunt & Peck", "Phone Thumbs", "Voice-to-Text"});
            p.add(styleBox);

            p.add(new JLabel("Keyboard:"));
            JComboBox<String> kbBox = new JComboBox<>(new String[]{"Mechanical", "Membrane", "Touchscreen", "Stenography"});
            p.add(kbBox);

            p.add(new JLabel("Accessories:"));
            JComboBox<String> accBox = new JComboBox<>(new String[]{"None", "Wrist Support", "Energy Drink", "Noise-Cancelling Headphones"});
            p.add(accBox);

            p.add(new JLabel("Symbol (Emoji/Char):"));
            JTextField symField = new JTextField("①");
            symField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
            p.add(symField);

            container.add(p);
        }

        customizePanel.add(new JLabel("Step 2: Personalize Your Typists", SwingConstants.CENTER), BorderLayout.NORTH);
        customizePanel.add(new JScrollPane(container), BorderLayout.CENTER);

        JButton startRaceBtn = new JButton("START LIVE RACE! >>");
        startRaceBtn.addActionListener(e -> cardLayout.show(mainContainer, "RACE"));
        customizePanel.add(startRaceBtn, BorderLayout.SOUTH);

        customizePanel.revalidate();
        customizePanel.repaint();
    }

    public static void main(String[] args) {
      
        SwingUtilities.invokeLater(() -> {
            new TypingRaceGUI().setVisible(true);
        });
    }
}