package com.ballsquad.ballsquad.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "works")
public class Work {

        @Id
        @Column(name = "id", nullable = false, unique = true)
        String id;

        @Column(name = "title", nullable = false)
        String title;

        @Column(name = "author", nullable = false)
        String author;
    
        public Work() {
        }
    
        public Work(String title, String id, String author) {
            super();
            this.title = title;
            this.id = id;
            this.author = author;
        }
    
        public String getTitle() {
            return this.title;
        }
    
        public String getId() {
            return this.id;
        }
    
        public String getAuthor() {
            return this.author;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    
        public void setId(String id) {
            this.id = id;
        }
    
        public void setAuthor(String author) {
            this.author = author;
        }
    
}
