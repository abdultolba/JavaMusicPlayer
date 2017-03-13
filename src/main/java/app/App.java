package main.java.app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;

public class App extends Application
{
    private static Scene scene;
    private Stage stage;

    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception
    {
        this.stage = stage;

        Parent loader = FXMLLoader.load(getClass().getResource("/layout.fxml"));

        stage.setTitle("Music Player");
        int width = 400;
        int height = 550;
        scene = new Scene(loader, width, height);
        initEvents();
        stage.setScene(scene);
        stage.setResizable(false);

        stage.show();
    }

    private void initEvents()
    {
        stage.setOnCloseRequest(event ->
        {
            Platform.exit();
            System.exit(0);
        });

        scene.setOnDragOver(event ->
        {
            Dragboard db = event.getDragboard();
            if (db.hasFiles())
                event.acceptTransferModes(TransferMode.COPY);
            else
                event.consume();
        });
    }
}
