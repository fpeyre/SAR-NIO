package Tests;

public class MainTest {

	public static void main(String[] args) {
		new Thread(new ServerTest(3201)).start();

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			System.out.println("Exception d'interruption : "+e.getMessage());
		}

		new Thread(new ClientTest("localhost",3201)).start();
	}
	
	
}
