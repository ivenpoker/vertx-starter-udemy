package com.study.udemy.json.domain;

public class Person {

    private Integer id;
    private String name;
    private boolean lovesVertx;

    // useful for java object and json object mapping
    public Person() {}

    public Person(final Integer id, final String name, final boolean lovesVertx) {
        this.id = id;
        this.name = name;
        this.lovesVertx = lovesVertx;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isLovesVertx() {
        return lovesVertx;
    }

    public void setLovesVertx(boolean lovesVertx) {
        this.lovesVertx = lovesVertx;
    }
}
