package com.ballsquad.ballsquad.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "authors")
public class Author {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    String id;

    @Column(name = "name", nullable = false)
    String name;

    public Author() {
    }

    public Author(String name, String id) {
        super();
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public String getId() {
        return this.id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

}
