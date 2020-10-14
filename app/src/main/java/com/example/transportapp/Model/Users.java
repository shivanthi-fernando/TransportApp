package com.example.transportapp.Model;

public class Users
{
    private String name, nic, password;

    public Users(String name, String nic, String password) {
        this.name = name;
        this.nic = nic;
        this.password = password;
    }

    public Users()
    {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
