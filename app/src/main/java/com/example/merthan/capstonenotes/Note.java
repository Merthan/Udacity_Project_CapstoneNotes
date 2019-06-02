package com.example.merthan.capstonenotes;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Note {
    private String title;
    private String body;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Note(String title, String body,String id) {
        this.id=id;
        this.title = title;
        this.body = body;
    }

    public Note(){}

    @Override
    public String toString() {
        return title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return Objects.equals(getId(), note.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public String[] asStringArray(){
        return new String[]{title,body,id};
    }

    public Note(String[] arr){
        this(arr[0],arr[1],arr[2]);
    }

}
