import java.net.*;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;

import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;

public class Client extends JFrame{

    Socket socket;

    BufferedReader br; //br for Reading
    PrintWriter out; //out for writing

    //Declare Components
    private JLabel heading =new JLabel("Client Area");
    private JTextArea messageArea=new JTextArea();
    private JTextField messageInput=new JTextField();
    private Font font =new Font("Roboto",Font.PLAIN,20);
    
    public Client(){
        try {
            System.out.println("Sending Request to Server");
            socket =new Socket("127.0.0.1",6666);
            System.out.println("Connection Done");

            br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out=new PrintWriter(socket.getOutputStream());
            createGUI();
            handleEvents();

            startReading();
            //startWriting();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleEvents() {

        messageInput.addKeyListener(new KeyListener(){

            @Override
            public void keyTyped(KeyEvent e) {
                
            }

            @Override
            public void keyPressed(KeyEvent e) {
                
            }

            @Override
            public void keyReleased(KeyEvent e) {
                //System.out.println("Key Released "+e.getKeyCode());

                if(e.getKeyCode()==10){
                    //System.out.println("YOu have Pressed Enter key");

                    String contentToSend=messageInput.getText();
                    messageArea.append("Client :"+contentToSend+"\n");
                    out.println(contentToSend);
                    out.flush();
                    messageInput.setText("");
                    messageInput.requestFocus();
                }
                
            }
            
        });
    }

    private void createGUI(){

        this.setTitle("Client Messenger[END]");
        this.setSize(700,700);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Coding for Components
        heading.setFont(font);
        messageArea.setFont(font);
        messageInput.setFont(font);
        heading.setIcon(new ImageIcon("clogo.png"));
        heading.setHorizontalTextPosition(SwingConstants.CENTER);
        heading.setVerticalTextPosition(SwingConstants.BOTTOM);
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(15,10,15,10));
        messageArea.setEditable(false);

        // Setting the frame layout
        this.setLayout(new BorderLayout());

        //Adding the components to frame

        this.add(heading,BorderLayout.NORTH);
        JScrollPane jScrollPane=new JScrollPane(messageArea);
        this.add(jScrollPane,BorderLayout.CENTER);
        this.add(messageInput,BorderLayout.SOUTH);
        
        this.setVisible(true);
    }

    public void startReading(){
        Runnable r1=()->{

            System.out.println("Reader Started..");
            try{
            while(true){
                String msg =br.readLine();
                if(msg.equals("exit")){
                    System.out.println("Server Terminated the chat");
                    JOptionPane.showMessageDialog(this,"Server Terminated the chat");
                    messageInput.setEnabled(false);
                    socket.close();
                    break;
                }
                //System.out.println("Attacker:"+msg);
                messageArea.append("Server : " + msg+"\n");
                }
            }
            catch(Exception e){
                //e.printStackTrace();
                System.out.println("Connection is closed");
            }
        };
        new Thread(r1).start();

    }

    public void startWriting(){
        Runnable r2=()->{
            System.out.println("Writer Started");
            try{
            while(!socket.isClosed()){

                    BufferedReader br1=new BufferedReader(new InputStreamReader(System.in));
                    String content=br1.readLine();
                    out.println(content);
                    out.flush();

                    if(content.equals("exit")){
                        socket.close();
                        break;
                    }

                }
            }
            
            catch(Exception e){
                //e.printStackTrace();
                System.out.println("Connection is closed");
            }
        };
        new Thread(r2).start();
    }
    public static void main(String[] args) {

        System.out.println("This is Client..");
        new Client();
    }
}
