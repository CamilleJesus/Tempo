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
    private Conexao conexao;

    public ThreadReceber(TelaRelogioController relogio) {
        this.relogio = relogio;
        conexao = Conexao.getInstancia();
    }

    @Override
    public void run() {

        while (true) {

            try {
                String cmd = conexao.receber();

                String[] comandos = cmd.split("@");

                if (comandos[0].equals("mestre")) {
                    mestre(comandos);
                } else if (comandos[0].equals("enviaTempo")) {
                    sincronizar(comandos);
                } else if (comandos[0].equals("chamaEleição")) {
                    conexao.enviar("concorreEleição@" + comandos[1] + "@" + conexao.getNome() + "@" + this.relogio.getHora() + "@" + this.relogio.getContador() + "@" + this.relogio.getMili());
                } else if (comandos[0].equals("concorreEleição")) {
                    eleicao(comandos);
                } else if (comandos[0].equals("atualizaCoordenador")) {

                    atualizaCoordenador(comandos);
                }
                System.out.println("Nome: " + conexao.getNome());
                
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

    public void mestre(String[] comandos) throws UnknownHostException, IOException {
        String acao = comandos[1];
        String nome = comandos[2];

        if (acao.equals("enviou")) {

            if (!nome.equals(conexao.getNome())) {

                conexao.enviar("mestre@recebeu@" + nome + "@" + conexao.getCoordenador());
            }
        } else if (acao.equals("recebeu")) {

            String mestre = comandos[3];

            if (nome.equals(conexao.getNome())) {

                conexao.setCoordenador(mestre);

            }
        }

    }

    public void sincronizar(String[] comandos) throws InterruptedException {

        if (!comandos[1].equals(conexao.getNome())) {
            this.relogio.atualizaTempo(Integer.parseInt(comandos[2]), Integer.parseInt(comandos[3]), Integer.parseInt(comandos[4]));
        }
    }

    public void eleicao(String[] comandos) throws UnknownHostException, IOException {
        String nomeChamouEleicao = comandos[1];
        String nome = comandos[2];
       
        Integer hora = Integer.parseInt(comandos[3]);
        Integer contador = Integer.parseInt(comandos[4]);
        Integer mili = Integer.parseInt(comandos[5]);

        //if (nomeChamouEleicao.equals(conexao.getNome())) {

            if (((hora * 60 * 60 * 1000) + contador * 1000 + mili) > ((this.maiorHora * 60 * 60 * 1000) + this.maiorContador * 1000 + this.maiorMili)) {
                this.maiorHora = hora;
                this.maiorContador = contador;
                this.concorrente = nome;
                this.maiorMili = mili;
                conexao.setCoordenador(nome);

                //conexao.enviar("atualizaCoordenador@" + nomeChamouEleicao + "@" + nome);
            }
        //}
    }

    private void atualizaCoordenador(String[] comandos) {
        if (!comandos[1].equals(conexao.getNome())) {
            conexao.setCoordenador(comandos[2]);
        }
    }
}
