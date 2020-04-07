package com.mbajdowski.exceptions;

public class NotSupportedDrawerException extends IllegalArgumentException {
    private static final String messageTemplate = "Supplied FrameDrawer '%s' does not exist!";

    public NotSupportedDrawerException(String notSupportedDrawer) {
        super(String.format(messageTemplate, notSupportedDrawer));
    }

}
