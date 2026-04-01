import java.awt.*;
import javax.swing.*;
// TODO: COLOURS, HIGHLIGHTING

public class TypingRaceGUI extends JFrame {

    // UI navigation and core containers
    private CardLayout cardLayout;
    private JPanel mainContainer;
    private JPanel customizePanel;
    private JPanel racePanel;
    private JSpinner seatSpinner;

    // Global Modifiers (Moved here to fix the "cannot find symbol" error)
    private JCheckBox autocorrectCheck;
    private JCheckBox caffeineCheck;
    private JCheckBox nightShiftCheck;

    // Simulation data
    private java.util.List<Typist> activeTypists = new java.util.ArrayList<>();
    private JTextPane passageArea; 
    private JPanel progressContainer;
    private String currentPassage = "The quick brown fox jumps over the lazy dog.";

    public TypingRaceGUI() {
        // Main window configuration
        setTitle("Typing Race Simulator - Ultimate Edition");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize CardLayout for screen switching
        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        // Create individual screens
        JPanel configPanel = createConfigPanel();
        this.racePanel = createRacePanel(); 
        JPanel statsPanel = createStatsPanel();
        customizePanel = new JPanel(new BorderLayout());

        // Register screens to the main container
        mainContainer.add(configPanel, "CONFIG");
        mainContainer.add(customizePanel, "CUSTOMIZE");
        mainContainer.add(racePanel, "RACE");
        mainContainer.add(statsPanel, "STATS");

        add(mainContainer);

        // Show the initial configuration screen on launch
        cardLayout.show(mainContainer, "CONFIG");
    }

    private JPanel createConfigPanel() {
        // Setup for the initial race configuration screen
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel title = new JLabel("Race Configuration", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        panel.add(title, BorderLayout.NORTH);

        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));

        // Passage Selection Section
        JPanel passagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        passagePanel.add(new JLabel("Passage Length/Type: "));
        JComboBox<String> passageBox = new JComboBox<>(new String[]{"Short Passage", "Medium Passage", "Long Passage", "Custom Text..."});
        passagePanel.add(passageBox);
        
        JTextField customTextField = new JTextField(20);
        customTextField.setEnabled(false);
        passagePanel.add(customTextField);
        
        // Enable custom text field only when "Custom Text" is selected
        passageBox.addActionListener(e -> customTextField.setEnabled("Custom Text...".equals(passageBox.getSelectedItem())));
        settingsPanel.add(passagePanel);

        // Participant Count Selection (2-6)
        JPanel seatPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        seatPanel.add(new JLabel("Number of Typists (2-6): "));
        seatSpinner = new JSpinner(new SpinnerNumberModel(2, 2, 6, 1));
        ((JSpinner.DefaultEditor) seatSpinner.getEditor()).getTextField().setEditable(false);
        seatPanel.add(seatSpinner);
        settingsPanel.add(seatPanel);

        // Global Modifier Flags
        JPanel modWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel modPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        modPanel.setBorder(BorderFactory.createTitledBorder("Modifiers"));
        
        // FIXED: Initialized the class-level variables instead of creating anonymous ones
        autocorrectCheck = new JCheckBox("Autocorrect (Slideback halved)");
        caffeineCheck = new JCheckBox("Caffeine Mode (Speed boost, high burnout)");
        nightShiftCheck = new JCheckBox("Night Shift (Lower Accuracy)");
        
        modPanel.add(autocorrectCheck);
        modPanel.add(caffeineCheck);
        modPanel.add(nightShiftCheck);
        
        modWrapper.add(modPanel);
        settingsPanel.add(modWrapper);

        panel.add(settingsPanel, BorderLayout.CENTER);

        // Navigation button to customization screen
        JButton nextBtn = new JButton("Next: Customize Typists >>");
        nextBtn.setPreferredSize(new Dimension(0, 50));
        nextBtn.setFont(new Font("Arial", Font.BOLD, 18));
        nextBtn.addActionListener(e -> {
            // Check if Custom Text is selected
            if ("Custom Text...".equals(passageBox.getSelectedItem())) {
                String userText = customTextField.getText().trim();
                if (userText.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter some custom text!");
                    return;
                }
                currentPassage = userText; // Update the race text
            } else {
                String selected = (String) passageBox.getSelectedItem();
                if ("Short Passage".equals(selected)) currentPassage = "The quick brown fox.";
                else if ("Medium Passage".equals(selected)) currentPassage = "The quick brown fox jumps over the lazy dog.";
                else if ("Long Passage".equals(selected)) currentPassage = "Success is not final, failure is not fatal: it is the courage to continue that counts.";
            }

            int count = (Integer) seatSpinner.getValue();
            prepareCustomizePanel(count);
            cardLayout.show(mainContainer, "CUSTOMIZE");
        });
        panel.add(nextBtn, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createRacePanel() {
        // Layout for the active race visualization screen
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(223, 230, 233));

        JLabel title = new JLabel("Step 2: LIVE RACE", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(title, BorderLayout.NORTH);

        // Finish button to jump to stats
        JButton finishBtn = new JButton("FINISH RACE & SHOW STATS >>");
        finishBtn.setFont(new Font("Arial", Font.BOLD, 20));
        finishBtn.addActionListener(e -> cardLayout.show(mainContainer, "STATS"));
        panel.add(finishBtn, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createStatsPanel() {
        // Analytics and results screen
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(9, 132, 227));

        JLabel title = new JLabel("Step 3: Post-Race Analytics (WPM & Accuracy)", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(title, BorderLayout.NORTH);

        // Button to restart and go back to configuration
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

        // Create individual settings block for each typist
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

        // Button to finalize data and start the race
        JButton startRaceBtn = new JButton("START LIVE RACE! >>");
        startRaceBtn.addActionListener(e -> {
            collectTypistData(count, container);
            setupRaceScreen();
            cardLayout.show(mainContainer, "RACE"); 
            startRaceAnimation();
        });
        customizePanel.add(startRaceBtn, BorderLayout.SOUTH);

        customizePanel.revalidate();
        customizePanel.repaint();
    }

    private void collectTypistData(int count, JPanel container) {
        // Harvests user input from customization panels and creates Typist objects
        activeTypists.clear();
        Component[] typistPanels = container.getComponents();

        for (int i = 0; i < count; i++) {
            JPanel p = (JPanel) typistPanels[i];
            
            String colorStr = (String) ((JComboBox<?>) p.getComponent(1)).getSelectedItem();
            String style = (String) ((JComboBox<?>) p.getComponent(3)).getSelectedItem();
            String kb = (String) ((JComboBox<?>) p.getComponent(5)).getSelectedItem();
            String acc = (String) ((JComboBox<?>) p.getComponent(7)).getSelectedItem();
            String sym = ((JTextField) p.getComponent(9)).getText();
            
            char symbolChar = sym.isEmpty() ? '?' : sym.charAt(0);

            Typist t = new Typist(symbolChar, "Player " + (i+1), 0.7); 
            t.setTypingStyle(style);
            t.setKeyboardType(kb);
            t.setAccessory(acc);
            
            // Assign actual Color object based on selection
            if ("Red".equals(colorStr)) t.setColor(Color.RED);
            else if ("Blue".equals(colorStr)) t.setColor(Color.BLUE);
            else if ("Green".equals(colorStr)) t.setColor(Color.GREEN);
            else t.setColor(Color.ORANGE);

            activeTypists.add(t);
        }
    }

    private void setupRaceScreen() {
        // Clears race panel and draws progress bars based on active typists
        racePanel.removeAll(); 
        racePanel.setLayout(new BorderLayout(10, 10));

        // Display area for the text to be typed
        passageArea = new JTextPane();
        passageArea.setEditable(false);
        passageArea.setFont(new Font("Monospaced", Font.BOLD, 20));
        passageArea.setText(currentPassage);
        passageArea.setBorder(BorderFactory.createTitledBorder("Type the following:"));
        racePanel.add(new JScrollPane(passageArea), BorderLayout.NORTH);

        // Create individual progress bar rows
        progressContainer = new JPanel(new GridLayout(activeTypists.size(), 1, 5, 5));
        for (Typist t : activeTypists) {
            JPanel typistRow = new JPanel(new BorderLayout());
            JLabel nameLabel = new JLabel(t.getName() + " [" + t.getSymbol() + "] ");
            
            JProgressBar bar = new JProgressBar(0, currentPassage.length());
            bar.setValue(0);
            bar.setStringPainted(true);
            bar.setForeground(t.getColor());
            
            typistRow.add(nameLabel, BorderLayout.WEST);
            typistRow.add(bar, BorderLayout.CENTER);
            progressContainer.add(typistRow);
        }
        racePanel.add(progressContainer, BorderLayout.CENTER);

        racePanel.revalidate();
    }
    
    private void startRaceAnimation() {
        // Start a timer that runs every 100ms
        Timer raceTimer = new Timer(100, null);
        int[] turnCount = {0}; 
        
        // Read global settings once at start
        boolean isAutocorrect = autocorrectCheck.isSelected();
        boolean isCaffeine = caffeineCheck.isSelected();
        boolean isNightShift = nightShiftCheck.isSelected();

        raceTimer.addActionListener(e -> {
            boolean raceOver = false;
            Typist winner = null;
            turnCount[0]++; // Increase turn count

            for (int i = 0; i < activeTypists.size(); i++) {
                Typist t = activeTypists.get(i);
                
                // 1. SET BASE VALUES
                double currentAcc = t.getAccuracy(); // Base accuracy (0.7)
                int speed = 1;                       // How many steps per move
                double burnoutChance = 0.02;         // Chance to get tired
                int restTime = 3;                    // How many turns to rest
                int penalty = 2;                     // Steps to go back on error

                // 2. APPLY GLOBAL MODIFIERS
                if (isNightShift) currentAcc -= 0.10; // Tired people miss more
                if (isAutocorrect) penalty = 1;       // Fixes help reduce penalty
                if (isCaffeine) {
                    if (turnCount[0] <= 10) speed += 1; // Fast start
                    else burnoutChance += 0.06;         // Crash later
                }

                // 3. APPLY TYPING STYLE
                switch (t.getTypingStyle()) {
                    case "Touch Typist": currentAcc += 0.15; break;
                    case "Hunt & Peck":  currentAcc -= 0.15; break;
                    case "Phone Thumbs": burnoutChance += 0.03; break;
                    case "Voice-to-Text": 
                        currentAcc += 0.05; 
                        penalty += 2; // Big penalty for voice errors
                        break;
                }

                // 4. APPLY KEYBOARD TYPE
                switch (t.getKeyboardType()) {
                    case "Mechanical":  currentAcc += 0.05; break;
                    case "Touchscreen": currentAcc -= 0.10; break;
                    case "Stenography": 
                        speed += 1;      // Very fast
                        currentAcc -= 0.20; // Very hard to use
                        break;
                }

                // 5. APPLY ACCESSORIES
                switch (t.getAccessory()) {
                    case "Wrist Support": restTime = 1; break; // Fast recovery
                    case "Noise-Cancelling Headphones": currentAcc += 0.10; break;
                    case "Energy Drink":
                        // Good in first half, bad in second half
                        if (t.getProgress() < (currentPassage.length() / 2)) currentAcc += 0.15;
                        else currentAcc -= 0.20;
                        break;
                }

                // Keep accuracy between 5% and 95%
                currentAcc = Math.max(0.05, Math.min(currentAcc, 0.95));

                // 6. MOVE LOGIC
                if (!t.isBurntOut()) {
                    for (int s = 0; s < speed; s++) {
                        if (Math.random() < currentAcc) {
                            t.typeCharacter(); // Success move
                        } else {
                            t.slideBack(penalty); // Error move
                        }
                    }
                    // Check for burnout
                    if (Math.random() < burnoutChance) t.burnOut(restTime);
                } else {
                    t.recoverFromBurnout(); // Is resting
                }

                // 7. UPDATE SCREEN
                JPanel row = (JPanel) progressContainer.getComponent(i);
                JProgressBar bar = (JProgressBar) row.getComponent(1);
                bar.setValue(t.getProgress());
                
                // Check for winner
                if (t.getProgress() >= currentPassage.length()) {
                    raceOver = true;
                    if (winner == null) winner = t;
                }
            }

            updateTextHighlighting(); // Blue color for text

            // If race ends, stop timer and go to stats
            if (raceOver) {
                ((Timer)e.getSource()).stop();
                JOptionPane.showMessageDialog(this, "Race Finished! Winner: " + winner.getName());
                cardLayout.show(mainContainer, "STATS");
            }
        });

        raceTimer.start();
    }

    // Helper method to highlight completed characters in the text pane
    private void updateTextHighlighting() {
        // Find max progress among all typists
        int maxProgress = 0;
        for (Typist t : activeTypists) {
            maxProgress = Math.max(maxProgress, t.getProgress());
        }
        
        // Ensure don't exceed text length
        maxProgress = Math.min(maxProgress, currentPassage.length());

        try {
            passageArea.setSelectionStart(0);
            passageArea.setSelectionEnd(maxProgress);
            passageArea.setSelectionColor(new Color(173, 216, 230, 100));
        } catch (Exception ex) {
            // Ignore highlighting errors
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new TypingRaceGUI().setVisible(true);
        });
    }
}