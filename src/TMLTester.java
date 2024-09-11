import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TMLTester {

    private static final String ADB_PATH = "adb"; // Caminho para o comando ADB
    private static Timer actionTimer;
    private static boolean isRunning = false;
    private static final List<Action> ACTIONS = new ArrayList<>();
    private static int actionIndex = 0;
    private static JTextArea logArea;

    // Classe interna para representar uma ação a ser executada
    private static class Action {
        enum ActionType {
            TAP, SWIPE, INPUT_TEXT, ADB_KEY_EVENT, ADB_NEXT, ADB_ENTER
        }

        ActionType type;
        Coordinate coordinate;
        InputText inputText;
        Swipe swipe;
        int interval;
        String description; // Descrição da ação

        // Construtores para diferentes tipos de ação
        Action(Coordinate coordinate, String description) {
            this.type = ActionType.TAP;
            this.coordinate = coordinate;
            this.interval = coordinate.interval;
            this.description = description;
        }

        Action(InputText inputText, String description) {
            this.type = ActionType.INPUT_TEXT;
            this.inputText = inputText;
            this.interval = inputText.interval;
            this.description = description;
        }

        Action(Swipe swipe, String description) {
            this.type = ActionType.SWIPE;
            this.swipe = swipe;
            this.interval = swipe.interval;
            this.description = description;
        }

        Action(int interval, String description, ActionType actionType) {
            this.type = actionType;
            this.interval = interval;
            this.description = description;
        }
    }

    // Classe interna para representar coordenadas para toque na tela
    private static class Coordinate {
        int x;
        int y;
        int interval; // Intervalo de tempo (em milissegundos) antes da ação

        Coordinate(int x, int y, int interval) {
            this.x = x;
            this.y = y;
            this.interval = interval;
        }
    }

    // Classe interna para representar um movimento de swipe na tela
    private static class Swipe {
        int startX;
        int startY;
        int endX;
        int endY;
        int interval;

        Swipe(int startX, int startY, int endX, int endY, int interval) {
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
            this.interval = interval;
        }
    }

    // Classe interna para representar entrada de texto
    private static class InputText {
        String text;
        int interval;

        InputText(String text, int interval) {
            this.text = text;
            this.interval = interval;
        }
    }

    // Método para iniciar a simulação
    public static void startSimulation() {
        if (!isRunning) {
            // Adiciona as ações à lista de ações
            ACTIONS.add(new Action(new Coordinate(379, 375, 4000), "Abrir serviço"));
            ACTIONS.add(new Action(new Coordinate(320, 985, 4000), "Fechar QR Code"));
            ACTIONS.add(new Action(new Coordinate(186, 358, 4000), "Linha"));
            ACTIONS.add(new Action(new Coordinate(186, 358, 4000), "Estação"));
            ACTIONS.add(new Action(new Coordinate(186, 358, 4000), "Dentro comboio"));
            ACTIONS.add(new Action(new Coordinate(186, 358, 4000), "Dentro comboio"));
            ACTIONS.add(new Action(new InputText("1", 5000), "Veiculo"));
            ACTIONS.add(new Action(new Coordinate(186, 275, 4000), "Dentro comboio"));
            ACTIONS.add(new Action(new Coordinate(620, 1100, 4000), "Dentro comboio"));
            ACTIONS.add(new Action(new Coordinate(100, 1100, 5000), "Confirmar Resumo"));
            ACTIONS.add(new Action(new Coordinate(100, 1100, 8000), "Infração"));
            ACTIONS.add(new Action(new Coordinate(300, 600, 4000), "Nível"));
            ACTIONS.add(new Action(new Coordinate(300, 600, 4000), "Artigo"));
            ACTIONS.add(new Action(new Swipe(500, 1000, 500, 0, 5000), "Swipe para baixo"));
            ACTIONS.add(new Action(new Coordinate(643, 673, 5000), "Infrator recusou assinar"));
            ACTIONS.add(new Action(new Coordinate(547, 960, 4000), "Dados"));
            ACTIONS.add(new Action(new Coordinate(269, 267, 4000), "Dados"));
            ACTIONS.add(new Action(new InputText("Hello", 5000), "Inserir nome"));
            ACTIONS.add(new Action(4000, "Próxima caixa de texto", Action.ActionType.ADB_NEXT));
            ACTIONS.add(new Action(2000, "Próxima caixa de texto", Action.ActionType.ADB_NEXT));
            ACTIONS.add(new Action(2000, "Próxima caixa de texto", Action.ActionType.ADB_NEXT));
            ACTIONS.add(new Action(2000, "Enter", Action.ActionType.ADB_ENTER));
            ACTIONS.add(new Action(new Coordinate(127, 247, 5000), "CC"));
            ACTIONS.add(new Action(4000, "Próxima caixa de texto", Action.ActionType.ADB_NEXT));
            ACTIONS.add(new Action(2000, "Próxima caixa de texto", Action.ActionType.ADB_NEXT));
            ACTIONS.add(new Action(2000, "Próxima caixa de texto", Action.ActionType.ADB_NEXT));
            ACTIONS.add(new Action(4000, "Próxima caixa de texto", Action.ActionType.ADB_NEXT));
            ACTIONS.add(new Action(2000, "Próxima caixa de texto", Action.ActionType.ADB_NEXT));
            ACTIONS.add(new Action(2000, "Próxima caixa de texto", Action.ActionType.ADB_NEXT));
            ACTIONS.add(new Action(new InputText("123456789", 5000), "Inserir nome"));
            ACTIONS.add(new Action(2000, "Próxima caixa de texto", Action.ActionType.ADB_NEXT));
            ACTIONS.add(new Action(new InputText("123456789", 5000), "Inserir nome"));
            ACTIONS.add(new Action(2000, "Próxima caixa de texto", Action.ActionType.ADB_NEXT));
            ACTIONS.add(new Action(2000, "Próxima caixa de texto", Action.ActionType.ADB_NEXT));
            ACTIONS.add(new Action(new InputText("dsadadaddada", 5000), "Inserir nome"));
            ACTIONS.add(new Action(2000, "Próxima caixa de texto", Action.ActionType.ADB_KEY_EVENT));
            ACTIONS.add(new Action(new Coordinate(385, 1100, 4000), "Confirmar"));
            ACTIONS.add(new Action(new Coordinate(525, 1000, 4000), "Guardar"));
            ACTIONS.add(new Action(new Coordinate(550, 1100, 4000), "Guardar"));
            ACTIONS.add(new Action(new Coordinate(525, 1000, 4000), "Guardar"));
            ACTIONS.add(new Action(new Coordinate(347, 1015, 4000), "Guardar"));
            ACTIONS.add(new Action(new Coordinate(300, 647, 4000), "Guardar"));
            ACTIONS.add(new Action(new InputText("dsadadaddada", 4000), "Inserir nome"));
            ACTIONS.add(new Action(new Coordinate(333, 1000, 4000), "Guardar"));
            ACTIONS.add(new Action(new Coordinate(340, 700, 4000), "Guardar"));
            ACTIONS.add(new Action(2000, "Próxima caixa de texto", Action.ActionType.ADB_KEY_EVENT));
            ACTIONS.add(new Action(new Coordinate(341, 538, 4000), "Guardar"));
            ACTIONS.add(new Action(new Coordinate(486, 682, 4000), "Guardar"));

            // Inicia a simulação
            isRunning = true;
            actionIndex = 0;
            startNextAction();
        }
    }

    // Método para parar a simulação
    public static void stopSimulation() {
        if (isRunning) {
            if (actionTimer != null) {
                actionTimer.cancel();
            }
            isRunning = false;
            actionIndex = 0; // Reinicia o índice da ação
            logMessage("Simulação parada.");
        }
    }

    // Método para iniciar a próxima ação
    private static void startNextAction() {
        if (isRunning) {
            Action action = ACTIONS.get(actionIndex);
            actionTimer = new Timer();
            actionTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (isRunning) {
                        switch (action.type) {
                            case TAP:
                                performTap(action.coordinate, action.description);
                                break;
                            case SWIPE:
                                performSwipe(action.swipe, action.description);
                                break;
                            case INPUT_TEXT:
                                performInputText(action.inputText, action.description);
                                break;
                            case ADB_KEY_EVENT:
                                performAdbKeyEvent(action.interval, action.description);
                                break;
                            case ADB_NEXT:
                                performAdbNext(action.interval, action.description);
                                break;
                            case ADB_ENTER:
                                performAdbEnter(action.interval, action.description);
                                break;

                        }
                        actionIndex++;
                        if (actionIndex >= ACTIONS.size()) {
                            // Se alcançar o fim das ações, reinicia o índice
                            actionIndex = 0;
                        }
                        startNextAction();
                    }
                }
            }, action.interval);
        }
    }

    // Método para simular toque na tela
    private static void performTap(Coordinate coordinate, String description) {
        try {
            String[] command = {ADB_PATH, "shell", "input", "tap", String.valueOf(coordinate.x), String.valueOf(coordinate.y)};
            Process process = Runtime.getRuntime().exec(command);
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                logMessage("Erro: Comando ADB falhou com código de saída " + exitCode);
            } else {
                logMessage("Toque simulado em (" + coordinate.x + ", " + coordinate.y + "): " + description);
            }
        } catch (IOException | InterruptedException e) {
            logMessage("Erro ao simular toque: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Método para simular swipe na tela
    private static void performSwipe(Swipe swipe, String description) {
        try {
            String[] command = {ADB_PATH, "shell", "input", "swipe", String.valueOf(swipe.startX), String.valueOf(swipe.startY), String.valueOf(swipe.endX), String.valueOf(swipe.endY)};
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            logMessage("Swipe simulado de (" + swipe.startX + ", " + swipe.startY + ") para (" + swipe.endX + ", " + swipe.endY + "): " + description);
        } catch (IOException | InterruptedException e) {
            logMessage("Erro ao simular swipe: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Método para simular evento de tecla ADB
    private static void performAdbKeyEvent(int interval, String description) {
        try {
            String[] command = {ADB_PATH, "shell", "input", "keyevent", "4"}; // 4 corresponde a KEYCODE_BACK
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            logMessage("Evento de tecla ADB executado: KEYCODE_BACK: " + description);
            Thread.sleep(interval); // Aguarda o intervalo especificado
        } catch (IOException | InterruptedException e) {
            logMessage("Erro ao executar evento de tecla ADB: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Método para simular próximo item focável via ADB
    private static void performAdbNext(int interval, String description) {
        try {
            String[] command = {ADB_PATH, "shell", "input", "keyevent", "61"}; // 20 corresponde a KEYCODE_DPAD_DOWN
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            logMessage("Evento de tecla ADB executado: Próximo item focável: " + description);
            Thread.sleep(interval); // Aguarda o intervalo especificado
        } catch (IOException | InterruptedException e) {
            logMessage("Erro ao executar próximo item focável via ADB: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Método para simular próximo item focável via ADB
    private static void performAdbEnter(int interval, String description) {
        try {
            String[] command = {ADB_PATH, "shell", "input", "keyevent", "66"}; // 20 corresponde a KEYCODE_DPAD_DOWN
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            logMessage("Evento de tecla ADB executado: Próximo item focável: " + description);
            Thread.sleep(interval); // Aguarda o intervalo especificado
        } catch (IOException | InterruptedException e) {
            logMessage("Erro ao executar próximo item focável via ADB: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Método para simular entrada de texto
    private static void performInputText(InputText inputText, String description) {
        try {
            String[] command = {ADB_PATH, "shell", "input", "text", inputText.text};
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            logMessage("Entrada de texto simulada: " + inputText.text + ": " + description);
        } catch (IOException | InterruptedException e) {
            logMessage("Erro ao simular entrada de texto: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Define a área de log para exibição das mensagens
    public static void setLogArea(JTextArea logArea) {
        MLTester.setLogArea(logArea);
    }

    // Método para registrar mensagens no log de forma thread-safe
    private static void logMessage(String message) {
        if (logArea != null) {
            SwingUtilities.invokeLater(() -> {
                logArea.append(message + "\n");
                logArea.setCaretPosition(logArea.getDocument().getLength());
            });
        } else {
            System.out.println(message); // Adiciona um log no console caso a logArea não esteja configurada
        }
    }

    // Método principal para criar e mostrar a interface gráfica
    public static void createAndShowGUI() {
        JFrame frame = new JFrame("ML Tester");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);

        JButton startButton = new JButton("Iniciar Simulação");
        startButton.addActionListener(e -> startSimulation());

        JButton stopButton = new JButton("Parar Simulação");
        stopButton.addActionListener(e -> stopSimulation());

        JPanel panel = new JPanel();
        panel.add(startButton);
        panel.add(stopButton);

        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(panel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }
}
