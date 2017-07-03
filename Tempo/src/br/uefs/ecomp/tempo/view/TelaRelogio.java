package br.uefs.ecomp.tempo.view;

import br.uefs.ecomp.tempo.connection.Conexao;

import java.io.IOException;

import javafx.application.Application;
import static javafx.application.Application.launch;

import javafx.fxml.FXMLLoader;

import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Stage;

import javax.swing.JOptionPane;


/**
 * Classe  principal TelaRelogio, responsável pela inicialização da interface do
 * relógio.
 *
 * @author Camille Jesus
 */
public class TelaRelogio extends Application {
    
    /** Método que carrega a tela e inicializa a cena (frame).
     * 
     * @param stage
     * @throws java.lang.Exception
     */
    @Override
    public void start(Stage stage) throws Exception {
        //Chama o arquivo FXML correpondente:
        Parent root = FXMLLoader.load(getClass().getResource("TelaRelogio.fxml"));      
        Scene scene = new Scene(root);
        stage.setTitle("Tempo em SD");   //Renomeia o frame
        stage.setScene(scene);
        stage.show();
    }

    /** Método que inicia o programa.
     * 
     * @param args the command line arguments
     * 
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        Conexao.singleton();   //Cria a conexão
        
        if (Conexao.getInstancia() != null) {
            String id = JOptionPane.showInputDialog(null, "Informe nome: ");
            Conexao conexao = Conexao.getInstancia();
            conexao.conectar();   //Conecta
            conexao.setNome(id);
            conexao.setMestre(id);            
            launch(args);   //Inicia a aplicação
        }      
    }
    
}