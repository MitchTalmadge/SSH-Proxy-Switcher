/*
 * Emoji Tools helps users and developers of Android, iOS, and OS X extract, modify, and repackage Emoji fonts.
 * Copyright (C) 2015 - 2016 Mitch Talmadge
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact Mitch Talmadge at mitcht@liveforcode.net
 */

package com.mitchtalmadge.sshproxyswitcher.gui.aptiapi;

import com.mitchtalmadge.sshproxyswitcher.Versioning;
import com.mitchtalmadge.sshproxyswitcher.gui.aptiapi.controllers.UpdateNoticeDialogController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class UpdateNoticeDialog {

    private final Stage stage;
    private UpdateNoticeDialogController controller;

    public UpdateNoticeDialog(String newVersion, String changeLog, String downloadUrl) {
        this.stage = new Stage();
        stage.setTitle("Update Released!");
        stage.getIcons().add(Versioning.getLogoAsJavaFXImage());
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);

        stage.setOnCloseRequest(e -> {
            close();
            e.consume();
        });

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/aptiapi/UpdateNoticeDialog.fxml"));
            Parent root = loader.load();

            this.controller = loader.getController();
            controller.setParent(this);
            controller.setChangeLogText("<html style='font-family: Arial, Helvetica, sans-serif'>"+changeLog+"</html>");
            controller.setHeaderLabelText("SSH Proxy Switcher V"+newVersion+" has been Released!");
            controller.setDownloadUrl(downloadUrl);

            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void display()
    {
        this.stage.showAndWait();
    }

    public void close() {
        stage.close();
    }
}
