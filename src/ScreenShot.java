import javax.swing.*;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

public class ScreenShot {

    // Método privado para obter o próximo número disponível para screenshot
    private static int getNextScreenshotNumber() {
        File screenshotsDir = new File("Screenshots");

        // Verifica se a pasta Screenshots existe e é um diretório
        if (!screenshotsDir.exists() || !screenshotsDir.isDirectory()) {
            return 0; // Se a pasta não existe ou não é um diretório, começamos com 0
        }

        // Obtém todos os arquivos de screenshot na pasta que começam com "screenshot_" e terminam com ".png"
        File[] screenshotFiles = screenshotsDir.listFiles((dir, name) -> name.startsWith("screenshot_") && name.endsWith(".png"));

        if (screenshotFiles != null && screenshotFiles.length > 0) {
            // Se encontrou arquivos de screenshot na pasta

            // Ordena os arquivos por número, extraindo o número do nome do arquivo
            Arrays.sort(screenshotFiles, (f1, f2) -> {
                int num1 = Integer.parseInt(f1.getName().replace("screenshot_", "").replace(".png", ""));
                int num2 = Integer.parseInt(f2.getName().replace("screenshot_", "").replace(".png", ""));
                return Integer.compare(num1, num2);
            });

            // Obtém o nome do último arquivo (o com o maior número)
            String lastFileName = screenshotFiles[screenshotFiles.length - 1].getName();

            // Extrai o número do último arquivo
            try {
                int lastNumber = Integer.parseInt(lastFileName.replace("screenshot_", "").replace(".png", ""));
                return lastNumber + 1; // Retorna o próximo número após o último encontrado
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return 0; // Em caso de erro, retornamos 0
            }
        } else {
            // Se não há arquivos de screenshot na pasta, começamos com 0
            return 0;
        }
    }


    // Método estático para capturar uma screenshot
    public static void captureScreenShot() {
        try {
            // Obtém o próximo número de screenshot disponível
            int screenshotNumber = getNextScreenshotNumber();

            // Define o nome do arquivo de captura usando o número obtido
            String screenshotFileName = "screenshot_" + screenshotNumber + ".png";

            // Captura a ecrã do dispositivo Android usando adb
            Process screencapProcess = Runtime.getRuntime().exec("adb shell screencap -p /sdcard/" + screenshotFileName);
            screencapProcess.waitFor();

            // Cria a pasta Screenshots se não existir
            File screenshotsDir = new File("Screenshots");
            if (!screenshotsDir.exists()) {
                screenshotsDir.mkdirs();
            }

            // Transfere a captura de ecrã do dispositivo para a pasta Screenshots local
            Process pullProcess = Runtime.getRuntime().exec("adb pull /sdcard/" + screenshotFileName + " Screenshots/");
            pullProcess.waitFor();

            // Remove a captura de ecrã do dispositivo após transferência
            Process removeProcess = Runtime.getRuntime().exec("adb shell rm /sdcard/" + screenshotFileName);
            removeProcess.waitFor();

            // Exibe mensagem de sucesso
            JOptionPane.showMessageDialog(null, "Captura de ecrã " + screenshotFileName + " salva com sucesso na pasta Screenshots!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

            // Abre a pasta Screenshots no gerenciador de arquivos
            Desktop desktop = Desktop.getDesktop();
            desktop.open(screenshotsDir);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao capturar a ecrã.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
