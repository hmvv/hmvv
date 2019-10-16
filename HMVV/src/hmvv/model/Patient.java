package hmvv.model;

public class Patient {
	public final String mrn;
	public final String firstName;
	public final String lastName;
	
	public Patient(String mrn, String firstName, String lastName) {
		this.mrn = mrn;
		this.firstName = firstName;
		this.lastName = lastName;
	}
}
