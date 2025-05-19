package com.example.acme_explorer.data;

import java.util.Date;

public class Actor {
    private String _id;
    private String name;
    private String surname;
    private String email;
    private String phone;
    private String role;
    private Date created;

    public Actor() {
        this._id = "";
        this.name = "";
        this.surname = "";
        this.email = "";
        this.phone = "";
        this.role = "CONSUMER";
        this.created = new Date();
    }

    public Actor(String _id, String name, String surname, String email, String phone, String role) {
        this._id = _id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.created = new Date();
    }

    // Getters y Setters...

    public String get_id() { return _id; }
    public void set_id(String _id) { this._id = _id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public Date getCreated() { return created; }
    public void setCreated(Date created) { this.created = created; }
}
