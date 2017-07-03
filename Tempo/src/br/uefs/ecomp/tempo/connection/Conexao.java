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
    private final String GRUPO = "225.4.5.6";    
    private MulticastSocket multicast;
    private String nome;
    private String mestre;
    
    /** Método que inicializa a classe.
     * 
     * @throws UnknownHostException
     * @throws IOException
     */
    public static void singleton() throws UnknownHostException, IOException {
        Conexao = new Conexao();
    }
    
    public void setMestre(String mestre){
        this.mestre = mestre;
    }
    
    public String getMestre(){
        return mestre;
    }
    
    /** Método que retorna a instância da classe.
     * 
     * @return Conexao
     */
    public static Conexao getInstancia() {
        return Conexao;
    }
    
    public void setNome (String nome){
        this.nome = nome;
    }
    
    public String getNome(){
        return nome;
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