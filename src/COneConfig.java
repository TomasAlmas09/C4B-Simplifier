import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class COneConfig {

    private JFrame frame;
    private JComboBox<String> versionComboBox;
    private JTextArea logTextArea;

    public COneConfig() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Configuração C-One");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Criar combo box para seleção de versão
        String[] versions = {"v1", "v2"};
        versionComboBox = new JComboBox<>(versions);
        frame.add(versionComboBox, BorderLayout.NORTH);

        // Criar área de texto para logs com wrap text
        logTextArea = new JTextArea();
        logTextArea.setEditable(false);
        logTextArea.setLineWrap(true); // Ativa a quebra de linha automática
        logTextArea.setWrapStyleWord(true); // Quebra palavras em linha
        JScrollPane scrollPane = new JScrollPane(logTextArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Criar botão de instalação
        JButton installButton = new JButton("Instalar APKs");
        installButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                installApksSimultaneously();
            }
        });
        frame.add(installButton, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    public void installApksSimultaneously() {
        String selectedVersion = (String) versionComboBox.getSelectedItem();
        if (selectedVersion == null) {
            logMessage("Nenhuma versão selecionada.");
            return;
        }

        // Usar o diretório atual como base para o caminho
        File apkDirectory = new File(System.getProperty("user.dir") + "/C-ONE/" + selectedVersion);
        logMessage("Acessando diretório: " + apkDirectory.getAbsolutePath());

        if (!apkDirectory.exists() || !apkDirectory.isDirectory()) {
            logMessage("O diretório '" + apkDirectory.getPath() + "' não existe ou não é um diretório.");
            return;
        }

        File[] apkFiles = apkDirectory.listFiles((dir, name) -> name.toLowerCase().endsWith(".apk"));

        if (apkFiles == null || apkFiles.length == 0) {
            logMessage("Nenhum arquivo APK encontrado na pasta '" + apkDirectory.getPath() + "'.");
            return;
        }

        for (File apkFile : apkFiles) {
            logMessage("Encontrado arquivo APK: " + apkFile.getName());
            String apkFilePath = apkFile.getAbsolutePath();
            new Thread(() -> installAPK(apkFilePath)).start();
        }
    }

    private void installAPK(String apkFilePath) {
        logMessage("Instalando APK: " + apkFilePath);
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("adb", "install", apkFilePath);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                logMessage(line);
            }
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                logMessage("APK " + apkFilePath + " instalado com sucesso.");
            } else {
                logMessage("Falha na instalação do APK " + apkFilePath + ". Código de saída: " + exitCode);
            }
        } catch (IOException | InterruptedException ex) {
            logMessage("Erro ao instalar o APK " + apkFilePath + ": " + ex.getMessage());
        }
    }

    private void logMessage(String message) {
        SwingUtilities.invokeLater(() -> logTextArea.append(message + "\n"));
    }
}
