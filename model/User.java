package model;

public class User {
    private int userId;
    private String name;
    private String email;  
    private String password;
    private String phoneNumber;
    private String address;
    private String role;
    private String registrationDate;

    // Private constructor for Builder pattern
    private User(Builder builder) {
        this.userId = builder.userId;
        this.name = builder.name;
        this.email = builder.email;  
        this.password = builder.password;
        this.phoneNumber = builder.phoneNumber;
        this.address = builder.address;
        this.role = builder.role;
        this.registrationDate = builder.registrationDate;
    }

    // Getters
    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {  
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getRole() {
        return role;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    // Builder class
    public static class Builder {
        private int userId;
        private String name;
        private String email;  
        private String password;
        private String phoneNumber;
        private String address;
        private String role;
        private String registrationDate;

        public Builder userId(int userId) {
            this.userId = userId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder email(String email) {  
            this.email = email;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder role(String role) {
            this.role = role;
            return this;
        }

        public Builder registrationDate(String registrationDate) {
            this.registrationDate = registrationDate;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}
