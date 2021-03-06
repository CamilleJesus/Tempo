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
    private Conexao conexao;

    public ThreadReceber(TelaRelogioController relogio) {
        this.relogio = relogio;
        this.conexao = Conexao.getInstancia();
    }

    @Override
    public void run() {

        while (true) {

            try {
                String[] comandos = (conexao.receber()).split("@");

                switch (comandos[0]) {
                    case "coordenador":
                        atualizaCoordenador(comandos);
                        break;
                    case "enviaTempo":
                        sincronizar(comandos);
                        break;
                    case "chamaEleicao":
                        conexao.enviar("concorreEleicao@" + conexao.getNome() + "@" + this.relogio.getHora() + "@" + this.relogio.getContador() + "@" + this.relogio.getMili());
                        break;
                    case "concorreEleicao":
                        eleicao(comandos);
                        break;
                    default:
                        break;
                }
                System.out.println("Nome: " + conexao.getNome());                
                System.out.println("Coordenador: " + conexao.getCoordenador());
            } catch (SocketTimeoutException exception) {

                try {
                    conexao.enviar("chamaEleicao@" + conexao.getNome());
                } catch (UnknownHostException ex) {
                    Logger.getLogger(ThreadReceber.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(ThreadReceber.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (IOException | InterruptedException exception) {
                Logger.getLogger(ThreadReceber.class.getName()).log(Level.SEVERE, null, exception);
            }
        }
    }

    public void atualizaCoordenador(String[] comandos) throws UnknownHostException, IOException {
        String acao = comandos[1];
        String nome = comandos[2];

        if (acao.equals("enviou")) {

            if (!nome.equals(conexao.getNome())) {
                conexao.enviar("coordenador@recebeu@" + nome + "@" + conexao.getCoordenador());
            }
        } else if (acao.equals("recebeu")) {

            if (nome.equals(conexao.getNome())) {
                conexao.setCoordenador(comandos[3]);
            }
        }
    }

    public void sincronizar(String[] comandos) throws InterruptedException {

        if (!comandos[1].equals(conexao.getNome())) {
            this.relogio.atualizaTempo(Integer.parseInt(comandos[2]), Integer.parseInt(comandos[3]), Integer.parseInt(comandos[4]));
        }
    }

    public void eleicao(String[] comandos) throws UnknownHostException, IOException {
        Integer hora = Integer.parseInt(comandos[2]);
        Integer contador = Integer.parseInt(comandos[3]);
        Integer mili = Integer.parseInt(comandos[4]);

            if (((hora * 3600 * 1000) + (contador * 1000) + mili) > ((this.maiorHora * 3600 * 1000) + (this.maiorContador) * 1000 + this.maiorMili)) {
                this.maiorHora = hora;
                this.maiorContador = contador;
                this.maiorMili = mili;
                this.conexao.setCoordenador(comandos[1]);
            }
    }
    
}