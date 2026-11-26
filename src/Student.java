class Student extends User {

    public Student (){ // Default
        super();
    }

    public Student (int Id){ // ID only
        super(Id);
    }

    public Student (int Id, String Name){ // ID and name only
        super(Id, Name);
    }

    public Student (int Id, String Name, String Email){
        super(Id, Name, Email);
    }

    public void updateStudent(String Name, String Email) {
        if (Name != null && !Name.trim().isEmpty()) {
            setName(Name.trim());
        }
        if (Email != null && !Email.trim().isEmpty()) {
            setEmail(Email.trim());
        }
    }

    public void viewProfile() {
        System.out.println("Student ID : " + getId());
        System.out.println("Name       : " + getName());
        System.out.println("Email      : " + getEmail());
    }
}
