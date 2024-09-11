import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GetLogs4Mobi {

    /**
     * Método para coletar logs do 4Mobi com base nos parâmetros fornecidos.
     * @param company Nome da empresa.
     * @param terminal Número do terminal (formatado com zeros à esquerda se necessário).
     * @param logArea JTextArea onde serão registradas as mensagens de log.
     */
    public static void collectLogs(String company, String terminal, JTextArea logArea) {
        // Caminho base onde os logs estão armazenados no dispositivo Android
        String baseFolderPath = "/sdcard/card4b/public/";
        // Diretório local onde o arquivo será copiado no computador
        String localLogsDirectory = "4Mobi Logs";

        // Formata o número do terminal para ter 4 dígitos, preenchendo com zeros à esquerda se necessário
        terminal = adicionaZerosEsquerda(terminal);

        // Constrói o caminho final com base nas entradas
        String folderPath = baseFolderPath + company.toLowerCase() + "/machines/" + terminal + "/";

        // Chama o método para processar os logs com o caminho construído e o diretório local
        processLogs(folderPath, localLogsDirectory, logArea);
    }

    /**
     * Método privado para adicionar zeros à esquerda até o número ter 4 dígitos.
     * @param terminal Número do terminal a ser formatado.
     * @return Número do terminal formatado com zeros à esquerda.
     */
    private static String adicionaZerosEsquerda(String terminal) {
        return String.format("%04d", Integer.parseInt(terminal));
    }

    /**
     * Método privado para processar a lógica de coleta de logs.
     * @param folderPath Caminho onde os logs estão armazenados no dispositivo.
     * @param localLogsDirectory Diretório local onde o arquivo será copiado no computador.
     * @param logArea JTextArea onde serão registradas as mensagens de log.
     */
    private static void processLogs(String folderPath, String localLogsDirectory, JTextArea logArea) {
        // Comando para listar todos os arquivos na pasta com detalhes
        String listCommand = "adb shell ls -l " + folderPath;

        try {
            // Executa o comando para listar os arquivos na pasta
            Process listProcess = Runtime.getRuntime().exec(listCommand);

            // Lê a saída para obter o arquivo mais recente
            BufferedReader reader = new BufferedReader(new InputStreamReader(listProcess.getInputStream()));
            String line;
            String recentFile = null;
            long recentTimestamp = 0;

            // Itera sobre cada linha para determinar o arquivo mais recente
            while ((line = reader.readLine()) != null) {
                // Exemplo de formato de saída esperado: "-rw-rw-r-- 1 root sdcard_rw 12345 2024-06-19 14:30 arquivo.txt"
                // Pode variar dependendo do ambiente do Android
                String[] tokens = line.split("\\s+");
                if (tokens.length >= 6) {
                    String fileTimestamp = tokens[tokens.length - 3] + " " + tokens[tokens.length - 2];
                    long timestamp = parseTimestamp(fileTimestamp);

                    if (timestamp > recentTimestamp) {
                        recentTimestamp = timestamp;
                        recentFile = tokens[tokens.length - 1];
                    }
                }
            }

            // Fecha o leitor de saída
            reader.close();

            // Verifica se foi encontrado um arquivo mais recente
            if (recentFile != null) {
                // Nome completo do arquivo no dispositivo
                String fullFilePath = folderPath + recentFile.trim();

                // Cria o diretório logs se ele não existir
                File logsDir = new File(localLogsDirectory);
                if (!logsDir.exists()) {
                    logsDir.mkdirs();
                }

                // Nome do arquivo local (mantendo o mesmo nome do arquivo no dispositivo)
                String localFilePath = localLogsDirectory + File.separator + recentFile.trim();

                // Comando para copiar o arquivo do dispositivo para o computador local
                String pullCommand = "adb pull \"" + fullFilePath + "\" \"" + localFilePath + "\"";

                // Executa o comando para copiar o arquivo
                Process pullProcess = Runtime.getRuntime().exec(pullCommand);
                pullProcess.waitFor();

                // Verifica se o arquivo foi copiado com sucesso
                File localFile = new File(localFilePath);
                if (localFile.exists()) {
                    logMessage(logArea, "Arquivo copiado com sucesso para " + localFilePath);
                    // Abrir o diretório no explorador de arquivos após a conclusão do pull
                    File logsFolder = new File("4Mobi Logs");
                    if (logsFolder.exists() && logsFolder.isDirectory()) {
                        Desktop.getDesktop().open(logsFolder);
                    } else {
                        logMessage(logArea, "Pasta '4Driver Logs' não encontrada para abrir.");
                    }
                } else {
                    logMessage(logArea, "Falha ao copiar o arquivo para " + localLogsDirectory);
                }

            } else {
                logMessage(logArea, "Nenhum arquivo encontrado na pasta " + folderPath);
            }

        } catch (IOException e) {
            logMessage(logArea, "Erro de I/O ao executar o processo: " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            logMessage(logArea, "O processo foi interrompido: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            logMessage(logArea, "Ocorreu um erro inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Método auxiliar para registrar mensagens de log na JTextArea.
     * @param logArea JTextArea onde a mensagem será registrada.
     * @param message Mensagem a ser registrada.
     */
    private static void logMessage(JTextArea logArea, String message) {
        if (logArea != null) {
            logArea.append(message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        }
    }

    /**
     * Método para fazer o parse de um timestamp de string para long.
     * @param timestampStr String contendo o timestamp a ser parseado.
     * @return Timestamp convertido para long (milissegundos desde 1 de janeiro de 1970).
     */
    private static long parseTimestamp(String timestampStr) {
        // Formato esperado do timestamp: "2024-06-19 14:30"
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

        try {
            // Faz o parse da string para um objeto Date
            Date date = format.parse(timestampStr.trim());
            return date.getTime(); // Retorna o timestamp em milissegundos
        } catch (ParseException e) {
            // Em caso de erro no parse, imprime o erro e retorna 0
            System.err.println("Erro ao analisar o timestamp: " + timestampStr);
            e.printStackTrace();
            return 0;
        }
    }
}
