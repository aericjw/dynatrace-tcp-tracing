package dynatrace.tcp.demo;

import java.io.*;
import java.net.Socket;
import java.util.logging.*;


import com.dynatrace.oneagent.sdk.OneAgentSDKFactory;
import com.dynatrace.oneagent.sdk.api.OneAgentSDK;
import com.dynatrace.oneagent.sdk.api.OutgoingRemoteCallTracer;
import com.dynatrace.oneagent.sdk.api.enums.ChannelType;

public class TcpClient {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    // STEP 1: Create an instance of the OneAgent SDK, only one per application
    private OneAgentSDK oneAgentSDK = OneAgentSDKFactory.createInstance();
    private OutgoingRemoteCallTracer outgoingRemoteCall;
    private static final Logger logger = Logger.getLogger(TcpServer.class.getName());

    public void startConnection(String ip, int port) {
        try {
            Handler handler = new FileHandler("TcpClient.log", true);
            handler.setFormatter(new SimpleFormatter());
            logger.addHandler(handler);
            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String sendMessage(String msg) {
        try {
            // STEP 2: create a tracer for incoming calls, one is needed per remote call
            outgoingRemoteCall = oneAgentSDK.traceOutgoingRemoteCall("tcpCall", "tcpDemo", "tcp://localhost/", ChannelType.TCP_IP, "localhost:12345"); 
            // STEP 3: set protocol used for communication
            outgoingRemoteCall.setProtocolName("TCP");
            // STEP 4: start the tracer
            outgoingRemoteCall.start();
            // STEP 5: create Dynatrace tag, used for stitching transactions, send tag in the payload
            String tag = outgoingRemoteCall.getDynatraceStringTag();
            logger.info("[TcpClient Tag] " + tag);
            logger.info("[TcpClient Msg] " + msg);
            out.println(msg + "\t" + tag);
            String resp = in.readLine();
            return resp;
        }
        catch (Exception e) {
            e.printStackTrace();
            outgoingRemoteCall.error(e);
            return "ERROR";
        }
        finally {
            // STEP 6: end the tracer
            outgoingRemoteCall.end();
        }
    }

    public void stopConnection() {
        try {
            in.close();
            out.close();
            clientSocket.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
