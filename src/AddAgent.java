import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class AddAgent {

    private static final Logger logger = Logger.getLogger(AddAgent.class.getName());
    private static Map<String, Map<String, String>> databaseOptions;

    public static void main(String[] args) {
        logger.info("Aplicação iniciada");

        // Carregar as conexões de banco de dados de um arquivo JSON
        databaseOptions = loadDatabaseOptions();

        if (databaseOptions == null) {
            JOptionPane.showMessageDialog(null, "Erro ao carregar opções de banco de dados.");
            return;
        }

        // Criar a interface gráfica para selecionar o banco de dados
        JFrame selectDbFrame = new JFrame("Selecionar Banco de Dados");
        JComboBox<String> dbSelectBox = new JComboBox<>(databaseOptions.keySet().toArray(new String[0]));
        JButton selectButton = new JButton("Selecionar");

        selectDbFrame.setLayout(new java.awt.FlowLayout());
        selectDbFrame.add(new JLabel("Escolha a base de dados:"));
        selectDbFrame.add(dbSelectBox);
        selectDbFrame.add(selectButton);

        // Configurar a ação do botão
        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedDb = (String) dbSelectBox.getSelectedItem();
                if (selectedDb != null && !selectedDb.isEmpty()) {
                    logger.info("Banco de dados selecionado: " + selectedDb);
                    // Abrir o formulário de inserção de dados passando as credenciais selecionadas
                    Map<String, String> dbCredentials = databaseOptions.get(selectedDb);
                    openInsertForm(dbCredentials);
                    selectDbFrame.dispose();
                }
            }
        });

        selectDbFrame.setSize(300, 100);
        selectDbFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        selectDbFrame.setVisible(true);
    }

    private static void openInsertForm(Map<String, String> dbCredentials) {
        JFrame frame = new JFrame("Adicionar Usuário");
        JLabel labelName = new JLabel("Nome:");
        JLabel labelLogin = new JLabel("Login (4 dígitos):");
        JLabel labelPassword = new JLabel("Senha (4 dígitos):");
        JTextField fieldName = new JTextField(20);
        JTextField fieldLogin = new JTextField(20);
        JTextField fieldPassword = new JTextField(20);
        JButton insertButton = new JButton("Inserir Registro");

        frame.setLayout(new java.awt.FlowLayout());
        frame.add(labelName);
        frame.add(fieldName);
        frame.add(labelLogin);
        frame.add(fieldLogin);
        frame.add(labelPassword);
        frame.add(fieldPassword);
        frame.add(insertButton);

        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userName = fieldName.getText();
                String userLogin = fieldLogin.getText();
                String userPassword = fieldPassword.getText();

                if (!isValidLoginAndPassword(userLogin, userPassword)) {
                    JOptionPane.showMessageDialog(null, "Login e Senha devem ter exatamente 4 dígitos e ser compostos apenas por números.");
                    return;
                }

                int employeeNr = Integer.parseInt(userLogin);

                // Inserir dados no banco de dados usando as credenciais selecionadas
                insertData(userName, userLogin, userPassword, employeeNr, dbCredentials);
            }
        });

        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private static boolean isValidLoginAndPassword(String login, String password) {
        return login.matches("\\d{4}") && password.matches("\\d{4}");
    }

    private static void insertData(String userName, String userLogin, String userPassword, int employeeNr, Map<String, String> dbCredentials) {
        String url = dbCredentials.get("url");
        String user = dbCredentials.get("user");
        String password = dbCredentials.get("password");

        String sql = "INSERT INTO [dbo].[users] "
                + "([user_name], [user_password], [active], [card_serial_nr], [biometric_info], "
                + "[user_login], [user_info], [employee_nr], [e_mail], [failed_login_attempts], "
                + "[exported], [wildcard_managed], [fiscal_nr]) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        logger.info("Iniciando inserção de dados no banco de dados");

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userName);
            pstmt.setString(2, userPassword);
            pstmt.setInt(3, 1);
            pstmt.setNull(4, java.sql.Types.VARCHAR);
            pstmt.setNull(5, java.sql.Types.VARCHAR);
            pstmt.setString(6, userLogin);
            pstmt.setNull(7, java.sql.Types.VARCHAR);
            pstmt.setInt(8, employeeNr);
            pstmt.setNull(9, java.sql.Types.VARCHAR);
            pstmt.setInt(10, 0);
            pstmt.setInt(11, 0);
            pstmt.setInt(12, 0);
            pstmt.setNull(13, java.sql.Types.VARCHAR);

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                logger.info("Registro inserido com sucesso.");
                if (rowsInserted > 0) {
                    logger.info("Registro inserido com sucesso.");

                    // Criar um JLabel para a mensagem
                    JLabel messageLabel = new JLabel("<html><div style='text-align: center;'>Registro inserido com sucesso!<br>!! ÁS 00:00 SERÁ GERADO UM NOVO FICHEIRO DE AGENTES OU CONSULTE UM ADMINSTRADOR !!</div></html>");

                    // Criar um JOptionPane para mostrar a mensagem centralizada
                    JOptionPane.showMessageDialog(null, messageLabel);
                }

            }

        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Erro ao inserir o registro", ex);
            JOptionPane.showMessageDialog(null, "Erro ao inserir o registro: " + ex.getMessage());
        }
    }

    // Método para carregar as opções de conexão de banco de dados de um arquivo JSON
    private static Map<String, Map<String, String>> loadDatabaseOptions() {
        JSONParser parser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) parser.parse(new FileReader("resources/databases.json"));
            return (Map<String, Map<String, String>>) jsonObject.get("databases");
        } catch (IOException | ParseException ex) {
            logger.log(Level.SEVERE, "Erro ao carregar opções de banco de dados", ex);
            return null;
        }
    }
}
