package model;

import java.util.List;

public class Work {
    
        String id;
        String title;
        String author;
        List<String> subjects;
    
        public Work() {
        }
    
        public Work(String title, String id, String author, List<String> subjects) {
            super();
            this.title = title;
            this.id = id;
            this.author = author;
            this.subjects = subjects;
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

        public List<String> getSubjects() {
            return subjects;
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
        
        public void setSubjects(List<String> subjects) {
            this.subjects = subjects;
        }
    
}
