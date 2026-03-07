package model;

/**
 * Model class representing an Employee entity.
 */
public class Employee {

    private int    employeeId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String department;
    private String position;
    private double salary;
    private String hireDate;
    private String status;

    // ── Constructors ──────────────────────────────────────────────────────────

    public Employee() {}

    public Employee(int employeeId, String firstName, String lastName,
                    String email, String phone, String department,
                    String position, double salary, String hireDate, String status) {
        this.employeeId = employeeId;
        this.firstName  = firstName;
        this.lastName   = lastName;
        this.email      = email;
        this.phone      = phone;
        this.department = department;
        this.position   = position;
        this.salary     = salary;
        this.hireDate   = hireDate;
        this.status     = status;
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public int    getEmployeeId()              { return employeeId; }
    public void   setEmployeeId(int id)        { this.employeeId = id; }

    public String getFirstName()               { return firstName; }
    public void   setFirstName(String n)       { this.firstName = n; }

    public String getLastName()                { return lastName; }
    public void   setLastName(String n)        { this.lastName = n; }

    public String getEmail()                   { return email; }
    public void   setEmail(String e)           { this.email = e; }

    public String getPhone()                   { return phone; }
    public void   setPhone(String p)           { this.phone = p; }

    public String getDepartment()              { return department; }
    public void   setDepartment(String d)      { this.department = d; }

    public String getPosition()                { return position; }
    public void   setPosition(String p)        { this.position = p; }

    public double getSalary()                  { return salary; }
    public void   setSalary(double s)          { this.salary = s; }

    public String getHireDate()                { return hireDate; }
    public void   setHireDate(String d)        { this.hireDate = d; }

    public String getStatus()                  { return status; }
    public void   setStatus(String s)          { this.status = s; }

    @Override
    public String toString() {
        return firstName + " " + lastName + " (" + department + ")";
    }
}
