package com.example.happypaws;

public class Animal {
    private int animalId;
    private int userId;
    private String name;
    private String type;
    private String ownerName;
    private String birthDate;
    private String gender;

    public Animal(int animalId, int userId, String name, String type, String ownerName, String birthDate, String gender) {
        this.animalId = animalId;
        this.userId = userId;
        this.name = name;
        this.type = type;
        this.ownerName = ownerName;
        this.birthDate = birthDate;
        this.gender = gender;
    }

    public int getAnimalId() {
        return animalId;
    }

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getGender() {
        return gender;
    }
}