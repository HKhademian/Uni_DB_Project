package app.controller;

import app.controller.cells.InvitationCellController;
import app.controller.cells.NetworkCellController;
import app.model.Invitation;
import app.model.User;
import app.repository.Repository;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class NetworkController {

    private User user;

    public NetworkController(User user){ this.user = user;}

    @FXML
    private Circle imagePlace;

    @FXML
    private TextField searchBox;

    @FXML
    private ImageView iconSearch;

    @FXML
    private JFXButton advanceSearch;

    @FXML
    private TextField Location;

    @FXML
    private TextField name;

    @FXML
    private TextField family;

    @FXML
    private ImageView home;

    @FXML
    private ImageView network;

    @FXML
    private ImageView messaging;

    @FXML
    private ImageView notification;

    @FXML
    private ImageView profile;

    @FXML
    private JFXButton logout;

    @FXML
    private JFXListView<User> networkList;

    @FXML
    private JFXListView<Invitation> inviteList;

    @FXML
    private JFXButton mutualConnection;

    private ObservableList<User> users;
    private ObservableList<Invitation> invitations;

    public void initialize(){

        name.setText(user.getFirstname());
        family.setText(user.getLastname());
        Location.setText(user.getLocation());
        setImage();

        users = FXCollections.observableArrayList(Repository.listMyNetworkProfiles(user.getUserId()));
        networkList.setItems(users);
        networkList.setCellFactory(NetworkCellController -> new NetworkCellController(true, user));

        invitations = FXCollections.observableArrayList(Repository.listUserInvitations(user.getUserId()));
        inviteList.setItems(invitations);
        inviteList.setCellFactory(InvitationCellController -> new InvitationCellController(user));

        mutualConnection.setOnAction(event -> seeMutualConnection());

        //Profile
        profile.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> profilePage());

        //Home
        home.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> homePage());

        //Notification
        notification.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> notificationPage());

        //Messaging
        messaging.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> Messaging());

        advanceSearch.setOnAction(event -> searchAdvance());
        iconSearch.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> search());

        logout.setOnAction(event -> logOut());
    }

    private void setImage() {
        if(user.getAvatar()!=null) {
            InputStream is = new ByteArrayInputStream(user.getAvatar());
            BufferedImage bf=null;
            try {
                bf = ImageIO.read(is);
            } catch (IOException e) {
                e.printStackTrace();
            }
            WritableImage wr = null;
            if (bf != null) {
                wr = new WritableImage(bf.getWidth(), bf.getHeight());
                PixelWriter pw = wr.getPixelWriter();
                for (int x = 0; x < bf.getWidth(); x++) {
                    for (int y = 0; y < bf.getHeight(); y++) {
                        pw.setArgb(x, y, bf.getRGB(x, y));
                    }
                }
            }
            imagePlace.setFill(new ImagePattern(wr));
        }
    }

    private void seeMutualConnection(){
        User user = networkList.getSelectionModel().getSelectedItem();
        if(user!=null){
            OpenWindow.openWindowWait("view/MutualConnection.fxml", new MutualConnectionController(this.user, user),
                    "Mutual Connection");
        }
    }

    private void logOut(){
        imagePlace.getScene().getWindow().hide();
        OpenWindow.openWindow("view/Login.fxml", new LoginController(), "Login");
    }

    private void profilePage(){
        imagePlace.getScene().getWindow().hide();
        OpenWindow.openWindow("view/Profile.fxml", new ProfileController(user, user),
                "Profile");
    }

    private void homePage(){
        imagePlace.getScene().getWindow().hide();
        OpenWindow.openWindow("view/Home.fxml", new HomeController(user), "Home");
    }

    private void notificationPage(){
        imagePlace.getScene().getWindow().hide();
        OpenWindow.openWindow("view/Notification.fxml", new NotificationController(user), "Notification");
    }

    private void Messaging(){
        imagePlace.getScene().getWindow().hide();
        OpenWindow.openWindow("view/Messaging.fxml", new MessagingController(user), "Messaging");
    }

    private void searchAdvance(){
        imagePlace.getScene().getWindow().hide();
        OpenWindow.openWindow("view/AdvanceSearch.fxml", new AdvanceSearchController(user), "Advance Search");
    }

    private void search(){
        String s = searchBox.getText().trim();
        if(!s.isEmpty()){
            imagePlace.getScene().getWindow().hide();
            OpenWindow.openWindow("view/SearchResult.fxml", new SearchResultController(user,
                            Repository.searchProfiles(s, null, null, null, null)),
                    "Search Result");
        }
    }
}
