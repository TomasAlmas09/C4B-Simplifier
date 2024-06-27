import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class C4BSimplifier extends JFrame {

    private JTextArea logArea; // Área de texto para exibição de logs

    public C4BSimplifier() {
        // Configuração do JFrame principal
        setTitle("Card4b - Simplifier"); // Define o título da janela
        setSize(700, 600); // Define o tamanho da janela
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Encerra o programa ao fechar a janela
        setLocationRelativeTo(null); // Centraliza a janela na tela
        setLayout(new BorderLayout()); // Define o layout da janela como BorderLayout

        // Criar pastas "APK", "logs" e "4Driver Logs" se não existirem
        createDirectoryIfNotExists("apk");
        createDirectoryIfNotExists("4Mobi Logs");
        createDirectoryIfNotExists("4Driver Logs");
        createDirectoryIfNotExists("Screenshots");
        createDirectoryIfNotExists("Recordings");

        // Painel superior com logo e título
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Cria um painel com layout de fluxo centralizado
        ImageIcon originalIcon = new ImageIcon("assets/card4b-logo.png"); // Carrega o ícone do logo
        Image originalImage = originalIcon.getImage().getScaledInstance(100, 60, Image.SCALE_SMOOTH); // Redimensiona o ícone
        ImageIcon logoIcon = new ImageIcon(originalImage); // Cria um novo ícone redimensionado
        JLabel logoLabel = new JLabel(logoIcon); // Cria um rótulo com o ícone
        JLabel titleLabel = new JLabel("Card4b - Simplifier", SwingConstants.CENTER); // Cria um rótulo com o título centralizado
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30)); // Define a fonte do título
        titlePanel.add(logoLabel); // Adiciona o ícone ao painel
        titlePanel.add(titleLabel); // Adiciona o título ao painel
        add(titlePanel, BorderLayout.NORTH); // Adiciona o painel ao norte da janela principal

        // Botão para abrir helper.txt
        JButton helperButton = new JButton("Help"); // Cria um botão com o texto "Help"
        helperButton.setPreferredSize(new Dimension(30, 30)); // Define o tamanho pequeno
        helperButton.setToolTipText("Abrir helper.txt");
        helperButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openHelperFile();
            }
        });
        titlePanel.add(helperButton);

        // Ícone do aplicativo
        setIconImage(originalIcon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH)); // Define o ícone da aplicação

        // Painel central com botões
        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 20, 20)); // Cria um painel com layout de grade
        JButton option1Button = new JButton("TESTE DE AUTOS - ML"); // Botão para iniciar teste de autos
        JButton option2Button = new JButton("Instalar APK"); // Botão para instalar APK
        JButton option3Button = new JButton("Recolher logs 4Mobi"); // Botão para coletar logs 4Mobi
        JButton option4Button = new JButton("Recolher logs 4Driver"); // Botão para coletar logs 4Driver
        JButton option5Button = new JButton("Ecrã Remoto"); // Botão para controle de tela remoto
        JButton screenshotButton = new JButton("Capturar Tela"); // Botão para capturar tela
        JButton stopServicesButton = new JButton("Stop Services"); // Botão para parar serviços
        JButton startRecordingButton = new JButton("Start Recording"); // Botão para iniciar a gravação de tela
        buttonPanel.add(option1Button); // Adiciona botão ao painel
        buttonPanel.add(option2Button); // Adiciona botão ao painel
        buttonPanel.add(option3Button); // Adiciona botão ao painel
        buttonPanel.add(option4Button); // Adiciona botão ao painel
        buttonPanel.add(option5Button); // Adiciona botão ao painel
        buttonPanel.add(screenshotButton); // Adiciona o novo botão de captura de tela ao painel
        buttonPanel.add(stopServicesButton); // Adiciona o novo botão de parar serviços ao painel
        buttonPanel.add(startRecordingButton); // Adiciona o botão de iniciar gravação ao painel
        add(buttonPanel, BorderLayout.CENTER); // Adiciona o painel de botões ao centro da janela principal
        helperButton.setPreferredSize(new Dimension(70, 30));

        add(buttonPanel, BorderLayout.CENTER); // Adiciona o painel de botões ao centro da janela principal



        // Ação do botão 1 (Teste de autos)
        option1Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isSingleAdbDeviceConnected()) {
                    showErrorMessage("Nenhum dispositivo ADB encontrado ou mais de um dispositivo conectado. Verifique a conexão dos dispositivos.");
                    return;
                }
                showLogWindow();
                logMessage("1. Teste de autos iniciado.");
                MLTester.startSimulation();
            }
        });

        // Ação do botão 2 (Instalar APK)
        option2Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isSingleAdbDeviceConnected()) {
                    showErrorMessage("Nenhum dispositivo ADB encontrado ou mais de um dispositivo conectado. Verifique a conexão dos dispositivos.");
                    return;
                }

                String apkFileName = JOptionPane.showInputDialog(C4BSimplifier.this, "Digite o nome do arquivo APK:");
                if (apkFileName != null && !apkFileName.isEmpty()) {
                    showLogWindow();
                    logMessage("Instalação do APK '" + apkFileName + "' iniciada.");
                    InstallerAPK.installApkWithDelay(apkFileName.trim()); // Inicia a instalação do APK com o nome fornecido
                } else {
                    showErrorMessage("Nome do arquivo APK inválido.");
                }
            }
        });

        // Ação do botão 3 (Recolher logs C-ONE)
        option3Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isSingleAdbDeviceConnected()) {
                    showErrorMessage("Nenhum dispositivo ADB encontrado ou mais de um dispositivo conectado. Verifique a conexão dos dispositivos.");
                    return;
                }
                showLogCollectionForm();
            }
        });

        // Ação do botão 4 (Recolher logs 4Driver)
        option4Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isSingleAdbDeviceConnected()) {
                    showErrorMessage("Nenhum dispositivo ADB encontrado ou mais de um dispositivo conectado. Verifique a conexão dos dispositivos.");
                    return;
                }
                GetLogs4Driver.collectLogs(C4BSimplifier.this);
            }
        });

        // Ação do botão de verificação de ADB
        option5Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isSingleAdbDeviceConnected()) {
                    showErrorMessage("Nenhum dispositivo ADB encontrado ou mais de um dispositivo conectado. Verifique a conexão dos dispositivos.");
                    return;
                }
                Scrcpy.executeAdbSrcSpy(); // Inicia o controle de tela remoto
            }
        });

        // Ação do botão de captura de tela
        screenshotButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isSingleAdbDeviceConnected()) {
                    showErrorMessage("Nenhum dispositivo ADB encontrado ou mais de um dispositivo conectado. Verifique a conexão dos dispositivos.");
                    return;
                }
                ScreenShot.captureScreenShot(); // Chama o método de captura de tela
            }
        });

        // Ação do botão Stop Services
        stopServicesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isSingleAdbDeviceConnected()) {
                    showErrorMessage("Nenhum dispositivo ADB encontrado ou mais de um dispositivo conectado. Verifique a conexão dos dispositivos.");
                    return;
                }
                showLogWindow();
                logMessage("Parando serviços 4driver ou 4mobi.");
                StopServices.stopServices();
                logMessage("Serviços 4driver ou 4mobi foram parados.");
            }
        });

        // Ação do botão Start Recording
        startRecordingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isSingleAdbDeviceConnected()) {
                    showErrorMessage("Nenhum dispositivo ADB encontrado ou mais de um dispositivo conectado. Verifique a conexão dos dispositivos.");
                    return;
                }

                ScreenRecordWithScrcpy.startRecording(C4BSimplifier.this);
            }
        });
    }

    // Restante do código da classe...

    // Verifica se há exatamente um dispositivo ADB conectado.
    private boolean isSingleAdbDeviceConnected() {
        try {
            Process process = Runtime.getRuntime().exec("adb devices"); // Executa o comando adb devices
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream())); // Lê a saída do processo
            String line;
            int deviceCount = 0;
            while ((line = reader.readLine()) != null) {
                if (line.matches("\\w+\\s+device")) { // Verifica se a linha contém um dispositivo conectado
                    deviceCount++;
                }
            }
            return deviceCount == 1; // Retorna true se apenas um dispositivo estiver conectado
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Exibe uma janela de log.
    void showLogWindow() {
        JFrame logFrame = new JFrame("Logs"); // Cria uma nova janela de log
        logFrame.setSize(500, 400); // Define o tamanho da janela
        logFrame.setLayout(new BorderLayout()); // Define o layout da janela como BorderLayout
        logArea = new JTextArea(); // Cria uma área de texto para os logs
        logArea.setEditable(false); // Torna a área de texto somente leitura
        JScrollPane scrollPane = new JScrollPane(logArea); // Cria um painel de rolagem para a área de texto
        JButton stopButton = new JButton("Fechar"); // Botão para parar a simulação
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MLTester.stopSimulation(); // Para a simulação
                logFrame.dispose(); // Fecha a janela de log
            }
        });
        logFrame.add(scrollPane, BorderLayout.CENTER); // Adiciona a área de texto ao centro da janela
        logFrame.add(stopButton, BorderLayout.SOUTH); // Adiciona o botão de parar ao sul da janela
        logFrame.setVisible(true); // Torna a janela de log visível
        InstallerAPK.setLogArea(logArea); // Define a área de log para a instalação de APKs
    }

    // Método para abrir o arquivo helper.txt
    private void openHelperFile() {
        try {
            File file = new File("helper.txt");
            Desktop desktop = Desktop.getDesktop();
            desktop.open(file);
        } catch (IOException ex) {
            ex.printStackTrace();
            showErrorMessage("Erro ao abrir o arquivo helper.txt");
        }
    }

    // Exibe um formulário para a coleta de logs 4Mobi.
    void showLogCollectionForm() {
        JFrame formFrame = new JFrame("Recolher logs 4Mobi"); // Cria uma nova janela para o formulário de coleta de logs
        formFrame.setSize(600, 500); // Define o tamanho da janela
        formFrame.setLayout(new GridLayout(4, 2, 10, 10)); // Define o layout da janela como GridLayout
        formFrame.setLocationRelativeTo(this); // Define a posição da janela em relação a esta janela

        JLabel companyLabel = new JLabel("Empresa:"); // Rótulo para selecionar a empresa
        JComboBox<String> companyField = new JComboBox<>(new String[]{"ml", "ca","tml"}); // Campo para selecionar a empresa

        JLabel terminalLabel = new JLabel("Número do Terminal:"); // Rótulo para digitar o número do terminal
        JTextField terminalField = new JTextField(); // Campo para digitar o número do terminal

        JButton confirmButton = new JButton("Confirmar"); // Botão para confirmar a coleta de logs

        formFrame.add(companyLabel); // Adiciona o rótulo da empresa à janela
        formFrame.add(companyField); // Adiciona o campo de seleção de empresa à janela
        formFrame.add(terminalLabel); // Adiciona o rótulo do terminal à janela
        formFrame.add(terminalField); // Adiciona o campo de digitação do terminal à janela
        formFrame.add(new JLabel()); // Adiciona um rótulo vazio para preenchimento do layout
        formFrame.add(confirmButton); // Adiciona o botão de confirmação à janela

        JTextArea formLogArea = new JTextArea(); // Área de texto para exibição de logs do formulário
        formLogArea.setEditable(false); // Torna a área de texto somente leitura
        JScrollPane formLogScrollPane = new JScrollPane(formLogArea); // Cria um painel de rolagem para a área de texto
        formFrame.add(formLogScrollPane); // Adiciona o painel de rolagem à janela

        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String company = (String) companyField.getSelectedItem(); // Obtém a empresa selecionada
                String terminal = terminalField.getText(); // Obtém o número do terminal digitado
                if (terminal.isEmpty()) {
                    showErrorMessage("Por favor, preencha o número do terminal."); // Exibe mensagem de erro se o número do terminal estiver vazio
                    return;
                }
                logMessage("Recolhendo logs para Empresa: " + company + " | Terminal: " + terminal); // Registra a mensagem de log
                formLogArea.append("Recolhendo logs para Empresa: " + company + " | Terminal: " + terminal + "\n"); // Adiciona mensagem à área de texto
                invokeGetLogs(company, terminal, formLogArea); // Inicia a coleta de logs
            }
        });

        formFrame.setVisible(true); // Torna a janela do formulário visível
    }

    // Invoca o processo de coleta de logs.
    private void invokeGetLogs(String company, String terminal, JTextArea formLogArea) {
        String baseFolderPath = "/sdcard/card4b/public/"; // Caminho base para a coleta de logs
        String folderPath = baseFolderPath + company.toLowerCase() + "/machines/" + terminal + "/"; // Caminho completo para os logs
        logMessage("Logs coletados para Empresa: " + company + " | Terminal: " + terminal); // Registra a mensagem de log
        GetLogs4Mobi.collectLogs(company, terminal, formLogArea); // Chama o método para coletar logs 4Mobi
    }

    // Cria um diretório se ele não existir.
    private void createDirectoryIfNotExists(String directoryName) {
        File directory = new File(directoryName); // Cria um objeto File com o nome do diretório
        if (!directory.exists()) { // Verifica se o diretório não existe
            boolean created = directory.mkdirs(); // Cria o diretório e todos os diretórios pais necessários
            if (created) {
                System.out.println("Diretório criado: " + directoryName); // Exibe mensagem se o diretório foi criado com sucesso
            } else {
                System.err.println("Falha ao criar diretório: " + directoryName); // Exibe mensagem de erro se falhar ao criar o diretório
            }
        }
    }

    // Registra uma mensagem de log na área de texto.
    void logMessage(String message) {
        if (logArea != null) {
            logArea.append(message + "\n"); // Adiciona a mensagem à área de texto
            logArea.setCaretPosition(logArea.getDocument().getLength()); // Define o cursor para o final do texto
        }
    }

    // Exibe uma mensagem de erro em uma caixa de diálogo.
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Erro de Conexão ADB", JOptionPane.ERROR_MESSAGE); // Exibe a mensagem de erro em uma caixa de diálogo
    }

    // Método principal para iniciar o aplicativo.
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new C4BSimplifier().setVisible(true); // Cria e exibe a interface gráfica do aplicativo
            }
        });
    }
}
