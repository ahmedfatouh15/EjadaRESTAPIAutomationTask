package utils;

public class Shared {

	public String generateFakeEmail(int usernameLength, String domain) {
		String emailAddress = "";
		String characters = "abcdefghijklmnopqrstuvwxyz1234567890_";
		while (emailAddress.length() < usernameLength) {
			int charOrder = (int) (Math.random() * characters.length());
			emailAddress += characters.substring(charOrder, charOrder + 1);
// 			emailAddress += Integer.valueOf((int) (Math.random() * 99)).toString();
			
		}
		emailAddress += "@" + domain;
		
		return emailAddress;
	}

}
