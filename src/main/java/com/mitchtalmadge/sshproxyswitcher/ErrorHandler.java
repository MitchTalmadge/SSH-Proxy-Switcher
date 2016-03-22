package com.mitchtalmadge.sshproxyswitcher;

import com.aptitekk.aptiapi.AptiAPI;
import com.aptitekk.aptiapi.AptiAPIErrorHandler;
import com.aptitekk.aptiapi.ErrorReport;
import com.mitchtalmadge.sshproxyswitcher.gui.GUIHelper;
import com.mitchtalmadge.sshproxyswitcher.gui.aptiapi.ErrorReportDialog;
import com.mitchtalmadge.sshproxyswitcher.gui.aptiapi.ProgressDialog;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyStringProperty;

public class ErrorHandler extends AptiAPIErrorHandler {

    private AptiAPI aptiAPI;
    private ProgressDialog progressDialog;

    public ErrorHandler(AptiAPI aptiAPI) {
        super(aptiAPI);
        this.aptiAPI = aptiAPI;
    }

    @Override
    public ErrorReport onErrorOccurred(Thread t, Throwable e) {
        ErrorReport errorReport = new ErrorReport(t, e);
        errorReport.setVersion(new Versioning().getVersionString());

        boolean sendReport = new ErrorReportDialog(errorReport).getResult();

        if (sendReport) {
            this.progressDialog = new ProgressDialog("Sending Error Report...");
            return errorReport;
        }
        return null;
    }

    @Override
    public void bindProperties(ReadOnlyDoubleProperty progressProperty, ReadOnlyStringProperty messageProperty) {
        //TODO: Allow for Cancelling
        progressDialog.bindProgressToProperty(progressProperty);
        progressDialog.bindMessagesToProperty(messageProperty);
    }

    @Override
    public void onSendingStarted() {
        progressDialog.display();
    }

    @Override
    public void onSendingComplete(boolean completedSuccessfully) {
        progressDialog.close();
        if (completedSuccessfully)
            GUIHelper.showInformationDialog("Error Report Sent!", "Error Report Sent!", "We have received your error report. Thank you!");
        else
            GUIHelper.showErrorDialog("Error Report Could Not be Sent", "Error Report Could Not be Sent", "We were unable to receive your error report. Sorry for the inconvenience.");
        shutDown();
    }

    @Override
    public void shutDown() {
        Platform.exit();
    }
}
