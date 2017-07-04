package br.uefs.ecomp.tempo.view.controller;

import br.uefs.ecomp.tempo.connection.Conexao;

import java.io.IOException;

import java.net.URL;
import java.net.UnknownHostException;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;

import javafx.concurrent.Task;

import javafx.event.ActionEvent;

import javafx.fxml.Initializable;
import javafx.fxml.FXML;

import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;


/**
 * Classe controladora TelaRelogioController, responsável pela configuração dos
 * elementos da interface do relógio.
 *
 * @author Camille Jesus
 */
public class TelaRelogioController implements Initializable {

    @FXML
    private Button buttonAlterarDrift;
    @FXML
    private Text textTempo;
    @FXML
    private Button buttonAlterarTempo;
    @FXML
    private Text textH;
    @FXML
    private TextField fieldDrift;
    @FXML
    private TextField fieldAlteraMinuto;
    @FXML
    private AnchorPane paneRelogio;
    @FXML
    private Text doisPontos4;
    @FXML
    private Text doisPontos3;
    @FXML
    private TextField fieldHora;
    @FXML
    private Text doisPontos2;
    @FXML
    private Text doisPontos1;
    @FXML
    private Text textS;
    @FXML
    private TextField fieldMinuto;
    @FXML
    private Text textDrift;
    @FXML
    private TitledPane telaRelogio;
    @FXML
    private Text textMin;
    @FXML
    private TextField fieldAlteraHora;
    @FXML
    private TextField fieldSegundo;
    @FXML
    private TextField fieldAlteraSegundo;
    @FXML
    private Separator separador;
    private int drift = 1000;
    private Integer id = 0, contador = 0, segundo = 0, minuto = 0, hora = 0;
    private Conexao conexao = Conexao.getInstancia();

    public Integer getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    /** Método que libera o evento do botão de alterar tempo, que modifica o horário
     * do relógio em tempo de execução.
     * 
     * @param event 
     */    
    @FXML
    void clicaAlterarTempo(ActionEvent event) {
        String fieldh = fieldAlteraHora.getText();
        String fieldm = fieldAlteraMinuto.getText();
        String fields = fieldAlteraSegundo.getText();
        
        //Se houver tempo:
        if ((!(fieldh.equals(""))) && (!(fieldm.equals(""))) && (!(fields.equals("")))) {
            
            if ((!(fieldh.equals(" "))) && (!(fieldm.equals(" "))) && (!(fields.equals(" ")))) {
                fieldHora.setText(fieldh);
                hora = Integer.parseInt(fieldh);
                fieldMinuto.setText(fieldm);
                fieldSegundo.setText(fields);
                
                //Condição de modificação da variável contadora:
                if (fieldm.equals("0")) {
                    contador = Integer.parseInt(fields);
                } else {
                    contador = (Integer.parseInt(fieldm) * 60) + Integer.parseInt(fields);
                }
            }
        }
    }

    /** Método que libera o evento do botão de alterar drift, que modifica o drift
     * do relógio em tempo de execução.
     * 
     * @param event 
     */
    @FXML
    void clicaAlterarDrift(ActionEvent event) {
        String field = fieldDrift.getText();
        
        //Se houver drift:
        if ((!(field.equals(""))) && (!(field.equals(" "))) && (!(field.equals("0")))) {
            drift = Integer.parseInt(field);   //Modifica o valor
        }
    }
    
    /** Método inicial de carregamento da tela.
     * 
     * @param url
     * @param rb 
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        fieldHora.setText("0");
        fieldMinuto.setText("0");
        fieldSegundo.setText("0");        
        fieldAlteraHora.setText("0");
        fieldAlteraMinuto.setText("0");
        fieldAlteraSegundo.setText("0");
        fieldDrift.setText("1000");
        
        this.contagem();   //Chama a tarefa
        
        try {
            this.conexao.enviar("entrei@enviou@" + this.conexao.getNome());
        } catch (UnknownHostException ex) {
            Logger.getLogger(TelaRelogioController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TelaRelogioController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        ThreadReceber tr = new ThreadReceber(this);
        new Thread(tr).start();
    }
    
    public void contagem() {
        Task task = new Task() {   //Criação de tarefa
            
            @Override
            protected Object call() throws Exception {
                
                while (true) {   //Contagem ilimitada
                    Thread.sleep(drift);   //No caso, correspondente ao tempo de drift

                    Platform.runLater(() -> {
                        contador++;   //Variável de controle do tempo
                        segundo = contador % 60;
                        fieldSegundo.setText(segundo.toString());
                        minuto = contador / 60;
                        
                        if (minuto == 60) {
                            minuto = 0;
                        }
                        fieldMinuto.setText(minuto.toString());
                        
                        if ((hora == 23) && (contador == 3600)) {   //Final do dia, reinicia toda contagem
                            hora = 0;
                            contador = 0;
                        }
                        
                        if (contador == 3600) {   //Final da hora, incrementa a hora
                            hora++;
                            contador = 0;
                        }
                        fieldHora.setText(hora.toString());
                        
                        if (conexao.getNome().equals(conexao.getCoordenador())) {
                            
                            try {
                                conexao.enviar("enviaTempo@" + conexao.getNome() + "@" + hora + "@" + contador);
                            } catch (UnknownHostException ex) {
                                Logger.getLogger(TelaRelogioController.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (IOException ex) {
                                Logger.getLogger(TelaRelogioController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }                            
                    });
                }
            }
        };        
        new Thread(task).start();   //Inicia a tarefa
    }
    
    public void atualizaTempo(Integer hora, Integer contador) {
        this.hora = hora;
        this.contador = contador;
        segundo = contador % 60;
        fieldSegundo.setText(segundo.toString());
        minuto = contador / 60;

        if (minuto == 60) {
            minuto = 0;
        }
        fieldMinuto.setText(minuto.toString());

        if ((hora == 23) && (contador == 3600)) {   //Final do dia, reinicia toda contagem
            hora = 0;
            contador = 0;
        }

        if (contador == 3600) {   //Final da hora, incrementa a hora
            hora++;
            contador = 0;
        }
        fieldHora.setText(hora.toString());
    }
    
}