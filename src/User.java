public class User {
    private int Id;
    private String Name;
    private String Email;

    public User (){ // Default
        this.Id = 0;
        this.Name = "Anonymous";
        this.Email = "Not provided";
    }

    public User (int Id){ // ID only
        this.Id = Id;
        this.Name = "Anonymous";
        this.Email = "Not provided";
    }

    public User (int Id, String Name){ // ID and name only
        this.Id = Id;
        this.Name = Name;
        this.Email = "Not provided";
    }

    public User (int Id, String Name, String Email){
        this.Id = Id;
        this.Name = Name;
        this.Email = Email;
    }
    
    public int getId(){return Id;}
    public void setId(int Id) { this.Id = Id;}

    public String getName(){return Name;}
    public void setName(String Name) { this.Name = Name;}

    public String getEmail(){return Email;}
    public void setEmail(String Email) { this.Email = Email;}

}
