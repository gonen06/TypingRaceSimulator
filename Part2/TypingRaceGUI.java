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
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(45, 52, 54)); // Şık koyu bir tema

        JLabel title = new JLabel("Step 1: Configure Race & Typists", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(title, BorderLayout.NORTH);

   
        JButton startBtn = new JButton("START RACE >>");
        startBtn.setFont(new Font("Arial", Font.BOLD, 20));
        startBtn.addActionListener(e -> cardLayout.show(mainContainer, "RACE")); // Yarış ekranına geç
        panel.add(startBtn, BorderLayout.SOUTH);

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
        finishBtn.addActionListener(e -> cardLayout.show(mainContainer, "STATS")); // İstatistik ekranına geç
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
        restartBtn.addActionListener(e -> cardLayout.show(mainContainer, "CONFIG")); // Ayarlara geri dön
        panel.add(restartBtn, BorderLayout.SOUTH);

        return panel;
    }

    public static void main(String[] args) {
      
        SwingUtilities.invokeLater(() -> {
            new TypingRaceGUI().setVisible(true);
        });
    }
}