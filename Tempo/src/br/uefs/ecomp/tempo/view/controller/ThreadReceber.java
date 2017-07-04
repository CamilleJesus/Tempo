package br.uefs.ecomp.tempo.view.controller;

import br.uefs.ecomp.tempo.connection.Conexao;

import java.io.IOException;

import java.net.SocketTimeoutException;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Camille Jesus
 */
public class ThreadReceber implements Runnable {

    private TelaRelogioController relogio;

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
                        this.relogio.atualizaTempo(Integer.parseInt(comandos[2]), Integer.parseInt(comandos[3]));
                    }                    
                }
                System.out.println("Nome: " + conexao.getNome());
                System.out.println("Id: " + relogio.getId());
                System.out.println("Coordenador: " + conexao.getCoordenador());
            } catch (SocketTimeoutException exception) {                
                System.out.println(relogio.getId());
            } catch (IOException exception) {
                Logger.getLogger(ThreadReceber.class.getName()).log(Level.SEVERE, null, exception);
            }
        }
    }

}