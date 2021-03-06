package app.controller.cells;

import app.controller.OpenWindow;
import app.controller.ProfileController;
import app.model.Notification;
import app.model.User;
import app.repository.Repository;
import com.jfoenix.controls.JFXListCell;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
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

public class NotificationCellController extends JFXListCell<Notification> {

    private User user;

    public NotificationCellController(User user) {
        this.user = user;
    }

    @FXML
    private AnchorPane rootAnchorPane;

    @FXML
    private Circle imagePlace;

    @FXML
    private Label name;

    @FXML
    private Label time;

    @FXML
    private Label reason;

    private FXMLLoader fxmlLoader;

    @FXML
    public void initialize() {

    }

    public void updateItem(Notification notification, boolean empty){
        super.updateItem(notification, empty);

        if(empty || notification == null){
            setText(null);
            setGraphic(null);
        }else{
            if(fxmlLoader == null){
                fxmlLoader = new FXMLLoader(getClass().getResource("../../../view/cells/NotificationCell.fxml"));
                fxmlLoader.setController(this);
                try {
                    fxmlLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            User user = Repository.getUserById(notification.getByUserId(), notification.getByUserId());
            setImage(user);
            name.setText(user.getFirstname() + " " + user.getLastname());
            setReason(notification);
            time.setText(notification.getTime().toString());

            imagePlace.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> profile(user));

            setText(null);
            setGraphic(rootAnchorPane);

        }
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

    private void setReason(Notification notification){
        if(notification instanceof Notification.BirthdayNotification){
            reason.setText("BirthDay");
        }
        else if(notification instanceof Notification.CommentNotification){
            reason.setText("Write comment");
        }
        else if(notification instanceof Notification.LikeArticleNotification){
            reason.setText("Like your article");
        }
        else if(notification instanceof Notification.LikeCommentNotification){
            reason.setText("Like your comment");
        }
        else if(notification instanceof Notification.ProfileVisitNotification){
            reason.setText("Visit your profile");
        }
        else if(notification instanceof Notification.ReplyCommentNotification){
            reason.setText("Reply your comment");
        }
        else if(notification instanceof Notification.SkillEndorseNotification){
            reason.setText("Endorse your skill");
        }
    }

    private void profile(User user){
        imagePlace.getScene().getWindow().hide();
        OpenWindow.openWindow("view/Profile.fxml", new ProfileController(user, this.user), "Profile");
    }
}
