import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;

public class InstallerAPK {

    private static final String ADB_PATH = "adb"; // Caminho para o comando ADB
    private static JTextArea logArea;

    /**
     * Instala um arquivo APK com um atraso de 1 segundo.
     * @param apkFileName Nome do arquivo APK a ser instalado.
     */
    public static void installApkWithDelay(String apkFileName) {
        // Aguarda 1 segundo e então executa o instalador APK com o arquivo especificado
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                File apkFile = findApkFileByName("apk/", apkFileName);

                if (apkFile != null) {
                    logMessage("Instalando APK: " + apkFile.getName() + "\n");
                    installApk(apkFile);
                } else {
                    logMessage("Nenhum arquivo APK encontrado com o nome '" + apkFileName + "'.");
                }
                timer.cancel(); // Cancela o timer após a execução
            }
        }, 1000); // 1000 milissegundos = 1 segundo
    }

    /**
     * Instala um arquivo APK especificado.
     * @param apkFile Arquivo APK a ser instalado.
     */
    public static void installApk(File apkFile) {
        String command = ADB_PATH + " install -r -d \"" + apkFile.getAbsolutePath() + "\"";

        try {
            // Executa o comando adb para instalar o arquivo APK
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                logMessage(line); // Registra cada linha de output como log
            }

            int exitCode = process.waitFor();

            // Verifica o código de saída do processo
            if (exitCode == 0) {
                logMessage("Instalação bem-sucedida: " + apkFile.getName());
            } else {
                logMessage("Erro ao instalar APK: " + apkFile.getName());
            }
        } catch (IOException | InterruptedException e) {
            logMessage("Erro ao executar comando adb: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Define a JTextArea onde serão registradas as mensagens de log.
     * @param logArea JTextArea para registro de logs.
     */
    public static void setLogArea(JTextArea logArea) {
        InstallerAPK.logArea = logArea;
    }

    /**
     * Registra uma mensagem na JTextArea de log.
     * @param message Mensagem a ser registrada.
     */
    private static void logMessage(String message) {
        if (logArea != null) {
            SwingUtilities.invokeLater(() -> {
                logArea.append(message + "\n");
                logArea.setCaretPosition(logArea.getDocument().getLength());
            });
        }
    }

    /**
     * Lista todos os arquivos APK em um diretório especificado.
     * @param directory Diretório onde os arquivos APK serão listados.
     * @return Array de File contendo os arquivos APK.
     */
    private static File[] listApkFiles(String directory) {
        File dir = new File(directory);
        return dir.listFiles((dir1, name) ->
                name.toLowerCase().endsWith(".apk"));
    }

    /**
     * Encontra o arquivo APK mais recente em um diretório especificado.
     * @param directory Diretório onde os arquivos APK serão procurados.
     * @return Arquivo APK mais recente.
     */
    private static File findLatestApkFile(String directory) {
        File[] apkFiles = listApkFiles(directory);

        if (apkFiles == null || apkFiles.length == 0) {
            return null;
        }

        // Ordena os arquivos por data de modificação (do mais recente para o mais antigo)
        Arrays.sort(apkFiles, Comparator.comparingLong(File::lastModified).reversed());

        // Retorna o arquivo mais recente
        return apkFiles[0];
    }

    /**
     * Encontra um arquivo APK pelo nome exato em um diretório especificado.
     * @param directory Diretório onde os arquivos APK serão procurados.
     * @param apkFileName Nome exato do arquivo APK a ser encontrado.
     * @return Arquivo APK encontrado ou null se não encontrado.
     */
    private static File findApkFileByName(String directory, String apkFileName) {
        // Verifica se o nome do arquivo já termina com .apk
        if (!apkFileName.toLowerCase().endsWith(".apk")) {
            apkFileName += ".apk"; // Acrescenta a extensão .apk se não estiver presente
        }

        File[] apkFiles = listApkFiles(directory);

        if (apkFiles == null || apkFiles.length == 0) {
            return null;
        }

        // Procura pelo arquivo com o nome especificado
        for (File apkFile : apkFiles) {
            if (apkFile.getName().equalsIgnoreCase(apkFileName)) {
                return apkFile;
            }
        }

        return null;
    }
}
