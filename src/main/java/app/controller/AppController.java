package main.java.app.controller;

import main.java.app.controller.playlist.Song;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class AppController implements Initializable
{
    @FXML
    private Button playButton;
    @FXML
    private Button pauseButton;
    @FXML
    private Button stopButton;
    @FXML
    private Button prevButton;
    @FXML
    private Button nextButton;
    @FXML
    private ToggleButton repeatButton;
    @FXML
    private ToggleButton muteButton;
    @FXML
    private ToggleButton shuffleButton;
    // package-private
    @FXML
    Label currentSongInfoLabel;
    @FXML
    Label songCurrentTimeLabel;
    @FXML
    Label songTotalTimeLabel;
    @FXML
    Slider progressSlider;
    @FXML
    Slider volumeSlider;
    @FXML
    ListView<Song> listView;

    private MediaController mediaController;

    private static final double PROGRESS_MIN_CHANGE = 1.5;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        mediaController = new MediaController(this);

        initButtons();
        initSliders();
    }

    private void initButtons()
    {
        playButton.setOnAction(event -> mediaController.play());
        pauseButton.setOnAction(event -> mediaController.pause());
        stopButton.setOnAction(event -> mediaController.stop());
        prevButton.setOnAction(event -> mediaController.playPrev());
        nextButton.setOnAction(event -> mediaController.playNext());

        muteButton.selectedProperty().addListener(observable ->
        {
            if (muteButton.isSelected())
            {
                mediaController.saveVolume(volumeSlider.getValue());
                volumeSlider.setValue(0);
                volumeSlider.setDisable(true);
            }
            else
            {
                volumeSlider.setValue(mediaController.getVolume());
                volumeSlider.setDisable(false);
            }
        });
    }

    private void initSliders()
    {
        progressSlider.valueChangingProperty().addListener((observable, wasChanging, isChanging) ->
        {
            if (!isChanging)
            {
                mediaController.seekPlayer(progressSlider.getValue());
            }
        });

        progressSlider.valueProperty().addListener((observable, oldValue, newValue) ->
        {
            if (!progressSlider.isValueChanging())
            {
                double currentTime = mediaController.getCurrentTime();
                if (Math.abs(currentTime - newValue.doubleValue()) > PROGRESS_MIN_CHANGE)
                {
                    mediaController.seekPlayer(newValue.doubleValue());
                }
            }
        });

        volumeSlider.valueProperty().addListener(observable ->
                mediaController.setVolume(volumeSlider.getValue() / 100));

        volumeSlider.setTooltip(new Tooltip("Volume"));
    }

    public boolean isRepeatButtonSelected()
    {
        return repeatButton.isSelected();
    }

    public boolean isShuffleButtonSelected()
    {
        return shuffleButton.isSelected();
    }
}
