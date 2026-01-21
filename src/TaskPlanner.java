import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

class Task implements Serializable {
    String name;
    Date dueDate;

    public Task(String name, Date dueDate) {
        this.name = name;
        this.dueDate = dueDate;
    }
}

public class TaskPlanner extends JFrame {
    private ArrayList<Task> tasks;
    private JTextArea taskListArea;
    private JTextField taskNameField;
    private JTextField taskDateField;

    public TaskPlanner() {
        tasks = new ArrayList<>();
        loadTasks();

        setTitle("Планер задач");
        setSize(500, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        taskListArea = new JTextArea();
        taskListArea.setEditable(false);
        loadTasksIntoTextArea();

        JPanel inputPanel = new JPanel();
        taskNameField = new JTextField(10);
        taskDateField = new JTextField(10);
        JButton addButton = new JButton("Добавить задачу");
        JButton deleteButton = new JButton("Удалить задачу");

        addButton.addActionListener(e -> addTask());
        deleteButton.addActionListener(e -> deleteTask());

        inputPanel.add(new JLabel("Название задачи:"));
        inputPanel.add(taskNameField);
        inputPanel.add(new JLabel("Срок выполнения:"));
        inputPanel.add(taskDateField);
        inputPanel.add(addButton);
        inputPanel.add(deleteButton);

        add(new JScrollPane(taskListArea), BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        setVisible(true);

        startReminder();
    }

    private void startReminder() {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Date now = new Date();
                for (Task task : tasks) {
                    if (task.dueDate != null && now.after(task.dueDate)) {
                        JOptionPane.showMessageDialog(null, "Напоминание: " + task.name, "Время задачи!", JOptionPane.INFORMATION_MESSAGE);
                        task.dueDate = null; // Отменяем уведомление после его вывода
                    }
                }
            }
        }, 0, 60000); // Проверять каждую минуту
    }

    private void addTask() {
        String taskName = taskNameField.getText();
        long dueDateMillis = Long.parseLong(taskDateField.getText());
        Date dueDate = new Date(System.currentTimeMillis() + dueDateMillis);

        Task newTask = new Task(taskName, dueDate);
        tasks.add(newTask);
        taskNameField.setText("");
        taskDateField.setText("");
        saveTasks();
        loadTasksIntoTextArea();
    }

    private void deleteTask() {
        String taskName = taskNameField.getText();
        tasks.removeIf(task -> task.name.equals(taskName));
        taskNameField.setText("");
        saveTasks();
        loadTasksIntoTextArea();
    }

    private void loadTasksIntoTextArea() {
        StringBuilder sb = new StringBuilder();
        for (Task task : tasks) {
            sb.append(task.name).append(" - ").append(task.dueDate).append("\n");
        }
        taskListArea.setText(sb.toString());
    }

    private void saveTasks() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("tasks.dat"))) {
            oos.writeObject(tasks);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadTasks() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("tasks.dat"))) {
            tasks = (ArrayList<Task>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            tasks = new ArrayList<>();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TaskPlanner::new);
    }
}