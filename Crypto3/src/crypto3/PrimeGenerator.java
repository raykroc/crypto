package crypto3;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PrimeGenerator {
	static long st;
	static int id = 0;
	
	SecureRandom rnd;
	int num;
	List<BigInteger> primes = new ArrayList<>();

	public PrimeGenerator(int num, int bitLength) {
		this.num = num;
		rnd = new SecureRandom();

		List<GeneratorThread> list = new ArrayList<>();
		
		for (int i = 0; i < num; i++) {
			list.add(new GeneratorThread(rnd, bitLength));
		}
		
		st = System.currentTimeMillis();
		for (int i = 0; i < num; i++) {
			list.get(i).start();
		}
	}
	
	List<BigInteger> getPrimes() {
		while (primes.size() < num) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) { e.printStackTrace(); }
		}
		return primes;
	}

	private class GeneratorThread extends Thread {
		Random rnd;
		int bitLength;
		
		public GeneratorThread(Random rnd, int bitLength) {
			this.rnd = rnd;
			this.bitLength = bitLength;
		}
		
		public void run() {
			primes.add(BigInteger.probablePrime(bitLength, rnd));
			//System.out.println(id++ + ": time: " + (System.currentTimeMillis() - st) + ": " + bi);
		}
	}

	public static void main(String[] args) {
		//new PrimeGenerator(8, 1024);
	}
}
