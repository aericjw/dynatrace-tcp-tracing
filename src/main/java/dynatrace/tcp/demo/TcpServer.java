package dynatrace.tcp.demo;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.*;


import com.dynatrace.oneagent.sdk.OneAgentSDKFactory;
import com.dynatrace.oneagent.sdk.api.IncomingRemoteCallTracer;
import com.dynatrace.oneagent.sdk.api.OneAgentSDK;

public class TcpServer {
    private ServerSocket serverSocket;
    // STEP 1: Create an instance of the OneAgent SDK, only one per application
    private static OneAgentSDK oneAgentSDK = OneAgentSDKFactory.createInstance();
    private static final Logger logger = Logger.getLogger(TcpServer.class.getName());

    public void start(int port) {
        try {
            Handler handler = new FileHandler("TcpServer.log", true);
            handler.setFormatter(new SimpleFormatter());
            logger.addHandler(handler);
            logger.info("Starting up server");
            serverSocket = new ServerSocket(port);
            while (true) {
                new ClientHandler(serverSocket.accept()).start();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            serverSocket.close();
            logger.info("Stopping server");
        }
        catch (Exception e) {
            System.err.println(e);
        }
    }

    private static class ClientHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        // STEP 2: create a tracer for incoming calls, one is needed per remote call
        private IncomingRemoteCallTracer incomingRemoteCall; 

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                logger.info("Incoming remote call");
                String inputLine;
                while ((inputLine = in.readLine()) != null){
                    int dynatraceTagIndex = inputLine.indexOf("\t") + 1;
                    // STEP 2 continued: create a tracer for incoming calls, one is needed per remote call
                    incomingRemoteCall = oneAgentSDK.traceIncomingRemoteCall("tcpCall", "tcpDemo", "tcp://localhost/");
                    // STEP 3: Extract/process the Dynatrace tag from the payload, this is for stitching
                    incomingRemoteCall.setDynatraceStringTag(inputLine.substring(dynatraceTagIndex)); 
                    // STEP 4: start the tracer, used for timing
                    incomingRemoteCall.start(); 
                    // STEP 5: set protocol used
                    incomingRemoteCall.setProtocolName("TCP"); 
                    logger.info("[TcpServer Tag] " + inputLine.substring(dynatraceTagIndex));
                    logger.info("[TcpServer Msg] " + inputLine.substring(0, dynatraceTagIndex));
                    if (".".equals(inputLine)) {
                        out.println("Ending Chat.");
                        logger.info("Ending Chat.");
                        break;
                    }
                    out.println(inputLine.substring(0, dynatraceTagIndex));
                    // STEP 6: stop the tracer
                    incomingRemoteCall.end(); 
                }

                in.close();
                out.close();
                clientSocket.close();
            }
            catch (Exception e) {
                e.printStackTrace();
                incomingRemoteCall.error(e);
            }
            finally {
                // STEP 6 continued: stop the tracer, this one is for termination
                incomingRemoteCall.end(); 
            }
        }
    }
}
