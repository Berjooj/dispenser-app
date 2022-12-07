package com.example.dispenser_app;

public class Dispenser {
    private String token, address, complement;
    private float current_capacity, capacity;
    private int id;

    public Dispenser(String token, String address, String complement, float current_capacity, float capacity, int id) {
        this.token = token;
        this.address = address;
        this.complement = complement;
        this.current_capacity = current_capacity;
        this.capacity = capacity;
        this.id = id;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setComplement(String complement) {
        this.complement = complement;
    }

    public void setCurrent_capacity(float current_capacity) {
        this.current_capacity = current_capacity;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public String getAddress() {
        return address;
    }

    public String getComplement() {
        return complement;
    }

    public float getCurrentCapacity() {
        return current_capacity;
    }

    public float getCapacity() {
        return capacity;
    }

    public int getId() {
        return id;
    }
}
