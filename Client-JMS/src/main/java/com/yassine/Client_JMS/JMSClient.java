package com.yassine.Client_JMS;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.print.Collation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class JMSClient extends Application{

	private Session session;
	private MessageProducer messageProducer;
	private MessageConsumer messageConsumer;
	private ObservableList <String> list;
	
	public static void main(String[] args){
		launch(args);
	}

	@Override
	public void start(Stage primaryStage){
		initialization();
		primaryStage.setTitle("JMS Client");
		VBox vBox = new VBox();
		GridPane gridPane = new GridPane();
		gridPane.setVgap(10);
		gridPane.setHgap(10);
		gridPane.setPadding(new Insets(10));
		Label lableFirstName = new Label("First Name: ");
		TextField textFieldFN = new TextField();
		Label lableLN = new Label("Last Name: ");
		TextField textFieldLN = new TextField();
		Label lableEmail = new Label("Email: ");
		TextField textFieldEmail = new TextField();
		Button saveButton = new Button("Save");
		gridPane.add(lableFirstName, 0, 0);
		gridPane.add(textFieldFN, 1, 0);
		gridPane.add(lableLN, 0, 1);
		gridPane.add(textFieldLN, 1, 1);
		gridPane.add(lableEmail, 0, 2);
		gridPane.add(textFieldEmail, 1, 2);
		gridPane.add(saveButton, 0, 3);
		vBox.getChildren().add(gridPane);
		vBox.setPadding(new Insets(10));
		vBox.setSpacing(10);
		list = FXCollections.observableArrayList();
		ListView<String> listView = new ListView<>(list);
		vBox.getChildren().add(listView);
		Scene scene = new Scene(vBox, 800, 600);
		primaryStage.setScene(scene);
		primaryStage.show();
		
		saveButton.setOnAction(e->{
			try {
				String fn = textFieldFN.getText();
				String ln = textFieldLN.getText();
				String email = textFieldEmail.getText();
				TextMessage msg =session.createTextMessage();
				msg.setText(fn+"_"+ln+"_"+email);
				messageProducer.send(msg);
			} catch (JMSException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		});
	}
	
	public void initialization() {
		try {
			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
			Connection connection = connectionFactory.createConnection();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			connection.start();
			Destination  destination = session.createQueue("studentQueue.queue");
			messageProducer =session.createProducer(destination);
			Destination  destination2 = session.createQueue("school.resp");
			messageConsumer = session.createConsumer(destination2);
			messageConsumer.setMessageListener(new MessageListener() {
				
				@Override
				public void onMessage(Message message) {
					if (message instanceof TextMessage) {
						try {
							String data = ((TextMessage) message).getText();
							list.add(data);
						} catch (JMSException e) {
							e.printStackTrace();
						}
					}
					
				}
			});
			
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
