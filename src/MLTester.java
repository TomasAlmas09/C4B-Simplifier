import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MLTester {

    private static final String ADB_PATH = "adb"; // Caminho para o comando ADB
    private static Timer actionTimer;
    private static boolean isRunning = false;
    private static final List<Action> ACTIONS = new ArrayList<>();
    private static int actionIndex = 0;
    private static JTextArea logArea;

    // Classe interna para representar uma ação a ser executada
    private static class Action {
        enum ActionType {
            TAP, SWIPE, INPUT_TEXT, ADB_KEY_EVENT, ADB_NEXT
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
            ACTIONS.add(new Action(new Coordinate(392, 482, 4000), "Abrir turno"));
            ACTIONS.add(new Action(new Coordinate(379, 375, 4000), "Abrir serviço"));
            ACTIONS.add(new Action(new Coordinate(186, 358, 4000), "Linha"));
            ACTIONS.add(new Action(new Coordinate(186, 358, 4000), "Estação"));
            ACTIONS.add(new Action(new Coordinate(186, 358, 4000), "Dentro comboio"));
            ACTIONS.add(new Action(new Coordinate(100, 1200, 5000), "Confirmar Resumo"));
            ACTIONS.add(new Action(new Coordinate(100, 1200, 8000), "Infração"));
            ACTIONS.add(new Action(new Coordinate(300, 600, 4000), "Nível"));
            ACTIONS.add(new Action(new Coordinate(300, 600, 4000), "Artigo"));
            ACTIONS.add(new Action(new Swipe(500, 1000, 500, 0, 5000), "Swipe para baixo"));
            ACTIONS.add(new Action(new Coordinate(500, 1000, 5000), "Infrator recusou assinar"));
            // ACTIONS.add(new Action(new Coordinate(650, 688, 4000), "NS"));
            ACTIONS.add(new Action(new Coordinate(680, 1130, 4000), "Dados"));
            ACTIONS.add(new Action(new InputText("Hello", 5000), "Inserir nome"));
            ACTIONS.add(new Action(4000, "Próxima caixa de texto", Action.ActionType.ADB_NEXT));
            ACTIONS.add(new Action(2000, "Próxima caixa de texto", Action.ActionType.ADB_NEXT));
            ACTIONS.add(new Action(2000, "Próxima caixa de texto", Action.ActionType.ADB_NEXT));
            ACTIONS.add(new Action(new Coordinate(244, 491, 5000), "Documento de identificação"));
            ACTIONS.add(new Action(new Coordinate(90, 879, 5000), "Tipo de documento Outros"));
            // ACTIONS.add(new Action(new Swipe(500, 500, 500, 200, 4000), "Swipe para baixo"));
            // ACTIONS.add(new Action(new Coordinate(232, 363, 4000), "Selecionar campo detalhes de documento"));
            ACTIONS.add(new Action(4000, "Próxima caixa de texto", Action.ActionType.ADB_NEXT));
            ACTIONS.add(new Action(2000, "Próxima caixa de texto", Action.ActionType.ADB_NEXT));
            ACTIONS.add(new Action(2000, "Próxima caixa de texto", Action.ActionType.ADB_NEXT));
            ACTIONS.add(new Action(2000, "Próxima caixa de texto", Action.ActionType.ADB_NEXT));
            ACTIONS.add(new Action(new InputText("Doc", 4000), "Inserir detalhes do documento"));
            //  ACTIONS.add(new Action(4000, "Pressionar tecla de volta (BACK)", Action.ActionType.ADB_KEY_EVENT));
            ACTIONS.add(new Action(new Coordinate(385, 1100, 4000), "Confirmar"));
            ACTIONS.add(new Action(new Coordinate(385, 1100, 6000), "Guardar"));
            ACTIONS.add(new Action(new Coordinate(335, 730, 9000), "Confirmar Impressão"));
            ACTIONS.add(new Action(4000, "Pressionar tecla de volta (BACK)", Action.ActionType.ADB_KEY_EVENT));
            ACTIONS.add(new Action(new Coordinate(310, 510, 4000), "Terminal Serviço"));
            ACTIONS.add(new Action(new Coordinate(500, 740, 4000), "Confirmar"));
            ACTIONS.add(new Action(new Coordinate(385, 1100, 4000), "Fechar turno"));
            ACTIONS.add(new Action(new Coordinate(500, 1000, 4000), "Confirmar"));

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
            String[] command = {ADB_PATH, "shell", "input", "keyevent", "20"}; // 20 corresponde a KEYCODE_DPAD_DOWN
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
        MLTester.logArea = logArea;
    }

    // Método para registrar mensagens no log de forma thread-safe
    private static void logMessage(String message) {
        if (logArea != null) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    logArea.append(message + "\n");
                    logArea.setCaretPosition(logArea.getDocument().getLength());
                }
            });
        }
    }
}
