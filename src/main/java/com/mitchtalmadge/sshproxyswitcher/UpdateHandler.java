package com.mitchtalmadge.sshproxyswitcher;

import com.aptitekk.aptiapi.AptiAPIUpdateHandler;
import com.mitchtalmadge.sshproxyswitcher.gui.aptiapi.UpdateNoticeDialog;

public class UpdateHandler extends AptiAPIUpdateHandler {
    @Override
    public void onUpdateAvailable(String newVersion, String changeLog, String downloadUrl) {
        new UpdateNoticeDialog(newVersion, changeLog, downloadUrl).display();
    }
}
