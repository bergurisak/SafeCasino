package vinnsla;

import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class RouletteController implements Initializable {
    private PlayerProfile profile;

    public void setProfile(PlayerProfile profile) {
        this.profile = profile;
        System.out.println("Profile set: $" + profile.getBalance());
        updateBetDisplay();

    }

    private void updateBetDisplay() {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
