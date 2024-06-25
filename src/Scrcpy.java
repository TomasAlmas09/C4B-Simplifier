import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class Scrcpy {
    private static JFrame parentFrame; // Janela pai para exibir mensagens

    public Scrcpy(JFrame parentFrame) {
        this.parentFrame = parentFrame;
    }

    // Método estático para executar o scrcpy via adb
    public static void executeAdbSrcSpy() {

        // Caminho completo para o executável scrcpy.exe
        String exePath = "packages/scrcpy/scrcpy.exe";
        System.out.println("Caminho completo do executável: " + exePath);

        // Verifica se o arquivo existe
        File exeFile = new File(exePath);
        if (!exeFile.exists()) {
            // Exibe mensagem de erro se o arquivo não for encontrado
            JOptionPane.showMessageDialog(parentFrame, "O arquivo scrcpy.exe não foi encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Construindo o comando para executar o arquivo
        ProcessBuilder processBuilder = new ProcessBuilder(exePath);

        try {
            // Iniciando o processo
            Process process = processBuilder.start();

            // Aguardando a execução do processo
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                // Exibe mensagem de sucesso se o processo terminar com código 0
                JOptionPane.showMessageDialog(parentFrame, "scrcpy.exe foi executado com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Exibe mensagem de erro se o processo terminar com código diferente de 0
                JOptionPane.showMessageDialog(parentFrame, "Falha ao executar scrcpy.exe dispositivo não suportado.", "Erro", JOptionPane.ERROR_MESSAGE);
            }

        } catch (IOException | InterruptedException e) {
            // Exibe mensagem de erro se ocorrer uma exceção durante a execução
            JOptionPane.showMessageDialog(parentFrame, "Erro ao executar scrcpy.exe: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
