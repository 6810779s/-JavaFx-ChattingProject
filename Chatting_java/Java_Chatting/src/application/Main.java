package application;
   
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class Main extends Application {
     private double xOffset = 0;
      private double yOffset = 0;
      ChatClient SocketConnect;
      ChatController chatController;
      FXMLLoader loader;
      Parent login;
      AnchorPane root;
   

      @Override
      public void init() throws Exception {
         SocketConnect = new ChatClient("127.0.0.1");
         FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/fxml_source/Chatting.fxml")); 
         login = loginLoader.load();
         chatController = loginLoader.getController();

         chatController.setSocket(SocketConnect);

         super.init();
      }

      
   @Override
   public void start(Stage primaryStage) {
      try {
          primaryStage.initStyle(StageStyle.DECORATED);
            primaryStage.setMaximized(false);
            chatController.setPrimaryStage(primaryStage);
            login.setOnMousePressed(new EventHandler<MouseEvent>() { 
               @Override
               public void handle(MouseEvent event) {
                  xOffset = event.getSceneX();
                  yOffset = event.getSceneY();
               }
            });

            login.setOnMouseDragged(new EventHandler<MouseEvent>() { 
               @Override
               public void handle(MouseEvent event) {
                  primaryStage.setX(event.getScreenX() - xOffset);
                  primaryStage.setY(event.getScreenY() - yOffset);
               }
            });

         
         Scene scene = new Scene(login);
         primaryStage.setTitle("Chatting");
         primaryStage.setScene(scene);
         primaryStage.setResizable(false);
         primaryStage.show();
         
      } catch(Exception e) {
         e.printStackTrace();
      }
   }
   

   @Override
   public void stop() throws Exception {
         super.stop();
         System.exit(0);
      }
   
   public static void main(String[] args) {
      launch(args);
   }
}