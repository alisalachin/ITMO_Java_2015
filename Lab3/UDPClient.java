import java.io.*;
import java.net.*;
import java.util.concurrent.*;

class UDPClientThread extends Thread {
    int id;
    String prefix;
    int numOfIter;
    String ip;
    int port;
    public UDPClientThread(String ip, int port, int id, String prefix, int numOfIter){
        this.ip=new String(ip);
        this.port=port;
        this.id=id;
        this.prefix=new String(prefix);
        this.numOfIter=numOfIter;
    }
    public void run() {

        DatagramSocket clientSocket = null;
        try {
            clientSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        InetAddress IPAddress = null;
        try {
            IPAddress = InetAddress.getByName(this.ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        byte[] sendData = new byte[1024];
        byte[] receiveData = new byte[1024];
        //String sentence = inFromUser.readLine();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        DatagramPacket sendPacket = null;
        final DatagramPacket[] receivePacket = new DatagramPacket[1];

        for(int i=0;i<this.numOfIter;i++){
            String sentence=this.prefix+this.id+"_"+i;
            System.out.println(sentence);
            sendData = sentence.getBytes();
            sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, this.port);
            final DatagramSocket finalClientSocket = clientSocket;
            final DatagramPacket finalSendPacket = sendPacket;
            Future<String> future = executor.submit(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    try {
                        finalClientSocket.send(finalSendPacket);
                    } catch (IOException e) {
                        System.out.println("IOExc");
                    }
                    receivePacket[0] = new DatagramPacket(receiveData, receiveData.length);
                    try {
                        finalClientSocket.receive(receivePacket[0]);
                    } catch (IOException e) {
                        System.out.println("IOExc");
                    }
                    return "Ready!";
                }
            });
                String s=null;
                while(s==null) {
                    try {
                    //    System.out.println("Started..");
                       s=future.get(3, TimeUnit.SECONDS);

                    } catch (TimeoutException e) {
                        future.cancel(true);
                    } catch (InterruptedException e) {
                        System.out.println("InterrExc");
                    } catch (ExecutionException e) {
                        System.out.println("ExecrExc");
                    }
                }

            String modifiedSentence = new String(receivePacket[0].getData());
            System.out.println(modifiedSentence);
        }
        clientSocket.close();
        executor.shutdownNow();
}
}

class UDPClient
{

    public static void main(String args[]) throws Exception {
        String ip=args[0];//"localhost";
        int port=new Integer(args[1]);//9876;
        String prefix=args[2];//"pref";
        int numOfThreads=new Integer(args[3]);//3;
        int numOfIter=new Integer(args[4]);//2;


        UDPClientThread[] t = new UDPClientThread[numOfThreads];
        for (int i = 0; i < numOfThreads; i++) {
            t[i] = new UDPClientThread(ip,port, i, prefix, numOfIter);
            t[i].start();
        }
        for (int i = 0; i < numOfThreads; i++) {
            try {
                t[i].join();
            } catch (InterruptedException e) {
                System.out.println("InterrExc");
            }

        }
    }
}