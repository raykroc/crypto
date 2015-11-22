package crypto3;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class RSA_CRT_THR2 {
	// e - public key
	// d - private key
	private BigInteger n, e, d;
	int numPrimes;
	List<BigInteger> primes, dPrimes, toSum;

	public RSA_CRT_THR2(int numPrimes, int bitlen, List<BigInteger> primes) {
		n = BigInteger.ONE;
		this.numPrimes = numPrimes;
		this.primes = primes;
		toSum = new ArrayList<>();
		generate(numPrimes, bitlen);
	}

	synchronized void addToSum(BigInteger bi) {
		toSum.add(bi);
	}

	public void generate(int numPrimes, int bitlen) {
		if (primes == null) {
			PrimeGenerator pg = new PrimeGenerator(numPrimes, bitlen);
			primes = pg.getPrimes();
		}
		dPrimes = new ArrayList<>();

		for (BigInteger b : primes) {
			n = n.multiply(b);
		}

		BigInteger m = BigInteger.ONE;
		for (BigInteger b : primes) {
			BigInteger db = b.subtract(BigInteger.ONE);
			m = m.multiply(db);
			dPrimes.add(db);
		}

		e = new BigInteger("3");
		while (m.gcd(e).intValue() > 1) {
			e = e.add(new BigInteger("2"));
		}
		d = e.modInverse(m);

		for (int i = 0; i < numPrimes; i++) {
			dPrimes.set(i, d.remainder(dPrimes.get(i)));
		}
	}

	public synchronized String encrypt(String message) {
		return (new BigInteger(message.getBytes())).modPow(e, n).toString();
	}

	String decryptCRT(String message) {
		BigInteger c = new BigInteger(message);
		System.out.println(c);
		System.out.println(n);
		for (int i = 0; i < numPrimes; i++) {
			new CRTThread(c.modPow(dPrimes.get(i), primes.get(i)), n, primes.get(i)).start();
		}

		BigInteger sum = BigInteger.ZERO;
		while (toSum.size() < numPrimes) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		for (BigInteger b : toSum) {
			sum = sum.add(b);
		}

		toSum.clear();

		return new String(sum.mod(n).toByteArray());
	}

	public synchronized String decrypt(String message) {
		return new String((new BigInteger(message)).modPow(d, n).toByteArray());
	}

	public void saveKeys(String name) {
		try {
			PrintWriter printWriter = new PrintWriter(name);
			printWriter.println("N: " + n);
			printWriter.println("E: " + e);
			printWriter.println("D: " + d);
			printWriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private class CRTThread extends Thread {
		BigInteger ai, n, ni;

		public CRTThread(BigInteger ai, BigInteger n, BigInteger ni) {
			this.ai = ai;
			this.n = n;
			this.ni = ni;
		}

		public void run() {
			BigInteger t = n.divide(ni);
			addToSum(ai.multiply(t).multiply(t.modInverse(ni)));
		}
	}

	public static void main(String[] args) {
		String plaintext1 = "Yellow and Black Border Collies";

//		List<BigInteger> list = new ArrayList<>();
//		try {
//			Scanner in = new Scanner(new FileReader("primes2048.txt"));
//			while (in.hasNextLine()) {
//				String line = in.nextLine();
//				list.add(new BigInteger(line));
//				plaintext1 += line;
//			}
//			in.close();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//
//		plaintext1 = plaintext1.substring(0, 63);

		RSA_CRT_THR2 rsa = new RSA_CRT_THR2(8, 32, null);

		long t0, t1, t2;// , t3;
		t0 = System.currentTimeMillis();
		String ciphertext = rsa.encrypt(plaintext1);
		t1 = System.currentTimeMillis();
		System.out.println("enc time: " + (t1 - t0));
		String plaintext2 = rsa.decryptCRT(ciphertext);
		t2 = System.currentTimeMillis();
		System.out.println("crt time: " + (t2 - t1));
		System.out.println(plaintext2);
		// String plaintext3 = rsa.decrypt(ciphertext);
		// t3 = System.currentTimeMillis();
		// System.out.println("dec time: " + (t3 - t2));
		// System.out.println(plaintext3);

		// String keysPath = "keys.txt";
		// rsa.saveKeys(keysPath);
	}
}