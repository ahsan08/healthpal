package javafeast.therap.healthpal.extras;

import java.util.HashMap;

public class MapValues {

	private HashMap<Integer, String> bloodGroup, cityName, speciality, rating ; 
	
	
	public MapValues() {
		
		settingBloodGroupValues();
		settingCityNames();
		settingSpecialities();
		settingUserRating();
	}
	
	
	 private void settingUserRating() {
		rating = new HashMap<Integer, String>();
		rating.put(0, "");
		rating.put(1, "Low");
		rating.put(2, "Avarage");
		rating.put(3, "High");
		
	}


	private void settingSpecialities() {
		speciality= new HashMap<Integer, String>();
		speciality.put(0, "");
		speciality.put(1, "General");
		speciality.put(2, "Surgury");
		speciality.put(3, "Child");
		speciality.put(4, "Eye");
		
	}


	private void settingCityNames() {
		cityName = new HashMap<Integer, String>();
		cityName.put(0, "");
	    cityName.put(1, "Dhaka");
	    cityName.put(2, "Chittagong");
	    cityName.put(3, "Khulna");
	    cityName.put(4, "Sylhet");
	    cityName.put(5, "Rajshahi");
	    cityName.put(6, "Borishal");
	    cityName.put(7, "Rongpur");
	    cityName.put(8, "Bogra");

	}
	 


	private void settingBloodGroupValues()
	 {
		 
		 bloodGroup = new HashMap<Integer, String>();
		 bloodGroup.put(0, "A+");
		 bloodGroup.put(1, "B+");
		 bloodGroup.put(2, "AB+");
		 bloodGroup.put(3, "O+");
		 bloodGroup.put(4, "A-");
		 bloodGroup.put(5, "B-");
		 bloodGroup.put(6, "AB-");
		 bloodGroup.put(7, "O-");
		 
		 
	 }
	 
	 public String getBloodGroupValues(int key)
	 {
		 String value=bloodGroup.get(key);
		 return value;
	 }
	 
	 public String getCityNames(int key)
	 {
		 String value=cityName.get(key);
		 return value;
	 }
	 
	 public String getSpeciality(int key)
	 {
		 String value=speciality.get(key);
		 return value;
	 }
	 
	 
	 public String getRating(int key)
	 {
		 String value= rating.get(key);
		 return value;
	 }


}
