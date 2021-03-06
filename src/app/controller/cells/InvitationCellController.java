package app.controller.cells;

import app.controller.OpenWindow;
import app.controller.ProfileController;
import app.model.Invitation;
import app.model.User;
import app.repository.Repository;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListCell;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class InvitationCellController extends JFXListCell<Invitation> {

    private User owner;

    public InvitationCellController(User owner) {this.owner = owner;}

    @FXML
    private AnchorPane rootAnchorPane;

    @FXML
    private Circle imagePlace;

    @FXML
    private Label name;

    @FXML
    private Label fromOrTo;

    @FXML
    private JFXButton acceptButton;

    @FXML
    private JFXButton rejectButton;

    @FXML
    private Label response;

    @FXML
    private TextArea message;

    @FXML
    private Label time;

    private FXMLLoader fxmlLoader;

    @FXML
    public void initialize() {

    }

    public void updateItem(Invitation invitation, boolean empty){
        super.updateItem(invitation, empty);

        if(empty || invitation == null){
            setText(null);
            setGraphic(null);
        }else{
            if(fxmlLoader == null){
                fxmlLoader = new FXMLLoader(getClass().getResource("../../../view/cells/InvitationCell.fxml"));
                fxmlLoader.setController(this);
                try {
                    fxmlLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            User sender = Repository.getUserById(invitation.getSenderUserId(), invitation.getSenderUserId());
            User receiver = Repository.getUserById(invitation.getReceiverUserId(), invitation.getReceiverUserId());
            message.setText(invitation.getMessage());
            time.setText(invitation.getTime().toString());

            if(sender.getUserId() == owner.getUserId()){
                setVisibleButton(false);
                fromOrTo.setText("to : ");
                name.setText(receiver.getFirstname() + " " + receiver.getLastname());
                setImage(receiver);
            }else{
                fromOrTo.setText("from : ");
                name.setText(sender.getFirstname() + " " + sender.getLastname());
                setImage(sender);
            }
            if(invitation.getStatus() == 1){
                response.setText("Accepted");
                setVisibleButton(false);
            }
            else if(invitation.getStatus() == -1){
                response.setText("Rejected");
                setVisibleButton(false);
            }
            imagePlace.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> profilePage(sender, receiver));
            //System.out.println(invitation.getStatus());

            acceptButton.setOnAction(event -> acceptInvite(sender.getUserId(), receiver.getUserId()));
            rejectButton.setOnAction(event -> rejectInvite(sender.getUserId(), receiver.getUserId()));

            setText(null);
            setGraphic(rootAnchorPane);


        }
    }

    private void setVisibleButton(boolean visible){
        acceptButton.setVisible(visible);
        rejectButton.setVisible(visible);
    }

    private void setImage(User user) {
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

    private void acceptInvite(int senderId, int receiverId){
        Repository.acceptInvitation(senderId, receiverId,true);
        setVisibleButton(false);
        response.setText("Accepted");
    }

    private void rejectInvite(int senderId, int receiverId){
        Repository.acceptInvitation(senderId, receiverId, false);
        setVisibleButton(false);
        response.setText("Rejected");
    }

    private void profilePage(User sender, User receiver){
        imagePlace.getScene().getWindow().hide();
        if(sender.getUserId() == owner.getUserId()){
            OpenWindow.openWindow("view/Profile.fxml",new ProfileController(receiver, sender), "Profile");
        }
        else{
            OpenWindow.openWindow("view/Profile.fxml",new ProfileController(sender, receiver), "Profile");
        }
    }

}
