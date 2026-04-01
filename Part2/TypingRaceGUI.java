import java.awt.*;
import javax.swing.*;

public class TypingRaceGUI extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainContainer;

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

   
        mainContainer.add(configPanel, "CONFIG");
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

        // Passage Selection and Custom Text
        JPanel passagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        passagePanel.add(new JLabel("Passage Length/Type: "));
        JComboBox<String> passageBox = new JComboBox<>(new String[]{"Short Passage", "Medium Passage", "Long Passage", "Custom Text..."});
        passagePanel.add(passageBox);
        
        JTextField customTextField = new JTextField(25);
        customTextField.setEnabled(false); 
        passagePanel.add(customTextField);

        passageBox.addActionListener(e -> {
            if ("Custom Text...".equals(passageBox.getSelectedItem())) {
                customTextField.setEnabled(true);
                customTextField.requestFocus();
            } else {
                customTextField.setEnabled(false);
                customTextField.setText("");
            }
        });
        settingsPanel.add(passagePanel);

        // Seat Count
        JPanel seatPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        seatPanel.add(new JLabel("Number of Typists (2-6): "));
        JSpinner seatSpinner = new JSpinner(new SpinnerNumberModel(2, 2, 6, 1));
        
        ((JSpinner.DefaultEditor) seatSpinner.getEditor()).getTextField().setEditable(false);
        seatPanel.add(seatSpinner);
        settingsPanel.add(seatPanel);

        // Difficulty Modifiers
        JPanel modifiersPanelWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel modifiersPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        modifiersPanel.setBorder(BorderFactory.createTitledBorder("Modifiers"));
        
        JCheckBox autocorrectCheck = new JCheckBox("Autocorrect (Slideback halved)");
        JCheckBox caffeineCheck = new JCheckBox("Caffeine Mode (Speed boost, high burnout)");
        JCheckBox nightShiftCheck = new JCheckBox("Night Shift (Lower Accuracy)");
        
        modifiersPanel.add(autocorrectCheck);
        modifiersPanel.add(caffeineCheck);
        modifiersPanel.add(nightShiftCheck);
        modifiersPanelWrapper.add(modifiersPanel);
        
        settingsPanel.add(modifiersPanelWrapper);

        panel.add(settingsPanel, BorderLayout.CENTER);

        JButton nextBtn = new JButton("Next: Customize Typists >>");
        nextBtn.setFont(new Font("Arial", Font.BOLD, 18));

        // For test
        nextBtn.addActionListener(e -> cardLayout.show(mainContainer, "RACE")); 
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

    public static void main(String[] args) {
      
        SwingUtilities.invokeLater(() -> {
            new TypingRaceGUI().setVisible(true);
        });
    }
}