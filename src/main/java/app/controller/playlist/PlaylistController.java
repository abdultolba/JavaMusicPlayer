package main.java.app.controller.playlist;

import app.controller.MediaController;
import com.sun.media.sound.InvalidFormatException;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class PlaylistController
{
    final private MediaController mediaController;
    final private ListView<Song> listView;

    private ObservableList<Song> list;
    private ContextMenu contextMenu;

    private Song currentSong, selectedSong;

    public PlaylistController(MediaController mediaController, ListView<Song> listView)
    {
        this.listView = listView;
        this.mediaController = mediaController;
        initListObjects();
        initActions();
    }

    private void initListObjects()
    {
        currentSong = null;
        selectedSong = null;

        ListProperty<Song> listProperty = new SimpleListProperty<>();
        list = FXCollections.observableArrayList();

        listProperty.set(list);
        listView.itemsProperty().bind(listProperty);
    }

    private void initActions()
    {
        initContextMenu();
        initMouseClick();
        initDeletingFiles();
        initAddFilesByDragging();
    }

    private void initContextMenu()
    {
        contextMenu = new ContextMenu();

        MenuItem play = new MenuItem("Play");
        MenuItem remove = new MenuItem("Remove");
        MenuItem showInExplorer = new MenuItem("Show in explorer");

        contextMenu.addEventFilter(MouseEvent.MOUSE_RELEASED, event ->
        {
            if (event.getButton() == MouseButton.SECONDARY)
            {
                contextMenu.hide();
                event.consume();
            }
        });

        play.setOnAction(e -> mediaController.play());

        remove.setOnAction(e -> removeSong(selectedSong));

        showInExplorer.setOnAction(e ->
        {
            try
            {
                // Windows platform only
                Runtime.getRuntime().exec("explorer.exe /select," + selectedSong.getFilePath());
            }
            catch (IOException e1)
            {
                contextMenu.hide();
            }
        });

        contextMenu.getItems().addAll(play, remove, showInExplorer);
    }

    private void initMouseClick()
    {
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                selectedSong = newValue);

        listView.setOnMouseClicked(event ->
        {
            if (event.getButton().equals(MouseButton.PRIMARY))
            {
                if (event.getClickCount() == 2 && listView.getSelectionModel().getSelectedItem() != null)
                {
                    // TODO: runs even if double click on empty space, when any item in appController.listView is selected
                    mediaController.play();
                }
            }
            else if (event.getButton().equals(MouseButton.SECONDARY))
            {
                if (listView.getSelectionModel().getSelectedItem() != null)
                {
                    contextMenu.show(listView, event.getScreenX(), event.getScreenY());
                }
            }
        });

    }

    private void initDeletingFiles()
    {
        listView.setOnKeyPressed(event ->
        {
            if (selectedSong != null && event.getCode() == KeyCode.DELETE)
                removeSong(selectedSong);
        });
    }

    private void initAddFilesByDragging()
    {
        listView.setOnDragDropped(event ->
        {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles())
            {
                success = true;
                for (File file : db.getFiles())
                {
                    final String filePath = file.getAbsolutePath();
                    Platform.runLater(() ->
                    {
                        try
                        {
                            Song newSong = new Song(filePath);
                            list.add(newSong);
                        }
                        catch (InvalidFormatException e)
                        {
                            // do nothing
                        }
                    });
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    private void removeSong(Song song)
    {
        list.remove(song);
    }

    private boolean isPlaylistEmpty()
    {
        return (list.size() == 0 || currentSong == null);
    }

    public boolean hasPrev()
    {
        if (isPlaylistEmpty())
            return false;
        else
            return list.indexOf(currentSong) != 0;
    }

    public boolean hasNext()
    {
        if (isPlaylistEmpty())
            return false;
        else
            return list.indexOf(currentSong) != list.size() - 1;
    }

    public void setPrevAsSelected()
    {
        selectedSong = list.get(list.indexOf(currentSong) - 1);
        listView.getSelectionModel().select(selectedSong);
    }

    public void setNextAsSelected()
    {
        selectedSong = list.get(list.indexOf(currentSong) + 1);
        listView.getSelectionModel().select(selectedSong);
    }

    public void setRandomAsSelected()
    {
        Random rd = new Random();
        int randomIndex = list.indexOf(currentSong);
        while (randomIndex == list.indexOf(currentSong))
        {
            randomIndex = rd.nextInt(getSize());
        }
        selectedSong = list.get(randomIndex);
        listView.getSelectionModel().select(selectedSong);
    }

    public Song getSelectedSong()
    {
        return selectedSong;
    }

    public Song getCurrentSong()
    {
        return currentSong;
    }

    public void setCurrentSong(Song currentSong)
    {
        this.currentSong = currentSong;
    }

    public int getSize()
    {
        return list.size();
    }
}
