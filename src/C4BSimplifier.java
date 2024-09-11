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
        setSize(1000, 250); // Define o tamanho da janela
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Encerra o programa ao fechar a janela
        setLocationRelativeTo(null); // Centraliza a janela na ecrã
        setLayout(new BorderLayout()); // Define o layout da janela como BorderLayout

        // Criar pastas "APK", "logs" e "4Driver Logs" se não existirem
        createDirectoryIfNotExists("apk");
        createDirectoryIfNotExists("4Mobi Logs");
        createDirectoryIfNotExists("4Driver Logs");
        createDirectoryIfNotExists("Screenshots");
        createDirectoryIfNotExists("Recordings");
        createDirectoryIfNotExists("Agentes");

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
        JButton helperButton = new JButton("HELP"); // Cria um botão com o texto "Help"
        helperButton.setPreferredSize(new Dimension(700, 300)); // Define o tamanho do botão
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
        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 10, 10)); // Cria um painel com layout de grade
        JButton option1Button = new JButton("TESTE DE AUTOS - ML"); // Botão para iniciar teste de autos
        JButton tmlTester = new JButton("TESTE DE AUTOS - TML"); // Botão para iniciar teste de autos
        JButton option2Button = new JButton("Instalar APK"); // Botão para instalar APK
        JButton option3Button = new JButton("Recolher logs 4Mobi"); // Botão para coletar logs 4Mobi
        JButton option4Button = new JButton("Recolher logs 4Driver"); // Botão para coletar logs 4Driver
        JButton option5Button = new JButton("Ecrã Remoto"); // Botão para controle de ecrã remoto
        JButton screenshotButton = new JButton("Capturar ecrã"); // Botão para capturar ecrã
        JButton stopServicesButton = new JButton("Stop Services"); // Botão para parar serviços
        JButton startRecordingButton = new JButton("Gravar ecrã"); // Botão para iniciar a gravação de ecrã
        JButton getAgents = new JButton("Get Agentes"); // Botão para abrir
        JButton apexTester = new JButton("Apex Tester"); // Botão para abrir
        JButton cardEditor = new JButton("Card Editor"); // Botão para abrir
        JButton coneConfig = new JButton("C-One Config"); // Botão para abrir
        JButton openCard4CardsButton = new JButton("Abrir Card4Cards"); // Botão para abrir o aplicativo Card4Cards

        buttonPanel.add(option2Button); // Adiciona botão ao painel
        buttonPanel.add(option3Button); // Adiciona botão ao painel
        buttonPanel.add(option4Button); // Adiciona botão ao painel
        buttonPanel.add(option5Button); // Adiciona botão ao painel
        buttonPanel.add(screenshotButton); // Adiciona o novo botão de captura de ecrã ao painel
        buttonPanel.add(startRecordingButton); // Adiciona o botão de iniciar gravação ao painel
        buttonPanel.add(stopServicesButton); // Adiciona o novo botão de parar serviços ao painel
        buttonPanel.add(option1Button); // Adiciona botão ao painel
        buttonPanel.add(tmlTester); // Adiciona botão ao painel
        buttonPanel.add(getAgents); // Adiciona botão ao painel
        buttonPanel.add(apexTester); // Adiciona botão ao painel
        buttonPanel.add(cardEditor); // Adiciona botão ao painel
        buttonPanel.add(coneConfig); // Adiciona botão ao painel
        buttonPanel.add(openCard4CardsButton); // Adiciona o botão de abrir Card4Cards ao painel
        add(buttonPanel, BorderLayout.CENTER); // Adiciona o painel de botões ao centro da janela principal
        helperButton.setPreferredSize(new Dimension(70, 30));

        add(buttonPanel, BorderLayout.CENTER); // Adiciona o painel de botões ao centro da janela principal

        // Ação do botão "Abrir Card4Cards"
        openCard4CardsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openCard4Cards();
            }
        });

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

        // Ação do botão 1 (Teste de autos)
        tmlTester.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isSingleAdbDeviceConnected()) {
                    showErrorMessage("Nenhum dispositivo ADB encontrado ou mais de um dispositivo conectado. Verifique a conexão dos dispositivos.");
                    return;
                }
                showLogWindow();
                logMessage("1. Teste de autos TML iniciado.");
                TMLTester.startSimulation();
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
                File logsFolder = new File("apk");
                if (logsFolder.exists() && logsFolder.isDirectory()) {
                    try {
                        Desktop.getDesktop().open(logsFolder);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    String apkFileName = JOptionPane.showInputDialog(C4BSimplifier.this, "Digite o nome do arquivo APK:");
                    if (apkFileName != null && !apkFileName.isEmpty()) {
                        showLogWindow();
                        logMessage("Instalação do APK '" + apkFileName + "' iniciada.");
                        InstallerAPK.installApkWithDelay(apkFileName.trim()); // Inicia a instalação do APK com o nome fornecido
                    } else {
                        showErrorMessage("Nome do arquivo APK inválido.");
                    }
                }else{
                    showErrorMessage("Dirétorio apk não encontrado.");
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
                Scrcpy.executeAdbSrcSpy(); // Inicia o controle de ecrã remoto
            }
        });

        // Ação do botão de captura de ecrã
        screenshotButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isSingleAdbDeviceConnected()) {
                    showErrorMessage("Nenhum dispositivo ADB encontrado ou mais de um dispositivo conectado. Verifique a conexão dos dispositivos.");
                    return;
                }
                ScreenShot.captureScreenShot(); // Chama o método de captura de ecrã
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

        // Ação do botão Start Recording
        cardEditor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardEditor.executeCardEditor();
            }
        });

        // Ação do botão Start Recording
        apexTester.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                File logsFolder = new File("packages/apexTester/app");
                if (logsFolder.exists() && logsFolder.isDirectory()) {
                    try {
                        showInfo("Abre o arquivo run.bat");
                        Desktop.getDesktop().open(logsFolder);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }else{
                    showErrorMessage("Dirétorio packages/apexTester/app não encontrado.");
                }

            }
        });

        // Ação do botão Get Agentes
        getAgents.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isSingleAdbDeviceConnected()) {
                    showErrorMessage("Nenhum dispositivo ADB encontrado ou mais de um dispositivo conectado. Verifique a conexão dos dispositivos.");
                    return;
                }
                showAgentCollectionForm(); // Mostra o formulário para coletar agentes
            }
        });


        // Ação do botão C-One Config
        coneConfig.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logMessage("Botão C-One Config pressionado."); // Log para verificar se a ação está sendo disparada
                new COneConfig(); // Criar e exibir a janela de configuração
            }
        });




    }

    // Método para exibir o formulário de coleta de agentes
    private void showAgentCollectionForm() {
        JFrame agentFormFrame = new JFrame("Recolher Agentes"); // Cria uma nova janela para o formulário de coleta de agentes
        agentFormFrame.setSize(400, 200); // Define o tamanho da janela
        agentFormFrame.setLayout(new GridLayout(3, 1)); // Define o layout da janela como GridLayout
        agentFormFrame.setLocationRelativeTo(this); // Define a posição da janela em relação a esta janela

        JLabel companyLabel = new JLabel("Empresa:"); // Rótulo para selecionar a empresa
        JComboBox<String> companyField = new JComboBox<>(new String[]{"ml", "rdl", "tml", "ca"}); // Campo para selecionar a empresa

        JButton confirmButton = new JButton("Confirmar"); // Botão para confirmar a coleta de agentes

        agentFormFrame.add(companyLabel); // Adiciona o rótulo da empresa à janela
        agentFormFrame.add(companyField); // Adiciona o campo de seleção de empresa à janela
        agentFormFrame.add(confirmButton); // Adiciona o botão de confirmação à janela

        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String company = (String) companyField.getSelectedItem(); // Obtém a empresa selecionada
                agentFormFrame.dispose(); // Fecha a janela de formulário
                collectAgents(company); // Chama o método para coletar agentes com a empresa selecionada
            }
        });

        agentFormFrame.setVisible(true); // Torna a janela do formulário visível
    }

    // Método para coletar os agentes com base na empresa selecionada
    private void collectAgents(String company) {
        showLogWindow(); // Mostra a janela de log no aplicativo
        logMessage("Recolhendo agentes para Empresa: " + company); // Registra mensagem de início de operação
        GetAgents.collectAgent(this, company); // Chama o método para coletar agentes na classe GetAgents
    }


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
        JComboBox<String> companyField = new JComboBox<>(new String[]{"ml", "ca","tml", "rdl"}); // Campo para selecionar a empresa

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
    // Exibe uma mensagem de erro em uma caixa de diálogo.
    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Erro de Conexão ADB", JOptionPane.INFORMATION_MESSAGE); // Exibe a mensagem de info em uma caixa de diálogo
    }

    // Método para abrir o aplicativo Card4Cards
    private void openCard4Cards() {

        // Caminho para o diretório onde o aplicativo Card4Cards.exe está localizado
        String userHome = System.getProperty("user.home");
        String appPath = userHome + File.separator + "appdata" + File.separator + "roaming" + File.separator + ".Card4Cards" + File.separator + "bin" + File.separator + "Card4Cards.exe";
        File appFile = new File(appPath);

        if (!appFile.exists()) {
            showErrorMessage("O arquivo Card4Cards.exe não foi encontrado no caminho especificado: " + appPath);
            return;
        }

        try {
            JOptionPane.showMessageDialog(this, "Card4Cards aberto com sucesso!\n\n              Clique OK", "Sucess", JOptionPane.INFORMATION_MESSAGE); // Exibe a mensagem de erro em uma caixa de diálogo
            Runtime.getRuntime().exec(appFile.getAbsolutePath());
        } catch (IOException e) {
            showErrorMessage("Erro ao tentar abrir o aplicativo Card4Cards: " + e.getMessage());
        }
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
