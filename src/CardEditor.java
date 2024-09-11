import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class CardEditor {
    private static JFrame parentFrame; // Janela pai para exibir mensagens

    public CardEditor(JFrame parentFrame) {
        CardEditor.parentFrame = parentFrame; // Inicializa a variável estática parentFrame
    }

    // Método estático para executar o WildcardEditor.exe
    public static void executeCardEditor() {
        // Caminho completo para o executável WildcardEditor.exe
        String exePath = "packages/cardEditor/WildcardEditor.exe";
        System.out.println("Caminho completo do executável: " + exePath);

        // Verifica se o arquivo existe
        File exeFile = new File(exePath);
        if (!exeFile.exists()) {
            // Exibe mensagem de erro se o arquivo não for encontrado
            JOptionPane.showMessageDialog(parentFrame,
                    "O arquivo WildcardEditor.exe não foi encontrado. Verifique se ele está no caminho: " + exePath,
                    "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Construindo o comando para executar o arquivo
        ProcessBuilder processBuilder = new ProcessBuilder(exePath);

        try {
            // Iniciando o processo
            System.out.println("Iniciando processo...");
            Process process = processBuilder.start();
            System.out.println("Processo iniciado.");

            // Capturando a saída do processo
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line); // Exibe a saída do processo, se houver
            }

            // Aguardando a execução do processo
            int exitCode = process.waitFor();
            System.out.println("Processo finalizado com código de saída: " + exitCode);

        } catch (IOException | InterruptedException e) {
            // Exibe mensagem de erro se ocorrer uma exceção durante a execução
            JOptionPane.showMessageDialog(parentFrame, "Erro ao executar WildcardEditor.exe: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); // Imprime o stack trace da exceção
        }
    }
}
