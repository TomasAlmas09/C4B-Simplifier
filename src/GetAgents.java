import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class GetAgents {

    /**
     * Método responsável por coletar o arquivo agent do dispositivo Android.
     * @param simplifier Instância de C4BSimplifier para exibir logs e interagir com a interface.
     * @param company Nome da empresa para determinar o diretório correto no dispositivo.
     */
    public static void collectAgent(C4BSimplifier simplifier, String company) {
        simplifier.logMessage("Recolhendo arquivo agent para Empresa: " + company); // Registra mensagem de início de operação

        try {
            // Comando para listar os arquivos no diretório agents no dispositivo Android com base na empresa selecionada
            String adbListCommand = "adb shell ls -t /sdcard/card4b/public/" + company.toLowerCase() + "/agents/";
            Process listProcess = Runtime.getRuntime().exec(adbListCommand); // Executa o comando adb
            BufferedReader listReader = new BufferedReader(new InputStreamReader(listProcess.getInputStream()));
            String latestFile = null;
            String line;

            // Procura pelo arquivo mais recente
            while ((line = listReader.readLine()) != null) {
                latestFile = line.trim();
                break; // Encontra o mais recente e sai do loop
            }

            int listExitCode = listProcess.waitFor(); // Espera até que o processo de listagem seja concluído

            // Verifica se houve erro na listagem ou se nenhum arquivo foi encontrado
            if (listExitCode != 0 || latestFile == null || latestFile.isEmpty()) {
                simplifier.logMessage("Falha ao listar arquivos ou nenhum arquivo encontrado para Empresa: " + company);
                return;
            }

            // Caminho completo do arquivo agent no dispositivo
            String fullFilePath = "/sdcard/card4b/public/" + company.toLowerCase() + "/agents/" + latestFile;

            // Constrói o comando adb pull para transferir o arquivo agent para o computador
            String pullDestination = "Agentes" + File.separator + latestFile; // Caminho onde o arquivo será salvo localmente
            String adbPullCommand = "adb pull " + fullFilePath + " \"" + pullDestination + "\"";
            Process pullProcess = Runtime.getRuntime().exec(adbPullCommand); // Executa o comando adb pull
            BufferedReader pullReader = new BufferedReader(new InputStreamReader(pullProcess.getInputStream()));

            // Lê e exibe as linhas de output do comando adb pull
            while ((line = pullReader.readLine()) != null) {
                simplifier.logMessage(line);
            }

            int pullExitCode = pullProcess.waitFor(); // Espera até que o processo de pull seja concluído

            // Verifica o código de saída do comando adb pull
            if (pullExitCode == 0) {
                simplifier.logMessage("Arquivo agent recolhido com sucesso para Empresa: " + company);

                // Abrir o diretório no explorador de arquivos após a conclusão do pull
                File agentsFolder = new File("Agentes");
                if (agentsFolder.exists() && agentsFolder.isDirectory()) {
                    Desktop.getDesktop().open(agentsFolder);
                } else {
                    simplifier.logMessage("Pasta 'Agentes' não encontrada para abrir.");
                }
            } else {
                simplifier.logMessage("Falha ao recolher arquivo agent para Empresa: " + company + ". Código de saída: " + pullExitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace(); // Imprime o stack trace da exceção
            simplifier.logMessage("Erro ao executar o comando ADB para Empresa " + company + ": " + e.getMessage()); // Registra mensagem de erro
        }
    }
}
