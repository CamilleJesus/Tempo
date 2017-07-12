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
    private int drift = 0;
    private Integer contador = 0, segundo = 0, minuto = 0, hora = 0;
    private Conexao conexao = Conexao.getInstancia();
    private int mili = 0;
    

    public Integer getHora() {
        return this.hora;
    }
    
    public Integer getMili() {
        return mili;
    }

    public void setHora(Integer hora) {
        this.hora = hora;
    }

    public Integer getContador() {
        return this.contador;
    }

    public void setContador(Integer contador) {
        this.contador = contador;
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
        Integer h = Integer.parseInt(fieldh);
        Integer m = Integer.parseInt(fieldm);
        Integer s = Integer.parseInt(fields);
        
        //Se houver tempo:
        if ((!(fieldh.equals(""))) && (!(fieldm.equals(""))) && (!(fields.equals("")))) {
            
            if ((!(fieldh.equals(" "))) && (!(fieldm.equals(" "))) && (!(fields.equals(" ")))) {
                
                //Se os valores estiverem dentro dos limites corretos:
                if (((h >= 0) && (h <=23)) && ((m >= 0) && (m <= 59)) && ((s >=0) && (s <= 59))) {
                    
                    //Se o valor inserido for maior que o atual:
                    if (((h * 3600) + (m * 60) + s) > ((this.hora * 3600) + (this.minuto * 60) + this.segundo)) {
                        this.fieldHora.setText(fieldh);
                        this.hora = h;
                        this.fieldMinuto.setText(fieldm);
                        this.fieldSegundo.setText(fields);

                        //Condição de modificação da variável contadora:
                        if (fieldm.equals("0")) {
                            this.contador = s;
                        } else {
                            this.contador = (m * 60) + s;
                        }
                    }
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
        String field = this.fieldDrift.getText();
            
            //Se houver drift:
            if ((!(field.equals(""))) && (!(field.equals(" "))) && (!(field.equals("0")))) {
                this.drift = Integer.parseInt(field);   //Modifica o valor
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
        fieldDrift.setText("0");
        
        this.contagem();   //Chama a tarefa
        
        try {
            this.conexao.enviar("mestre@enviou@" + conexao.getNome());
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

                    mili = 0;
                    if (!conexao.getNome().equals(conexao.getCoordenador())) {
                        if (drift >= 0){
                            Thread.sleep(drift);
                        }else{
                            mili += -drift;
                        }
                    }
                    while (mili < 1000){
                        Thread.sleep(100);   //No caso, correspondente ao tempo de drift
                        mili += 100;
                        if (conexao.getNome().equals(conexao.getCoordenador())) {
                            
                            
                            conexao.enviar("enviaTempo@" + conexao.getNome() + "@" + hora + "@" + contador+ "@" +mili);
                        }    
                        System.out.println("milisegundos: "+mili);
                    }

                    
                    Platform.runLater(() -> {
                        contador++;   //Variável de controle do tempo
                        segundo = contador % 60;
                        fieldSegundo.setText(segundo+"");
                        minuto = contador / 60;
                        
                        if (minuto == 60) {
                            minuto = 0;
                        }
                        fieldMinuto.setText(minuto+"");
                        
                        if ((hora == 23) && (contador == 3600)) {   //Final do dia, reinicia toda contagem
                            hora = 0;
                            contador = 0;
                        }
                        
                        if (contador == 3600) {   //Final da hora, incrementa a hora
                            hora++;
                            contador = 0;
                        }
                        fieldHora.setText(hora+"");
                        
                                                    
                    });
                }
            }
            
        };        
        new Thread(task).start();   //Inicia a tarefa
    }
    
    public void atualizaTempo(Integer hora, Integer contador, Integer mili) throws InterruptedException {
        int tempo1 = (this.hora * 60 *60 *1000) + this.contador*1000 + this.mili;
        int tempo2 = (hora * 60 *60 *1000) + contador *1000 + mili;
        if (tempo1 < tempo2) {
            this.mili = mili;
            this.hora = hora;
            this.contador = contador;
            this.segundo = contador % 60;
            this.minuto = contador / 60;

            if (this.minuto == 60) {
                this.minuto = 0;
            }

            if ((hora == 23) && (contador == 3600)) {   //Final do dia, reinicia toda contagem
                hora = 0;
                contador = 0;
            }

            if (contador == 3600) {   //Final da hora, incrementa a hora
                hora++;
                contador = 0;
            }
            this.fieldSegundo.setText(this.segundo.toString());
            this.fieldMinuto.setText(this.minuto.toString());
            this.fieldHora.setText(hora.toString());
        } else if(tempo1 > tempo2){
            System.err.println("Tempo1: "+tempo1);
            System.err.println("Tempo2:"+ tempo2);
            try {
                this.conexao.enviar("chamaEleição@" + this.conexao.getNome());
            } catch (UnknownHostException ex) {
                Logger.getLogger(TelaRelogioController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(TelaRelogioController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }        
    }
    
}