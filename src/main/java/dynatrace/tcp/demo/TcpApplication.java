package dynatrace.tcp.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.*;

@SpringBootApplication
@RestController
public class TcpApplication {
    private static final Logger logger = Logger.getLogger(TcpServer.class.getName());

    public static void main(String[] args) {
    	try {
            // startup SpringBoot Application
            SpringApplication.run(TcpApplication.class, args); 

            // startup TCP Server on port 12345
            TcpServer tcpServer = new TcpServer();
            tcpServer.start(12345);

            // logging config
            Handler handler = new FileHandler("TcpApplication.log", true);
            handler.setFormatter(new SimpleFormatter());
            logger.addHandler(handler);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    @GetMapping("/tcp") //http request to trigger tcp request to tomcat tcp server
	public String tcpClient() {
        logger.info("Sending message to 127.0.0.1:12345");
        // startup TCP Client
        TcpClient tcpClient = new TcpClient();
        tcpClient.startConnection("127.0.0.1", 12345);

        // send two messages, Testing! and .
        // . will terminate the connection
        String resp = tcpClient.sendMessage("Testing!");
        tcpClient.sendMessage(".");
        tcpClient.stopConnection();
        return resp;
    }
}