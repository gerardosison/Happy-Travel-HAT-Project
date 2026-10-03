import ui.UI;
import ui.LoginModule;
import javax.swing.*;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UI.applyGlobalDefaults();
            new LoginModule();
        });
    }
}