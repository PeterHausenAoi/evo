package com.github.PeterHausenAoi.evo.util;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class TextLogger implements LogHandler {
    private static final String TAG = TextLogger.class.getSimpleName();

    private final TextArea mArea;

    public TextLogger(TextArea area) {
        this.mArea = area;
    }

    @Override
    public void handle(String msg) {
        StringBuilder builder = new StringBuilder();

        builder.append("\n")
                .append(msg);

        Platform.runLater(() -> {
            mArea.appendText(builder.toString());
            mArea.setScrollTop(Double.MAX_VALUE);
        });
    }
}