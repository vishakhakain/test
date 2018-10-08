
package server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author Visakha
 */
public class ClientsHandling extends Thread{
    
        Socket socket;
        int clientNumber;

    public ClientsHandling(Socket socket, int clientNumber) {
        this.socket = socket;
            this.clientNumber = clientNumber;
            log("New connection with client# " + clientNumber + " at " + socket);
    }
    

 public void run() {
            try {

                       BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));

                       PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                System.out.println("out :"+in.readLine());
                // Send a welcome message to the client.
                System.out.println("Hello, you are client #" + clientNumber + ".");
                
                     
            } catch (IOException e) {
                log("Error handling client# " + clientNumber + ": " + e);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    log("Couldn't close a socket, what's going on?");
                }
                log("Connection with client# " + clientNumber + " closed");
            }
        }

    
        /**
         * Logs a simple message.  In this case we just write the
         * message to the server applications standard output.
         */
        private void log(String message) {
            System.out.println(message);
        }
    }
    


