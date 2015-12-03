import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


class  UDPServerThread extends Thread {
    String sentence;
    DatagramPacket receivePacket;
    DatagramSocket serverSocket;
    public UDPServerThread(String sentence, DatagramPacket receivePacket, DatagramSocket serverSocket){
        this.sentence=sentence;
        this.receivePacket=receivePacket;
        this.serverSocket=serverSocket;
    }
    public void run() {
        byte[] sendData = new byte[1024];
        sentence="Hello, "+sentence;
        System.out.println(sentence);
        InetAddress IPAddress = receivePacket.getAddress();
        int port = receivePacket.getPort();
        sendData = sentence.getBytes();
        DatagramPacket sendPacket =
                new DatagramPacket(sendData, sendData.length, IPAddress, port);
        try {
            serverSocket.send(sendPacket);
        } catch (IOException e) {
            System.out.println("IOExc");
        }
    }

}
class UDPServer
{
    public static void main(String args[]) throws Exception
    {
        int port=new Integer(args[0]); //9876;
        int poolSize=new Integer(args[1]);//2;
        ExecutorService pool;
        pool = Executors.newFixedThreadPool(poolSize);
        DatagramSocket serverSocket = new DatagramSocket(port);
        byte[] receiveData = new byte[1024];

        while(true)
        {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            String sentence = new String(receivePacket.getData());
            pool.execute(new UDPServerThread(sentence, receivePacket, serverSocket));

        }
    }
}