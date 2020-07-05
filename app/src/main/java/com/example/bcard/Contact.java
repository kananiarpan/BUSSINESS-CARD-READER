package com.example.bcard;


public class Contact {
    private String imgUrl;
    private String company;
    private String email;
    private String phone;
    private String website;
    private String person;
    private String address;


    public Contact(){}
    public Contact(String imgUrl,String company, String email, String phone, String website, String person, String address) {
        this.imgUrl= imgUrl;
        this.company = company;
        this.email = email;
        this.phone = phone;
        this.website = website;
        this.person = person;
        this.address = address;

    }




    public String getImgUrl(){
        return imgUrl;
    }
    public void setImgUrl(String imgUrl) {
        this.imgUrl=imgUrl;
    }
    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


}

