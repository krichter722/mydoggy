package org.noos.xing.mydoggy.mydoggyset;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MyDoggySetLoader {

	public MyDoggySetLoader() {
	}

	public void load() {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				MyDoggySet myDoggySet = new MyDoggySet();
				try {
					myDoggySet.setUp();
					myDoggySet.start(null);
				} catch (Exception e) {
					e.printStackTrace();  
				}
			}
		});
		thread.start();
	}

}
