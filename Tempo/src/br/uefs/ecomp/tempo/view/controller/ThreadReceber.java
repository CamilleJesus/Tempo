package br.uefs.ecomp.tempo.view.controller;

import br.uefs.ecomp.tempo.connection.Conexao;

import java.io.IOException;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Camille Jesus
 */
public class ThreadReceber implements Runnable {

    private TelaRelogioController relogio;
    private Integer maiorHora = 0, maiorContador = 0, maiorMili = 0;
    private String concorrente;

    public ThreadReceber(TelaRelogioController relogio) {
        this.relogio = relogio;
    }

    @Override
    public void run() {
        Conexao conexao = Conexao.getInstancia();

        while (true) {

            try {
                String cmd = conexao.receber();
                System.out.println(cmd);
                String[] comandos = cmd.split("@");                
                System.out.println(comandos[0]);
                
                if (comandos[0].equals("entrei")) {
                    String acao = comandos[1];
                    String nome = comandos[2];
                    
                    if (acao.equals("enviou")) {
                        System.out.println("enviou");
                        
                        if (!nome.equals(conexao.getNome())) {
                            System.out.println(nome);
                            conexao.enviar("entrei@recebeu@"+nome+"@"+relogio.getId());                           
                        }
                    } else if (acao.equals("recebeu")) {
                        System.out.println("recebeu");                        
                        int id = Integer.parseInt(comandos[3]);
                        System.out.println(id +"id");
                        
                        if (nome.equals(conexao.getNome())) {
                            System.out.println(nome);
                            
                            if (id >= relogio.getId()) {                                
                                relogio.setId(id + 1);
                                conexao.setIdCoordenador(relogio.getId());
                            }                            
                            conexao.enviar("mestre@enviou@"+conexao.getNome());
                        }
                    }                   
                    System.out.println(relogio.getId()+"final");
                }else if (comandos[0].equals("mestre")) {
                    String acao = comandos[1];
                    String nome = comandos[2];
                    
                    if (acao.equals("enviou")) {
                        System.out.println("enviou");
                        
                        if (!nome.equals(conexao.getNome())) {
                            System.out.println(nome);
                            conexao.enviar("mestre@recebeu@"+nome+"@"+relogio.getId()+"@"+conexao.getNome());                           
                        }
                    } else if (acao.equals("recebeu")) {
                        System.out.println("recebeu");                        
                        int id = Integer.parseInt(comandos[3]);
                        System.out.println(id +"id");
                        String mestre = comandos[4];
                        
                        if (nome.equals(conexao.getNome())) {
                            System.out.println(nome);
                            
                            if (id < conexao.getIdCoordenador()) {
                                conexao.setIdCoordenador(id);                                
                                conexao.setCoordenador(mestre);
                            }                            
                        }
                    }                   
                    System.out.println("Coordenador: " + conexao.getCoordenador());
                } else if (comandos[0].equals("enviaTempo")) {
                    
                    if (!comandos[1].equals(conexao.getNome())){
                        this.relogio.atualizaTempo(Integer.parseInt(comandos[2]), Integer.parseInt(comandos[3]), Integer.parseInt(comandos[4]));
                    }                    
                } else if (comandos[0].equals("chamaEleição")) {      
                    conexao.enviar("concorreEleição@" + comandos[1] + "@" + conexao.getNome() + "@" + this.relogio.getId() + "@" + this.relogio.getHora() + "@" + this.relogio.getContador() + "@" + this.relogio.getMili());
                } else if (comandos[0].equals("concorreEleição")) {
                    String nomeChamouEleicao = comandos[1];
                    String nome = comandos[2];
                    Integer id = Integer.parseInt(comandos[3]);
                    Integer hora = Integer.parseInt(comandos[4]);
                    Integer contador = Integer.parseInt(comandos[5]);
                    Integer mili = Integer.parseInt(comandos[6]);
                    
                    if (nomeChamouEleicao.equals(conexao.getNome())) {
                        
                        if (((hora * 60 * 60 * 1000) + contador*1000+mili) > ((this.maiorHora * 60* 60 * 1000) + this.maiorContador*1000+this.maiorMili)) {
                            this.maiorHora = hora;
                            this.maiorContador = contador;
                            this.concorrente = nome;
                            this.maiorMili = mili;
                            conexao.setCoordenador(nome);
                            conexao.setIdCoordenador(id);
                            conexao.enviar("atualizaCoordenador@" + nomeChamouEleicao + "@" + nome + "@" + id);
                        }
                    }
                } else if (comandos[0].equals("atualizaCoordenador")) {
                    
                    if (!comandos[1].equals(conexao.getNome())) {
                        conexao.setCoordenador(comandos[2]);
                        conexao.setIdCoordenador(Integer.parseInt(comandos[3]));
                    }
                }
                System.out.println("Nome: " + conexao.getNome());
                System.out.println("Id: " + relogio.getId());
                System.out.println("Coordenador: " + conexao.getCoordenador());
            } catch (SocketTimeoutException exception) {
                
                try {
                    conexao.enviar("chamaEleição@" + conexao.getNome());
                } catch (UnknownHostException ex) {
                    Logger.getLogger(ThreadReceber.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(ThreadReceber.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (IOException exception) {
                Logger.getLogger(ThreadReceber.class.getName()).log(Level.SEVERE, null, exception);
            } catch (InterruptedException ex) {
                Logger.getLogger(ThreadReceber.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}