package br.uefs.ecomp.tempo.connection;

import java.io.IOException;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;


/**
 *
 * @author felipe
 */
public class Conexao {
    
    private static Conexao Conexao;
    private final int PORTA = 5000;
    private final String GRUPO = "225.4.5.9";    
    private MulticastSocket multicast;
    private String nome;
    private String coordenador;
    private int idCoordenador = 0;
    
    /** Método que inicializa a classe.
     * 
     * @throws UnknownHostException
     * @throws IOException
     */
    public static void singleton() throws UnknownHostException, IOException {
        Conexao = new Conexao();
    }    
    
    /** Método que retorna a instância da classe.
     * 
     * @return Conexao
     */
    public static Conexao getInstancia() {
        return Conexao;
    }
        
    /** Método que retorna o nome do host.
     * 
     * @return nome
     */
    
    public String getNome(){
        return nome;
    }
    
    /** Método que altera o nome do host.
     * 
     * @param nome 
     */
    public void setNome (String nome){
        this.nome = nome;
    }
    
    /** Método que retorna o coordenador do grupo.
     * 
     * @return coordenador
     */
    public String getCoordenador() {
        return this.coordenador;
    }
    
    /** Método que altera o coordenador do grupo.
     * 
     * @param coordenador 
     */
    public void setCoordenador(String coordenador) {
        this.coordenador = coordenador;
    }
    
    /** Método que retorna o id do coordenador do grupo.
     * 
     * @return idCoordenador
     */
    public int getIdCoordenador() {
        return this.idCoordenador;
    }
    
    /** Método que altera o id do coordenador do grupo.
     * 
     * @param idCoordenador 
     */
    public void setIdCoordenador(int idCoordenador) {
        this.idCoordenador = idCoordenador;
    }
    
    /** Método que conecta ao grupo.
     * 
     * @throws IOException 
     */
    public void conectar() throws IOException {     
        this.multicast = new MulticastSocket(this.PORTA);
        this.multicast.joinGroup(InetAddress.getByName(this.GRUPO));
        this.multicast.setSoTimeout(2000);
    }
    
    /** Método que desconecta do grupo.
     * 
     * @throws IOException 
     */
    public void desconectar() throws IOException {
        this.multicast.leaveGroup(InetAddress.getByName(this.GRUPO));
        this.multicast.close();
    }
    
    /** Método que envia uma string para o grupo.
     * 
     * @param s
     * 
     * @throws java.net.SocketException
     * @throws java.net.UnknownHostException
     * @throws java.io.IOException
     */
    public void enviar(String s) throws SocketException, UnknownHostException, IOException {
        DatagramSocket socket = new DatagramSocket();       
        byte[] buf = s.getBytes();   
        socket.send(new DatagramPacket(buf, buf.length, InetAddress.getByName(this.GRUPO), this.PORTA));
        socket.close();  
    }
    
    /** Método que recebe uma string do grupo e a retorna.
     * 
     * @throws java.io.IOException
     * 
     * @return String
     * @throws java.net.SocketTimeoutException
     */
    public String receber() throws IOException, SocketTimeoutException {
        byte[] buf = new byte[256];
        DatagramPacket pack = new DatagramPacket(buf, buf.length);
        this.multicast.receive(pack);        
        return  (new String(pack.getData()).trim());
    }
    
}