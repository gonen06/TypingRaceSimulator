import java.awt.*;
import javax.swing.*;

public class TypingRaceGUI extends JFrame {

    // UI navigation and core containers
    private CardLayout cardLayout;
    private JPanel mainContainer;
    private JPanel customizePanel;
    private JPanel racePanel;
    private JSpinner seatSpinner;

    // Global Modifiers
    private JCheckBox autocorrectCheck;
    private JCheckBox caffeineCheck;
    private JCheckBox nightShiftCheck;

    // Simulation data and UI components for the race
    private java.util.List<Typist> activeTypists = new java.util.ArrayList<>();
    private java.util.List<JTextPane> typistTextPanes = new java.util.ArrayList<>();
    private java.util.List<JProgressBar> progressBars = new java.util.ArrayList<>();
    private JPanel progressContainer;
    private String currentPassage = "The quick brown fox jumps over the lazy dog.";

    // Statistics
    private long startTime;
    private JPanel statsContentPanel; 
    private java.util.Map<String, java.util.List<RaceRecord>> raceHistory = new java.util.HashMap<>();

    // Inner class to store past race records
    class RaceRecord {
        int position;
        int wpm;
        double accuracy;
        int burnouts;
        int points; // for Leaderboard
        public RaceRecord(int position, int wpm, double accuracy, int burnouts, int points) {
            this.position = position; this.wpm = wpm; this.accuracy = accuracy; this.burnouts = burnouts; this.points = points;
        }
    }

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
        
        autocorrectCheck = new JCheckBox("Autocorrect (Slideback halved)");
        caffeineCheck = new JCheckBox("Caffeine Mode (Speed boost, high burnout)");
        nightShiftCheck = new JCheckBox("Night Shift (Lower Accuracy)");
        
        // ADDED TOOLTIPS FOR GLOBAL MODIFIERS
        autocorrectCheck.setToolTipText("Reduces mistype penalty from 2 steps back to 1 step.");
        caffeineCheck.setToolTipText("Grants 2x speed for the first 10 turns, but heavily increases burnout risk afterwards.");
        nightShiftCheck.setToolTipText("Reduces accuracy for all typists by 10%.");
        
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
        // Analytics and results screen base
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(9, 132, 227));

        JLabel title = new JLabel("Step 3: Post-Race Analytics", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(title, BorderLayout.NORTH);

        // Dynamic content panel for tabs
        statsContentPanel = new JPanel(new BorderLayout());
        panel.add(statsContentPanel, BorderLayout.CENTER);

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
            // ADDED TOOLTIP 
            styleBox.setToolTipText("Touch: +Acc | Hunt: -Acc | Phone: +Burnout | Voice: +Acc, but huge mistype penalty");
            p.add(styleBox);

            p.add(new JLabel("Keyboard:"));
            JComboBox<String> kbBox = new JComboBox<>(new String[]{"Mechanical", "Membrane", "Touchscreen", "Stenography"});
            kbBox.setToolTipText("Mech: +Acc | Touch: -Acc | Steno: 2x Speed, but huge -Acc");
            p.add(kbBox);

            p.add(new JLabel("Accessories:"));
            JComboBox<String> accBox = new JComboBox<>(new String[]{"None", "Wrist Support", "Energy Drink", "Noise-Cancelling Headphones"});
            accBox.setToolTipText("Wrist: Quick Recovery | Energy: Good 1st half, Bad 2nd half | Headphones: +Acc");
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
            
            // Reset typists before the race starts to clear previous stats
            for(Typist t : activeTypists) {
                t.resetToStart(); 
            }
            
            setupRaceScreen();
            cardLayout.show(mainContainer, "RACE"); 
            
            startTime = System.currentTimeMillis(); // Start timer for WPM
            startRaceAnimation();
        });
        customizePanel.add(startRaceBtn, BorderLayout.SOUTH);

        customizePanel.revalidate();
        customizePanel.repaint();
    }

    private void collectTypistData(int count, JPanel container) {
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
            
            if ("Red".equals(colorStr)) t.setColor(Color.RED);
            else if ("Blue".equals(colorStr)) t.setColor(Color.BLUE);
            else if ("Green".equals(colorStr)) t.setColor(Color.GREEN);
            else if ("Yellow".equals(colorStr)) t.setColor(Color.YELLOW);
            else if ("Magenta".equals(colorStr)) t.setColor(Color.MAGENTA);
            else t.setColor(Color.ORANGE);

            activeTypists.add(t);
        }
    }

    private void setupRaceScreen() {
        racePanel.removeAll(); 
        racePanel.setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Step 2: LIVE RACE", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        racePanel.add(title, BorderLayout.NORTH);

        typistTextPanes.clear();
        progressBars.clear();
        
        progressContainer = new JPanel(new GridLayout(activeTypists.size(), 1, 10, 10));
        
        for (Typist t : activeTypists) {
            // Create a row for each typist
            JPanel typistRow = new JPanel(new BorderLayout(10, 10));
            typistRow.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
            ));
            
            // Left Side: Name and Symbol
            JLabel nameLabel = new JLabel(t.getName() + " [" + t.getSymbol() + "] ");
            nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
            nameLabel.setPreferredSize(new Dimension(150, 30));
            
            // Center: Individual Text Pane (TypeRacer style)
            JTextPane textPane = new JTextPane();
            textPane.setEditable(false);
            textPane.setFont(new Font("Monospaced", Font.BOLD, 16));
            textPane.setText(currentPassage);
            typistTextPanes.add(textPane); // Save to list to update later
            
            // Bottom: Progress Bar
            JProgressBar bar = new JProgressBar(0, currentPassage.length());
            bar.setValue(0);
            bar.setStringPainted(true);
            bar.setForeground(t.getColor());
            
            bar.setUI(new javax.swing.plaf.basic.BasicProgressBarUI());
            progressBars.add(bar); // Save to list to update later
            
            JPanel centerP = new JPanel(new BorderLayout(0, 5));
            centerP.add(new JScrollPane(textPane), BorderLayout.CENTER);
            centerP.add(bar, BorderLayout.SOUTH);
            
            typistRow.add(nameLabel, BorderLayout.WEST);
            typistRow.add(centerP, BorderLayout.CENTER);
            
            progressContainer.add(typistRow);
        }
        
        racePanel.add(new JScrollPane(progressContainer), BorderLayout.CENTER);
        racePanel.revalidate();
        racePanel.repaint();
    }
    
    private void startRaceAnimation() {
        Timer raceTimer = new Timer(100, null);
        int[] turnCount = {0}; 
        
        boolean isAutocorrect = autocorrectCheck.isSelected();
        boolean isCaffeine = caffeineCheck.isSelected();
        boolean isNightShift = nightShiftCheck.isSelected();

        raceTimer.addActionListener(e -> {
            boolean raceOver = false;
            Typist winner = null;
            turnCount[0]++; 

            for (int i = 0; i < activeTypists.size(); i++) {
                Typist t = activeTypists.get(i);
                
                // 1. SET BASE VALUES
                double currentAcc = t.getAccuracy(); 
                int speed = 1;                       
                double burnoutChance = 0.02;         
                int restTime = 3;                    
                int penalty = 2;                     

                // 2. APPLY GLOBAL MODIFIERS
                if (isNightShift) currentAcc -= 0.10; 
                if (isAutocorrect) penalty = 1;       
                if (isCaffeine) {
                    if (turnCount[0] <= 10) speed += 1; 
                    else burnoutChance += 0.06;         
                }

                // 3. APPLY TYPING STYLE
                switch (t.getTypingStyle()) {
                    case "Touch Typist": currentAcc += 0.15; break;
                    case "Hunt & Peck":  currentAcc -= 0.15; break;
                    case "Phone Thumbs": burnoutChance += 0.03; break;
                    case "Voice-to-Text": 
                        currentAcc += 0.05; 
                        penalty += 2; 
                        break;
                }

                // 4. APPLY KEYBOARD TYPE
                switch (t.getKeyboardType()) {
                    case "Mechanical":  currentAcc += 0.05; break;
                    case "Touchscreen": currentAcc -= 0.10; break;
                    case "Stenography": 
                        speed += 1;      
                        currentAcc -= 0.20; 
                        break;
                }

                // 5. APPLY ACCESSORIES
                switch (t.getAccessory()) {
                    case "Wrist Support": restTime = 1; break; 
                    case "Noise-Cancelling Headphones": currentAcc += 0.10; break;
                    case "Energy Drink":
                        if (t.getProgress() < (currentPassage.length() / 2)) currentAcc += 0.15;
                        else currentAcc -= 0.20;
                        break;
                }

                currentAcc = Math.max(0.05, Math.min(currentAcc, 0.95));

                // 6. MOVE LOGIC
                if (!t.isBurntOut()) {
                    for (int s = 0; s < speed; s++) {
                        if (Math.random() < currentAcc) {
                            t.typeCharacter(); 
                        } else {
                            t.slideBack(penalty); 
                        }
                    }
                    if (Math.random() < burnoutChance) t.burnOut(restTime);
                } else {
                    t.recoverFromBurnout(); 
                }

                // 7. UPDATE SCREEN (Progress bar + Text Highlighting)
                progressBars.get(i).setValue(t.getProgress());
                updateTypistText(typistTextPanes.get(i), t.getProgress(), t.getColor());
                
                if (t.getProgress() >= currentPassage.length()) {
                    raceOver = true;
                    if (winner == null) winner = t;
                }
            }

            if (raceOver) {
                ((Timer)e.getSource()).stop();
                
                // Calculate time and generate stats view
                long timeTakenMs = System.currentTimeMillis() - startTime;
                generateStatsView(timeTakenMs);
                
                JOptionPane.showMessageDialog(this, "Race Finished! Winner: " + winner.getName());
                cardLayout.show(mainContainer, "STATS");
            }
        });

        raceTimer.start();
    }

    private void updateTypistText(JTextPane pane, int progress, Color color) {
        javax.swing.text.StyledDocument doc = pane.getStyledDocument();
        int len = doc.getLength();
        if (progress > len) progress = len;
        
        Color safeColor = (color == Color.YELLOW) ? new Color(204, 204, 0) : color;
        
        // Style for text that has already been typed (Colored and Bold)
        javax.swing.text.Style completedStyle = pane.addStyle("Completed", null);
        javax.swing.text.StyleConstants.setForeground(completedStyle, safeColor);
        javax.swing.text.StyleConstants.setBold(completedStyle, true);

        // Style for text waiting to be typed (Gray and Normal)
        javax.swing.text.Style pendingStyle = pane.addStyle("Pending", null);
        javax.swing.text.StyleConstants.setForeground(pendingStyle, Color.GRAY);
        javax.swing.text.StyleConstants.setBold(pendingStyle, false);

        doc.setCharacterAttributes(0, progress, completedStyle, true);
        doc.setCharacterAttributes(progress, len - progress, pendingStyle, true);
    }

    // Method to generate tabs for Statistics
    private void generateStatsView(long timeTakenMs) {
        statsContentPanel.removeAll();
        double minutes = timeTakenMs / 60000.0;
        
        JTabbedPane tabbedPane = new JTabbedPane();

        // Sort typists by progress to determine final positions
        java.util.List<Typist> sortedTypists = new java.util.ArrayList<>(activeTypists);
        sortedTypists.sort((a, b) -> Integer.compare(b.getProgress(), a.getProgress()));

        // Current Race Stats
        JPanel currentRacePanel = new JPanel();
        currentRacePanel.setLayout(new BoxLayout(currentRacePanel, BoxLayout.Y_AXIS));
        currentRacePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Data lists for the interactive chart
        java.util.List<String> names = new java.util.ArrayList<>();
        java.util.List<Integer> wpms = new java.util.ArrayList<>();
        java.util.List<Double> accuracies = new java.util.ArrayList<>();
        java.util.List<Integer> burnoutsList = new java.util.ArrayList<>();
        java.util.List<Color> colors = new java.util.ArrayList<>();

       for (Typist t : activeTypists) {
            int position = sortedTypists.indexOf(t) + 1;
            
            // WPM
            int wpm = (int) Math.round((t.getProgress() / 5.0) / minutes);
            
            // Accuracy Percentage
            int totalKeys = t.getTotalKeystrokes();
            int mistakes = t.getMistypeCount();
            double accPercent = totalKeys == 0 ? 0 : ((totalKeys - mistakes) / (double)totalKeys) * 100.0;
            
            // Burnout Count
            int burnouts = t.getTotalBurnoutEvents();
            
            double accChange = 0.0;

            if (raceHistory.containsKey(t.getName()) && !raceHistory.get(t.getName()).isEmpty()) {
                java.util.List<RaceRecord> pastRecords = raceHistory.get(t.getName());
                double lastAcc = pastRecords.get(pastRecords.size() - 1).accuracy;
                accChange = accPercent - lastAcc;
            }

            // Point Algorithm

            int positionPoints = 0;
            if (position == 1) positionPoints = 3;
            else if (position == 2) positionPoints = 2;
            else if (position == 3) positionPoints = 1;
            
            int wpmBonus = wpm / 10;
            int burnoutPenalty = burnouts;
            int pointsEarned = Math.max(0, positionPoints + wpmBonus - burnoutPenalty);

            // Save to History
            raceHistory.putIfAbsent(t.getName(), new java.util.ArrayList<>());
            raceHistory.get(t.getName()).add(new RaceRecord(position, wpm, accPercent, burnouts, pointsEarned));

            // Print on screen
            JPanel row = new JPanel(new GridLayout(1, 6));
            row.setBorder(BorderFactory.createTitledBorder(t.getName() + " [" + t.getSymbol() + "] - Position: " + position));
            row.add(new JLabel("WPM: " + wpm));
            row.add(new JLabel(String.format("Accuracy: %.1f%%", accPercent)));
            row.add(new JLabel(String.format("Acc Change: %+.1f%%", accChange)));
            row.add(new JLabel("Mistypes: " + mistakes));
            row.add(new JLabel("Burnouts: " + burnouts));
            row.add(new JLabel("Points: +" + pointsEarned));
            
            currentRacePanel.add(row);

            // Add to chart lists
            names.add(t.getName());
            wpms.add(wpm);
            accuracies.add(accPercent);
            burnoutsList.add(burnouts);
            colors.add(t.getColor());
        }
        tabbedPane.addTab("Current Race", new JScrollPane(currentRacePanel));

        // History and Bests
        JPanel historyPanel = new JPanel();
        historyPanel.setLayout(new BoxLayout(historyPanel, BoxLayout.Y_AXIS));
        
        for (String name : raceHistory.keySet()) {
            java.util.List<RaceRecord> records = raceHistory.get(name);
            int maxWpm = 0;
            double avgAcc = 0;
            StringBuilder trend = new StringBuilder();
            
            for (int i = 0; i < records.size(); i++) {
                RaceRecord r = records.get(i);
                if (r.wpm > maxWpm) maxWpm = r.wpm;
                avgAcc += r.accuracy;
                
                // WPM Trend
                trend.append(r.wpm);
                if (i < records.size() - 1) trend.append(" -> ");
            }
            avgAcc /= records.size();

            JPanel hRow = new JPanel(new GridLayout(2, 1));
            hRow.setBorder(BorderFactory.createTitledBorder(name + "'s Lifetime Stats"));
            
            JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
            topRow.add(new JLabel("Total Races: " + records.size() + " | "));
            topRow.add(new JLabel("Personal Best WPM: " + maxWpm + " | "));
            topRow.add(new JLabel(String.format("Avg Accuracy: %.1f%%", avgAcc)));
            
            JPanel bottomRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
            bottomRow.add(new JLabel("WPM Trend: " + trend.toString()));
            
            hRow.add(topRow);
            hRow.add(bottomRow);
            historyPanel.add(hRow);
        }
        tabbedPane.addTab("History & Trends", new JScrollPane(historyPanel));

        // Graphic
        JPanel compareWrapper = new JPanel(new BorderLayout());
        
        // Choose metric
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        controlPanel.add(new JLabel("Select Metric to Compare: "));
        JComboBox<String> metricBox = new JComboBox<>(new String[]{"WPM", "Accuracy", "Burnouts"});
        controlPanel.add(metricBox);
        compareWrapper.add(controlPanel, BorderLayout.NORTH);

        JPanel chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (wpms.isEmpty()) return;
                
                String selectedMetric = (String) metricBox.getSelectedItem();
                java.util.List<Double> values = new java.util.ArrayList<>();
                
                // Load data based on selected metric
                for(int i = 0; i < names.size(); i++) {
                    if ("WPM".equals(selectedMetric)) values.add((double)wpms.get(i));
                    else if ("Accuracy".equals(selectedMetric)) values.add(accuracies.get(i));
                    else values.add((double)burnoutsList.get(i));
                }
                
                double maxVal = java.util.Collections.max(values);
                if (maxVal == 0) maxVal = 1; // Prevent divide by zero
                
                int width = getWidth();
                int height = getHeight();
                int barWidth = width / values.size() - 20;

                for (int i = 0; i < values.size(); i++) {
                    int barHeight = (int) ((values.get(i) / maxVal) * (height - 50));
                    g.setColor(colors.get(i));
                    g.fillRect(10 + i * (barWidth + 20), height - barHeight - 20, barWidth, barHeight);
                    
                    g.setColor(Color.BLACK);
                    String labelVal = "Accuracy".equals(selectedMetric) ? String.format("%.1f", values.get(i)) : String.valueOf(values.get(i).intValue());
                    g.drawString(names.get(i) + " (" + labelVal + ")", 10 + i * (barWidth + 20), height - 5);
                }
            }
        };
        chartPanel.setBackground(Color.WHITE);
        metricBox.addActionListener(e -> chartPanel.repaint());
        compareWrapper.add(chartPanel, BorderLayout.CENTER);
        
        tabbedPane.addTab("Comparison Chart", compareWrapper);

        // Leaderboard
        JPanel leaderboardPanel = new JPanel();
        leaderboardPanel.setLayout(new BoxLayout(leaderboardPanel, BoxLayout.Y_AXIS));
        leaderboardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Helper class to sort leaderboard
        class PlayerRank {
            String name; int totalPoints; String badges;
            PlayerRank(String n, int p, String b) { name=n; totalPoints=p; badges=b; }
        }
        java.util.List<PlayerRank> ranking = new java.util.ArrayList<>();

        // Calculate points and badges for each player
        for (String name : raceHistory.keySet()) {
            java.util.List<RaceRecord> records = raceHistory.get(name);
            int totalPts = 0;
            int consecutiveWins = 0;
            int maxConsecutiveWins = 0;
            int consecutiveNoBurnouts = 0;
            int maxConsecutiveNoBurnouts = 0;

            for (RaceRecord r : records) {
                totalPts += r.points; // Sum cumulative points

                // Task 22 Logic: Consecutive Wins
                if (r.position == 1) consecutiveWins++;
                else consecutiveWins = 0;
                maxConsecutiveWins = Math.max(maxConsecutiveWins, consecutiveWins);

                // Task 22 Logic: Consecutive No Burnouts
                if (r.burnouts == 0) consecutiveNoBurnouts++;
                else consecutiveNoBurnouts = 0;
                maxConsecutiveNoBurnouts = Math.max(maxConsecutiveNoBurnouts, consecutiveNoBurnouts);
            }

            // Award Badges
            StringBuilder badges = new StringBuilder();
            if (maxConsecutiveWins >= 3) badges.append("[Speed Demon] ");
            if (maxConsecutiveNoBurnouts >= 5) badges.append("[Iron Fingers] ");

            ranking.add(new PlayerRank(name, totalPts, badges.toString()));
        }

        // Sort descending by total points
        ranking.sort((a, b) -> Integer.compare(b.totalPoints, a.totalPoints));

        // Display Leaderboard
        JLabel lbTitle = new JLabel("Global Leaderboard");
        lbTitle.setFont(new Font("Arial", Font.BOLD, 20));
        leaderboardPanel.add(lbTitle);
        leaderboardPanel.add(Box.createVerticalStrut(10));

        for (int i = 0; i < ranking.size(); i++) {
            PlayerRank pr = ranking.get(i);
            JPanel rRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
            rRow.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
            
            JLabel rLabel = new JLabel((i + 1) + ". " + pr.name + " - " + pr.totalPoints + " pts   " + pr.badges);
            rLabel.setFont(new Font("Arial", Font.BOLD, 16));
            if (!pr.badges.isEmpty()) rLabel.setForeground(new Color(204, 102, 0)); // Highlight badge owners
            
            rRow.add(rLabel);
            leaderboardPanel.add(rRow);
        }

        tabbedPane.addTab("Leaderboard & Badges", new JScrollPane(leaderboardPanel));

        statsContentPanel.add(tabbedPane, BorderLayout.CENTER);
        statsContentPanel.revalidate();
        statsContentPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TypingRaceGUI().setVisible(true);
        });
    }
}