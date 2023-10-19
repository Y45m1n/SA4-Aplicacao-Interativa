import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class TodoList extends JFrame {
    private JPanel mainPanel;
    private JTextField taskInputField;
    private JButton addButton;
    private JList<String> taskList;
    private DefaultListModel<String> listModel;
    private JButton deleteButton;
    private JButton markDoneButton;
    private JComboBox<String> filterComboBox;
    private JButton clearCompletedButton;
    private JLabel timerLabel;
    private int timerCount;
    private List<Task> tasks;
    private Timer clearTimer;

    public TodoList() {
        super("To-Do List App");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(600, 300);
        this.setLocationRelativeTo(null);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        tasks = new ArrayList<>();
        listModel = new DefaultListModel<>();
        taskList = new JList<>(listModel);

        taskInputField = new JTextField();
        addButton = new JButton("Adicionar");
        addButton.setBackground(Color.green);
        deleteButton = new JButton("Excluir");
        deleteButton.setBackground(Color.RED);
        markDoneButton = new JButton("Concluir");
        markDoneButton.setBackground(Color.GREEN.darker());
        filterComboBox = new JComboBox<>(new String[] { "Todas", "Ativas", "Concluídas" });
        clearCompletedButton = new JButton("Limpar Concluídas");
        clearCompletedButton.setBackground(Color.yellow);
        timerLabel = new JLabel("");
        timerCount = 0;

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(taskInputField, BorderLayout.CENTER);
        inputPanel.add(addButton, BorderLayout.EAST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(deleteButton);
        buttonPanel.add(markDoneButton);
        buttonPanel.add(filterComboBox);
        buttonPanel.add(clearCompletedButton);
        buttonPanel.add(timerLabel);

        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(taskList), BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        this.add(mainPanel);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addTask();

            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                deleteTask();
            }
        });

        // Adiciona um ouvinte de mouse ao botão markDoneButton
        markDoneButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Verifica se o número de cliques é igual a 2 (duplo clique)
                    markTaskDone();

                
            }
        });
        // Adiciona um ouvinte de mouse ao botão markDoneButton
        taskList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Verifica se o número de cliques é igual a 2 (duplo clique)
                if (e.getClickCount() == 2) {
                    // Se o duplo clique for detectado, chama a função markTaskDone()
                    markTaskDone();

                }
            }
        });
        filterComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterTasks();
            }
        });

        // Adiciona um ouvinte de teclado ao campo de entrada de tarefas
        // (taskInputField)
        taskInputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                // Verifica se a tecla pressionada é a tecla "Enter" (código de tecla
                // KeyEvent.VK_ENTER)
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    // Se a tecla "Enter" for pressionada, chama a função addTask()
                    addTask();
                }
            }
        });

        // Adiciona um ouvinte de ação (action listener) ao botão "clearCompletedButton"
        clearCompletedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Quando o botão é clicado, chama a função confirmClearCompleted()
                confirmClearCompleted();
            }
        });

        // Cria um temporizador (Timer) que dispara a cada 1000 milissegundos (1
        // segundo)
        clearTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Decrementa o contador do temporizador (timerCount) em 1
                timerCount--;
                if (timerCount > 0) {
                    // Se o contador do temporizador é maior que 0, atualiza o rótulo (label)
                    // timerLabel
                    timerLabel.setText("Limpar em " + timerCount + " segundos");
                } else {
                    // Se o contador do temporizador atingir 0 ou menos, chama a função
                    // clearCompletedTasks()
                    // Encerra o temporizador (stop) e remove o texto do rótulo (label) timerLabel
                    clearCompletedTasks();
                    clearTimer.stop();
                    timerLabel.setText("");
                }
            }
        });

    }

    // Método para adicionar uma nova tarefa
    private void addTask() {
        // Obtém a descrição da tarefa a partir do campo de entrada, removendo espaços
        // em branco no início e no final
        String taskDescription = taskInputField.getText().trim();

        try {
            // Verifica se a descrição da tarefa está vazia
            if (taskDescription.isEmpty()) {
                // Se estiver vazia, lança uma exceção com uma mensagem de erro
                throw new IllegalArgumentException("O campo não pode ficar vazio.");
            }

            // Cria uma nova instância de tarefa (Task) com a descrição fornecida
            Task newTask = new Task(taskDescription);

            // Adiciona a nova tarefa à lista de tarefas (tasks)
            tasks.add(newTask);

            // Atualiza a lista de tarefas na interface do usuário
            updateTaskList();

            // Limpa o campo de entrada de tarefa
            taskInputField.setText("");
        } catch (IllegalArgumentException e) {
            // Se ocorrer uma exceção (campo vazio), exibe uma caixa de diálogo de erro com
            // a mensagem da exceção
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método para excluir uma tarefa
    private void deleteTask() {
        // Obtém o índice da tarefa selecionada na lista de tarefas (taskList)
        int selectedIndex = taskList.getSelectedIndex();

        // Verifica se nenhuma tarefa está selecionada (índice igual a -1)
        if (selectedIndex == -1) {
            // Se nenhuma tarefa estiver selecionada, exibe uma caixa de diálogo de erro
            JOptionPane.showMessageDialog(this, "Selecione uma tarefa para excluir.", "Erro",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            // Se uma tarefa estiver selecionada, exibe uma caixa de diálogo de confirmação
            int choice = JOptionPane.showConfirmDialog(this, "Deseja realmente apagar?", "Confirmação",
                    JOptionPane.YES_NO_OPTION);

            // Verifica se o usuário escolheu "Sim" na caixa de diálogo de confirmação
            if (choice == JOptionPane.YES_OPTION) {
                // Verifica se o índice da tarefa selecionada está dentro dos limites válidos
                if (selectedIndex >= 0 && selectedIndex < tasks.size()) {
                    // Remove a tarefa da lista de tarefas
                    tasks.remove(selectedIndex);

                    // Atualiza a lista de tarefas na interface do usuário
                    updateTaskList();
                }
            }
        }
    }

    private void markTaskDone() {
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < tasks.size()) {
            Task task = tasks.get(selectedIndex);
            task.setDone(true);
            updateTaskList();
        }
    }

    private void filterTasks() {
        String filter = (String) filterComboBox.getSelectedItem();
        listModel.clear();
        for (Task task : tasks) {
            if (filter.equals("Todas") ||
                    (filter.equals("Ativas") && !task.isDone()) ||
                    (filter.equals("Concluídas") && task.isDone())) {
                listModel.addElement(task.getDescription() + (task.isDone() ? " (Concluída)" : ""));
            }
        }
    }

    // Método para confirmar a limpeza de todas as tarefas concluídas
    private void confirmClearCompleted() {
        // Exibe uma caixa de diálogo de confirmação para verificar se o usuário deseja
        // limpar as tarefas concluídas
        int choice = JOptionPane.showConfirmDialog(this, "Deseja realmente apagar todas as tarefas concluídas?",
                "Confirmação", JOptionPane.YES_NO_OPTION);

        // Se o usuário escolher "Sim" na caixa de diálogo de confirmação
        if (choice == JOptionPane.YES_OPTION) {
            // Verifica se o contador do temporizador (timerCount) é menor ou igual a 0
            if (timerCount <= 0) {
                // Define o tempo desejado em segundos (neste caso, 15 segundos)
                timerCount = 15;

                // Inicia o temporizador (clearTimer) para programar a limpeza das tarefas
                // concluídas
                clearTimer.start();
            }
        }
    }

    // Método para limpar todas as tarefas concluídas
    private void clearCompletedTasks() {
        // Cria uma lista (completedTasks) para armazenar as tarefas concluídas
        List<Task> completedTasks = new ArrayList<>();

        // Itera sobre a lista de tarefas (tasks)
        for (Task task : tasks) {
            // Verifica se a tarefa está marcada como concluída
            if (task.isDone()) {
                // Se estiver concluída, adiciona a tarefa à lista de tarefas concluídas
                // (completedTasks)
                completedTasks.add(task);
            }
        }

        // Remove todas as tarefas concluídas da lista de tarefas (tasks)
        tasks.removeAll(completedTasks);

        // Atualiza a lista de tarefas na interface do usuário para refletir a remoção
        // das tarefas concluídas
        updateTaskList();
    }

    private void updateTaskList() {
        listModel.clear();
        for (Task task : tasks) {
            listModel.addElement(task.getDescription() + (task.isDone() ? " (Concluída)" : ""));
        }
    }

    public void run() {
        this.setVisible(true);
    }

    public static void main(String[] args) {
        TodoList todoList = new TodoList();
        todoList.run();
    }
}
