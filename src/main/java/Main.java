import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import me.gosimple.nbvcxz.Nbvcxz;
import me.gosimple.nbvcxz.resources.Generator;
import me.gosimple.nbvcxz.scoring.Result;

public class Main {
  public static void main(String[] args) {
    JLabel label1 = new JLabel("Enter password:");
    JTextField textField1 = new JTextField(20);
    JLabel label2 = new JLabel("Password strength:");
    JTextField textField2 = new JTextField(20);
    JLabel label3 = new JLabel("Generated password:");
    JButton button3 = new JButton("Generate password");
    JTextArea textArea3 = new JTextArea();
    fillArea(textArea3);
    textArea3.setFont(new Font("Monospaced", Font.PLAIN, 12));
    JPanel panel1 = new JPanel(new GridLayout(3, 2));
    panel1.add(label1);
    panel1.add(textField1);
    panel1.add(label2);
    panel1.add(textField2);
    panel1.add(label3);
    panel1.add(button3);
    JFrame frame = new JFrame("Password strength and generator");
    frame.add(panel1, BorderLayout.NORTH);
    frame.add(new JScrollPane(textArea3), BorderLayout.CENTER);
    frame.pack();
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.setVisible(true);

    textField1
        .getDocument()
        .addDocumentListener(
            new DocumentListener() {
              @Override
              public void insertUpdate(DocumentEvent e) {
                update();
              }

              @Override
              public void removeUpdate(DocumentEvent e) {
                update();
              }

              @Override
              public void changedUpdate(DocumentEvent e) {
                update();
              }

              private void update() {
                textField2.setText(resultToString(calculateStrength(textField1.getText())));
              }
            });
    button3.addActionListener(e -> fillArea(textArea3));
  }

  private static void fillArea(JTextArea area) {
    TreeMap<Double, TreeSet<String>> passwords = new TreeMap<>();
    for (int i = 0; i < 25; i++) {
      String p = generatePassword();
      Result r = calculateStrength(p);
      passwords
          .computeIfAbsent(
              r.getEntropy(),
              k ->
                  new TreeSet<>(
                      Comparator.comparingInt(String::length).thenComparing(String::compareTo)))
          .add(p);
    }
    StringBuilder sb = new StringBuilder("Generated Passwords:\n");
    int i = 1;
    for (TreeSet<String> list : passwords.values()) {
      for (String s : list) {
        sb.append(String.format("%02d. ", i++))
            .append(s)
            .append(" - ")
            .append(resultToString(calculateStrength(s)))
            .append("\n");
      }
    }
    area.setText(sb.toString());
    area.setCaretPosition(0);
  }

  private static Result calculateStrength(String password) {
    // With all defaults...
    Nbvcxz nbvcxz = new Nbvcxz();
    return nbvcxz.estimate(password);
  }

  private static String resultToString(Result result) {
    return String.format("%d/%f", result.getBasicScore(), result.getEntropy());
  }

  private static final Random RANDOM = new Random();

  private static String generatePassword() {
    int len = RANDOM.nextInt(5) + 12;
    return Generator.generateRandomPassword(Generator.CharacterTypes.ALPHANUMERIC, len);
  }
}
