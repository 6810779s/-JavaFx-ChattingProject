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
         alert.setHeaderText("닉네임 미입력");
         alert.setContentText("채팅에서 사용하실 닉네임을 쓰시오.");
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
            textField.setEditable(true); //채팅 치는 곳 
            send.setDisable(false); //전송버튼
            
             
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
			   Platform.runLater(()->{//wrapText="true" textarea 줄바꿈.
				   textArea.appendText("\n\n<귓속말 이용법>\n*1. 접속 인원 옆의 화살표를 눌러 자신 외의 다른"
					   		+ " 접속자 이름을 선택하세요."
							   +"\n2. 귓속말 체크박스에 체크를 하세요."
							   +"\n3. 원하는 내용을 적으면, 해당 접속자에게만 문자 "
							   + "내용이 보입니다.\n");
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

   
   public void setSocket(ChatClient SocketConnect) { //소켓 설정
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
                     msg =  SocketConnect.getInMsg().readLine(); // 서버에서 보낸 메시지를 읽음.
                     System.out.println("Log");
                  } catch (IOException e1) {
                     System.out.println("소켓에러");
                  }
                  if (msg != null) {
                     System.out.println(msg+"client");
                     rmsg = msg.split("/");
                     if (rmsg[0].equals("Login")) {
                    	 String user=rmsg[1];
                    	 change_Login();
                    	 Platform.runLater(()->{
                    		 textArea.appendText("Server>"+ user +"님이 로그인 하셨습니다.\n");
                    	 });
                    	 
                           
                          
                        
                     }else if(rmsg[0].equals("NoLogin")) {
                    	 System.out.println("2");
                    	 Platform.runLater(()->{
                    		 Alert alert = new Alert(AlertType.WARNING);
                             alert.setTitle("Warning");
                             alert.setHeaderText("닉네임 중복");
                             alert.setContentText("접속자 리스트에 존재하는 닉네임 입니다.");
                             alert.showAndWait();
                    	 });
                    	
						}else if(rmsg[0].equals("Logout")) {
                    	 String user=rmsg[1];
                    	 Platform.runLater(()->{
                    		 textArea.appendText("Server>"+ user +"님이 로그아웃 하셨습니다.\n");
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
                    	
                     }else if(rmsg[0].equals("비속어 방지")) {
                    	 String user=rmsg[1];
                    	 String tmsg=rmsg[2];
                    	 Platform.runLater(()->{
                    		 textArea.appendText(user+">"+tmsg.replace("바보", "**").replace("멍청이", "***")+"\n");
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