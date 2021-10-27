package com.study.udemy.customcodec;

public class Ping {
    private String message;
    private boolean enabled;

    public Ping() {}


    public Ping(final String message, final boolean enabled) {
        this.message = message;
        this.enabled = enabled;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String name) {
        this.message = name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "Ping{message=%s, enabled=%s}".formatted(getMessage(), isEnabled());
    }
}
