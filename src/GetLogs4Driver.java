import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class GetLogs4Driver {

    /**
     * Método responsável por coletar os logs do 4Driver.
     * @param simplifier Instância de C4BSimplifier para exibir logs e interagir com a interface.
     */
    public static void collectLogs(C4BSimplifier simplifier) {
        simplifier.showLogWindow(); // Mostra a janela de log no aplicativo
        simplifier.logMessage("Recolhendo logs do 4Driver."); // Registra mensagem de início de operação

        try {
            // Comando para listar os arquivos no diretório de logs no dispositivo Android
            String adbListCommand = "adb shell ls -t /sdcard/card4b/public/outbox/logs/";
            Process listProcess = Runtime.getRuntime().exec(adbListCommand); // Executa o comando adb
            BufferedReader listReader = new BufferedReader(new InputStreamReader(listProcess.getInputStream()));
            String latestFile = null;
            String line;

            // Procura pelo arquivo mais recente que contém "logs" no nome
            while ((line = listReader.readLine()) != null) {
                if (line.contains("logs")) {
                    latestFile = line.trim();
                    break; // Encontra o mais recente e sai do loop
                }
            }

            int listExitCode = listProcess.waitFor(); // Espera até que o processo de listagem seja concluído

            // Verifica se houve erro na listagem ou se nenhum arquivo 'logs' foi encontrado
            if (listExitCode != 0 || latestFile == null || latestFile.isEmpty()) {
                simplifier.logMessage("Falha ao listar arquivos ou nenhum arquivo 'logs' encontrado.");
                return;
            }

            // Caminho completo do arquivo de log no dispositivo
            String fullFilePath = "/sdcard/card4b/public/outbox/logs/" + latestFile;

            // Constrói o comando adb pull para transferir o arquivo de log para o computador
            String adbPullCommand = "adb pull " + fullFilePath + " \"4Driver Logs/" + new File(latestFile).getName() + "\"";
            Process pullProcess = Runtime.getRuntime().exec(adbPullCommand); // Executa o comando adb pull
            BufferedReader pullReader = new BufferedReader(new InputStreamReader(pullProcess.getInputStream()));

            // Lê e exibe as linhas de output do comando adb pull
            while ((line = pullReader.readLine()) != null) {
                simplifier.logMessage(line);
            }

            int pullExitCode = pullProcess.waitFor(); // Espera até que o processo de pull seja concluído

            // Verifica o código de saída do comando adb pull
            if (pullExitCode == 0) {
                simplifier.logMessage("Logs do 4Driver recolhidos com sucesso.");
            } else {
                simplifier.logMessage("Falha ao recolher logs do 4Driver. Código de saída: " + pullExitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace(); // Imprime o stack trace da exceção
            simplifier.logMessage("Erro ao executar o comando ADB: " + e.getMessage()); // Registra mensagem de erro
        }
    }
}
