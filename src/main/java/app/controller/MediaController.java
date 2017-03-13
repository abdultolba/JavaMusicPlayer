package main.java.app.controller;

import main.java.app.controller.playlist.PlaylistController;
import main.java.app.controller.playlist.Song;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class MediaController
{
    final private AppController appController;

    final private PlaylistController playlistController;

    private MediaPlayer mediaPlayer;
    private double volume;
    private boolean paused;


    // init
    public MediaController(AppController appController)
    {
        volume = 50;
        paused = false;

        this.appController = appController;
        playlistController = new PlaylistController(this, this.appController.listView);
    }

    // public methods - main UI actions
    public void play()
    {
        if (playlistController.getSelectedSong() == null)
            return;
        else if (playlistController.getSelectedSong().equals(playlistController.getCurrentSong()))
        {
            if (paused)
            {
                if (!isMediaPlayerEmpty())
                    mediaPlayer.play();
                paused = false;
            }
            else
            {
                if (!isMediaPlayerEmpty())
                {
                    mediaPlayer.stop();
                    mediaPlayer.play();
                }
            }
            update();
        }
        else
        {
            if (playlistController.getCurrentSong() != null)
                mediaPlayer.stop();

            playlistController.setCurrentSong(playlistController.getSelectedSong());
            mediaPlayer = playlistController.getCurrentSong().getMediaPlayer();
            update();
            mediaPlayer.play();
        }
    }

    public void pause()
    {
        if (!isMediaPlayerEmpty())
        {
            if (paused)
            {
                paused = false;
                mediaPlayer.play();
            }
            else
            {
                mediaPlayer.pause();
                paused = true;
            }
        }
    }

    public void stop()
    {
        if (!isMediaPlayerEmpty())
            mediaPlayer.stop();
    }

    public void playPrev()
    {
        if (appController.isShuffleButtonSelected())
        {
            playlistController.setRandomAsSelected();
            play();
        }
        else if (playlistController.hasPrev())
        {
            playlistController.setPrevAsSelected();
            play();
        }
    }

    public void playNext()
    {
        if (appController.isShuffleButtonSelected())
        {
            playlistController.setRandomAsSelected();
            play();
        }
        else if (playlistController.hasNext())
        {
            playlistController.setNextAsSelected();
            play();
        }
    }

    // public methods - additional actions
    public void seekPlayer(double seconds)
    {
        if (!isMediaPlayerEmpty())
            mediaPlayer.seek(Duration.seconds(seconds));
    }

    public double getCurrentTime()
    {
        return mediaPlayer.getCurrentTime().toSeconds();
    }

    public void saveVolume(double volume)
    {
        if (!isMediaPlayerEmpty())
            this.volume = volume;
    }

    public void setVolume(double volume)
    {
        if (!isMediaPlayerEmpty())
            mediaPlayer.setVolume(volume);
    }

    public double getVolume()
    {
        return volume;
    }

    // private methods


    private void playShuffled()
    {
        if (playlistController.getSize() != 1)
        {
            playlistController.setRandomAsSelected();
        }
        play();
    }


    private boolean isMediaPlayerEmpty()
    {
        return mediaPlayer == null;
    }

    private void update()
    {
        // set player max value
        mediaPlayer.totalDurationProperty().addListener((observable, oldDuration, newDuration) ->
                appController.progressSlider.setMax(newDuration.toSeconds()));

        // set total time
        appController.songTotalTimeLabel.setText(playlistController.getCurrentSong().getLengthString());

        // set current time label and progress slider
        mediaPlayer.currentTimeProperty().addListener((observable, oldTime, newTime) ->
        {
            if (!appController.progressSlider.isValueChanging())
            {
                appController.progressSlider.setValue(newTime.toSeconds());
                int currentTime = (int) mediaPlayer.getCurrentTime().toSeconds();
                appController.songCurrentTimeLabel.setText(Song.getFormattedTime(currentTime));
            }
        });

        // set end of media
        mediaPlayer.setOnEndOfMedia(() ->
        {

            if (appController.isRepeatButtonSelected())
            {
                mediaPlayer.seek(Duration.ZERO);
            }
            else
            {
                if (appController.isShuffleButtonSelected())
                {
                    playShuffled();
                }
                else
                {
                    // if end of list
                    if (!playlistController.hasNext())
                        mediaPlayer.stop();
                    else
                        playNext();
                }
            }

        });

        // update volume (in fact restore previous song volume)
        mediaPlayer.setVolume(appController.volumeSlider.getValue() / 100);

        // update info label
        appController.currentSongInfoLabel.setText("Artist:\t" + playlistController.getCurrentSong().getArtist()
                + "\nTitle:\t\t" + playlistController.getCurrentSong().getTitle()
                + "\nLength:\t" + playlistController.getCurrentSong().getLengthString());
    }
}