import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

class Student {
    String name;
    Map<String, Integer> grades;
    Map<String, Integer> credits;

    Student(String name, Map<String, Integer> grades, Map<String, Integer> credits) {
        this.name = name;
        this.grades = grades;
        this.credits = credits;
    }

    public double getFinalGrade() {
        return grades.values().stream().mapToInt(Integer::intValue).average().orElse(0);
    }

    public int getTotalCreditsAttempted() {
        return credits.values().stream().mapToInt(Integer::intValue).sum();
    }

    public int getTotalCreditsEarned() {
        int total = 0;
        for (String subject : grades.keySet()) {
            if (grades.get(subject) >= 40) {
                total += credits.get(subject);
            }
        }
        return total;
    }

    public String getDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append("Name: ").append(name).append("\n\n");
        sb.append("Subject Grades and Credits:\n");
        for (String subject : grades.keySet()) {
            sb.append(String.format("%-25s Grade: %3d   Credit: %2d\n", subject, grades.get(subject), credits.get(subject)));
        }
        sb.append("\nFinal Grade (Average): ").append(String.format("%.2f", getFinalGrade())).append("\n");
        sb.append("Total Credits Attempted: ").append(getTotalCreditsAttempted()).append("\n");
        sb.append("Total Credits Earned: ").append(getTotalCreditsEarned()).append("\n");
        return sb.toString();
    }
}

public class StudentGradeTrackerGUI extends JFrame {
    private JTextField nameField;
    private Map<String, JTextField> gradeFields;
    private Map<String, JTextField> creditFields;
    private DefaultListModel<String> studentListModel;
    private JList<String> studentList;
    private JTextArea detailArea;
    private ArrayList<Student> students;

    public StudentGradeTrackerGUI() {
        students = new ArrayList<>();
        gradeFields = new LinkedHashMap<>();
        creditFields = new LinkedHashMap<>();

        String[] subjects = {
            "Mathematics", "Physics", "Chemistry", "Computer Science",
            "Electrical Engineering", "Mechanical Engineering", "Civil Engineering",
            "Electronics", "Thermodynamics", "Engineering Drawing"
        };

        setTitle("Engineering Student Grade Tracker");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Input Panel with GridBagLayout
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            "Student Info",
            0,
            0,
            new Font("SansSerif", Font.BOLD, 14)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Student Name Label
        JLabel nameLabel = new JLabel("Student Name:");
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        inputPanel.add(nameLabel, gbc);

        // Student Name Field
        nameField = new JTextField();
        nameField.setHorizontalAlignment(JTextField.CENTER);
        nameField.setPreferredSize(new Dimension(200, 25));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        inputPanel.add(nameField, gbc);

        // Subject rows
        int row = 1;
        for (String subject : subjects) {
            gbc.insets = new Insets(5, 10, 5, 10);

            // Subject Label
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.gridwidth = 1;
            inputPanel.add(new JLabel(subject + " Grade:"), gbc);

            // Grade Field
            JTextField gradeField = new JTextField();
            gradeField.setPreferredSize(new Dimension(100, 25));
            gradeFields.put(subject, gradeField);
            gbc.gridx = 1;
            inputPanel.add(gradeField, gbc);

            // Credit Label
            gbc.gridx = 2;
            inputPanel.add(new JLabel("Credit:"), gbc);

            // Credit Field
            JTextField creditField = new JTextField();
            creditField.setPreferredSize(new Dimension(100, 25));
            creditFields.put(subject, creditField);
            gbc.gridx = 3;
            inputPanel.add(creditField, gbc);

            row++;
        }

        // Button Panel
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Student");
        JButton reportButton = new JButton("Show Report");
        buttonPanel.add(addButton);
        buttonPanel.add(reportButton);

        // Student List Panel
        studentListModel = new DefaultListModel<>();
        studentList = new JList<>(studentListModel);
        JScrollPane listScrollPane = new JScrollPane(studentList);
        listScrollPane.setBorder(BorderFactory.createTitledBorder("Students"));

        // Detail Area
        detailArea = new JTextArea();
        detailArea.setEditable(false);
        detailArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane detailScrollPane = new JScrollPane(detailArea);
        detailScrollPane.setBorder(BorderFactory.createTitledBorder("Student Details"));

        // Split Pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScrollPane, detailScrollPane);
        splitPane.setDividerLocation(250);

        // Add components
        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(splitPane, BorderLayout.SOUTH);

        // Actions
        addButton.addActionListener(e -> addStudent());
        reportButton.addActionListener(e -> showReport());
        studentList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showStudentDetails(studentList.getSelectedValue());
            }
        });
    }

    private void addStudent() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a student name!");
            return;
        }

        Map<String, Integer> grades = new HashMap<>();
        Map<String, Integer> credits = new HashMap<>();

        try {
            for (String subject : gradeFields.keySet()) {
                int grade = Integer.parseInt(gradeFields.get(subject).getText().trim());
                int credit = Integer.parseInt(creditFields.get(subject).getText().trim());
                grades.put(subject, grade);
                credits.put(subject, credit);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for grades and credits!");
            return;
        }

        Student student = new Student(name, grades, credits);
        students.add(student);
        studentListModel.addElement(name);

        nameField.setText("");
        gradeFields.values().forEach(field -> field.setText(""));
        creditFields.values().forEach(field -> field.setText(""));
    }

    private void showReport() {
        if (students.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No student data available!");
        } else {
            detailArea.setText("Select a student from the list to view details.");
        }
    }

    private void showStudentDetails(String name) {
        for (Student s : students) {
            if (s.name.equals(name)) {
                detailArea.setText(s.getDetails());
                break;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentGradeTrackerGUI().setVisible(true));
    }
}