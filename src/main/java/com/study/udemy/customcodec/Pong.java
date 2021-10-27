package com.study.udemy.customcodec;

public class Pong {
    private Integer id;

    public Pong() {}

    public Pong(final Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Pong{id=%s}".formatted(getId());
    }
}
