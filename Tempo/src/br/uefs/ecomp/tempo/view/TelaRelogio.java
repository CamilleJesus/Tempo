package br.uefs.ecomp.tempo.view;

import br.uefs.ecomp.tempo.connection.Conexao;

import java.io.IOException;
import java.net.InetAddress;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.event.EventHandler;

import javafx.fxml.FXMLLoader;

import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.swing.JOptionPane;


/**
 * Classe  principal TelaRelogio, responsável pela inicialização da interface do
 * relógio.
 *
 * @author Camille Jesus
 */
public class TelaRelogio extends Application {
    private static String id;
    
    /** Método que carrega a tela e inicializa a cena (frame).
     * 
     * @param stage
     * @throws java.lang.Exception
     */
    @Override
    public void start(Stage stage) throws Exception {
        
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            
            @Override
            public void handle(WindowEvent event) {
                Platform.exit();
                System.exit(0);
            }
        });
        //Chama o arquivo FXML correpondente:
        Parent root = FXMLLoader.load(getClass().getResource("TelaRelogio.fxml"));      
        Scene scene = new Scene(root);
        stage.setTitle("Tempo em SD" + " <" + id +  "> ");   //Renomeia o frame
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
            //por setIpMulticast
            //id = InetAddress.getLocalHost().getHostAddress();
            id = JOptionPane.showInputDialog(null, "Informe nome: ");
            Conexao conexao = Conexao.getInstancia();
            conexao.conectar();   //Conecta
            conexao.setNome(id);
            conexao.setCoordenador(id);     
            launch(args);   //Inicia a aplicação
        }      
    }
    
}