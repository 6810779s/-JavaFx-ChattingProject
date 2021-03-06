package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ChatController implements Initializable{
   @FXML
   private Label username,number;
   @FXML
   private TextField username2;
   @FXML
   private TextArea textArea,textField;
   @FXML
   private Button Login,send;
   @FXML
   private ChoiceBox choiceBox;
   @FXML
   private CheckBox checkBox;
   
   private BufferedReader inMsg=null;
   private PrintWriter outMsg = null;
   private CheckBox whisper;
   private String name;
   private Stage primaryStage;
   private AnchorPane root;
   private int logout = 0;
   private int num;

   ChatClient SocketConnect;
   
   @Override
   public void initialize(URL arg0, ResourceBundle arg1) {

	 
	   Platform.runLater(()->{ 
	   checkBox.setDisable(true);
	  choiceBox.setVisible(false);
      textField.setEditable(false);
      textArea.setVisible(false);
      send.setDisable(true);
      textArea.setEditable(false);
      //label.setVisible(false);
      username2.setVisible(true);
      });
   }
   
   public void LoginHandle() {
      
	   
      name = username2.getText().toString();
      if(name.equals("")) {
          
         Alert alert = new Alert(AlertType.WARNING);
         alert.setTitle("Warning");
         alert.setHeaderText("????????? ?????????");
         alert.setContentText("???????????? ???????????? ???????????? ?????????.");
         alert.showAndWait();
          
          
      }else {
         if(logout==0) {
        	 SocketConnect.getOutMsg().println("Login/"+name);
        
         }else {
            change_Logout();
            logout = 0;
            textArea.setVisible(false);
            SocketConnect.getOutMsg().println("Logout/"+name);
            
         }
      }
   }
   
   public void change_Login() {
	   
       Platform.runLater(()->{
    	   checkBox.setDisable(false);
    	   choiceBox.setVisible(true);
            username2.setVisible(false);
            username.setText(name);
            Login.setText("Logout");
            choiceBox.setValue("");
            logout = 1;
            textArea.setText("");
            textArea.setVisible(true);
            textField.setEditable(true); //?????? ?????? ??? 
            send.setDisable(false); //????????????
            
             
       });    
   }
   public void change_Logout() {
      
      Platform.runLater(()->{
    	  checkBox.setDisable(true);
    	  	number.setText("0");
    	  	choiceBox.setVisible(false);
            textField.setEditable(false);
            send.setDisable(true);
            username.setText("");
            username2.setVisible(true);
            username2.setText("");
            Login.setText("Login");
         });
   }

   public void SendHandle() {
	   String to_user = choiceBox.getValue().toString(); 
	   if(checkBox.isSelected()==true) {
		   if(to_user.equals("") || to_user.equals(name)) {
			   Platform.runLater(()->{//wrapText="true" textarea ?????????.
				   textArea.appendText("\n\n<????????? ?????????>\n*1. ?????? ?????? ?????? ???????????? ?????? ?????? ?????? ??????"
					   		+ " ????????? ????????? ???????????????."
							   +"\n2. ????????? ??????????????? ????????? ?????????."
							   +"\n3. ????????? ????????? ?????????, ?????? ?????????????????? ?????? "
							   + "????????? ????????????.\n");
			   });
			  
				   
		   }else {
			   SocketConnect.getOutMsg().println("Whisper/"+name+"/"+to_user+"/"+textField.getText().toString());
		   }
	   }else {
			SocketConnect.getOutMsg().println(name+"/"+textField.getText().toString());	   
	   }
	   Platform.runLater(()->{
		   textField.setText("");
		   textField.requestFocus();
	   });
   }

   
   public void setSocket(ChatClient SocketConnect) { //?????? ??????
         this.SocketConnect = SocketConnect;
         getMsg();
      }
    public void setPrimaryStage(Stage primaryStage) {
         this.primaryStage = primaryStage;

      }
     public void setRoot(AnchorPane root) {
         this.root = root;
      }

   
   public void getMsg() {
       Thread thread = new Thread() {
            @Override
            public void run() {
            	String[] list = null;
               String[] rmsg = null;
               String msg = null;
               while (true) {
                  try {
                     msg =  SocketConnect.getInMsg().readLine(); // ???????????? ?????? ???????????? ??????.
                     System.out.println("Log");
                  } catch (IOException e1) {
                     System.out.println("????????????");
                  }
                  if (msg != null) {
                     System.out.println(msg+"client");
                     rmsg = msg.split("/");
                     if (rmsg[0].equals("Login")) {
                    	 String user=rmsg[1];
                    	 change_Login();
                    	 Platform.runLater(()->{
                    		 textArea.appendText("Server>"+ user +"?????? ????????? ???????????????.\n");
                    	 });
                    	 
                           
                          
                        
                     }else if(rmsg[0].equals("NoLogin")) {
                    	 System.out.println("2");
                    	 Platform.runLater(()->{
                    		 Alert alert = new Alert(AlertType.WARNING);
                             alert.setTitle("Warning");
                             alert.setHeaderText("????????? ??????");
                             alert.setContentText("????????? ???????????? ???????????? ????????? ?????????.");
                             alert.showAndWait();
                    	 });
                    	
						}else if(rmsg[0].equals("Logout")) {
                    	 String user=rmsg[1];
                    	 Platform.runLater(()->{
                    		 textArea.appendText("Server>"+ user +"?????? ???????????? ???????????????.\n");
                         });
                        
                     }
                     else if(rmsg[0].equals("Client_number")) {
                    	 num=Integer.parseInt(rmsg[1]);
                    	 Platform.runLater(()->{
                    		 number.setText(""+num);
                    	 });
                    	 
                     }
                     else if(rmsg[0].equals("Client_List")) {
                    	 choiceBox.getItems().clear();
                    	 String tms= rmsg[1].substring(0,rmsg[1].length()-1);
                    	 list = tms.split("#");
                    	 System.out.println(tms);
                    	 System.out.println(num);
                    		 choiceBox.getItems().add("");
                    		 for(int i=0;i<num;i++) {
                        		 choiceBox.getItems().add(list[i]);
                        	 }

                    	 
                     }else if(rmsg[0].equals("Whisper")) {
                    	 String user_From = rmsg[1];
                    	 String user_To = rmsg[2];
                    	 String tmsg = rmsg[3];
                    	 
                    	 Platform.runLater(()->{
                    		 textArea.appendText(user_From+">"+user_To+ ":"+tmsg+"\n");
                    	 });
                    	
                     }else if(rmsg[0].equals("????????? ??????")) {
                    	 String user=rmsg[1];
                    	 String tmsg=rmsg[2];
                    	 Platform.runLater(()->{
                    		 textArea.appendText(user+">"+tmsg.replace("??????", "**").replace("?????????", "***")+"\n");
                    	 });
                    	 
                     }
                     else {
                    	 String user = rmsg[0];
                    	 String tmsg = rmsg[1];
                    	 Platform.runLater(()->{
                    		 textArea.appendText(user+">"+tmsg+"\n");
                    	 });
                        
                       
                     }
                     
                     msg=null;
                     
                        }
                  }
               }
               
            };
            thread.start();
      }
            
   

}