package crypto3;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class RSA_CRT {
	// e - public key
	// d - private key
	private BigInteger n = BigInteger.ONE, e, d;
	int numPrimes;
	List<BigInteger> primes, dPrimes, mPrimes;

	public RSA_CRT(int numPrimes, int bitlen, List<BigInteger> primes) {
		this.numPrimes = numPrimes;
		this.primes = primes;
		generate(numPrimes, bitlen);
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

	public synchronized String decryptCRT(String message) {
		BigInteger c = new BigInteger(message);
//		mPrimes = new ArrayList<>();
//		for (int i = 0; i < numPrimes; i++) {
//			mPrimes.add(c.modPow(dPrimes.get(i), primes.get(i)));
//		}

		BigInteger sum = BigInteger.ZERO;
		for (int i = 0; i < numPrimes; i++) {
			BigInteger t = n.divide(primes.get(i));
//			sum = sum.add(mPrimes.get(i).multiply(t).multiply(t.modInverse(primes.get(i)))).mod(n);
			sum = sum.add(c.modPow(dPrimes.get(i), primes.get(i)).multiply(t).multiply(t.modInverse(primes.get(i)))).mod(n);
		}

		return new String(sum.toByteArray());
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
	public static void main(String[] args) {
		String plaintext1 = "";//Yellow and Black Border Collies";

		List<BigInteger> list = new ArrayList<>();
		try {
			Scanner in = new Scanner(new FileReader("primes2048.txt"));
			while (in.hasNextLine()) {
				String line = in.nextLine();
				list.add(new BigInteger(line));
				plaintext1 += line;
			}
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		plaintext1 = plaintext1.substring(0, 2047);
		RSA_CRT rsa = new RSA_CRT(8, 2048, list);

		long t0, t1, t2;//, t3;
		t0 = System.currentTimeMillis();
		String ciphertext = rsa.encrypt(plaintext1);
		t1 = System.currentTimeMillis();
		System.out.println("enc time: " + (t1 - t0));
		String plaintext2 = rsa.decryptCRT(ciphertext);
		t2 = System.currentTimeMillis();
		System.out.println("crt time: " + (t2 - t1));
		System.out.println(plaintext2);
//		String plaintext3 = rsa.decrypt(ciphertext);
//		t3 = System.currentTimeMillis();
//		System.out.println("dec time: " + (t3 - t2));
//		System.out.println(plaintext3);

//		String keysPath = "keys.txt";
//		rsa.saveKeys(keysPath);
	}
}