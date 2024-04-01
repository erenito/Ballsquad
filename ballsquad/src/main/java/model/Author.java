package model;

import java.util.List;

public class Author {

    String id;
    String name;
    List<Work> works;

    public Author() {
    }

    public Author(String name, String id, List<Work> works) {
        super();
        this.name = name;
        this.id = id;
        this.works = works;
    }

    public String getName() {
        return this.name;
    }

    public String getId() {
        return this.id;
    }

    public List<Work> getWorks() {
        return works;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setWorks(List<Work> works) {
        this.works = works;
    }

}
