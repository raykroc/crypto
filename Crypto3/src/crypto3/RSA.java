package crypto3;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.List;
import java.util.Scanner;

public class RSA {
	// e - public key
	// d - private key
	private BigInteger n = BigInteger.ONE, e, d;

	public RSA(int numPrimes, int bitlen) {
		generate(numPrimes, bitlen);
	}

	public RSA(BigInteger N, BigInteger E, BigInteger D) {
		n = N;
		e = E;
		d = D;
	}

	public void generate(int numPrimes, int bitlen) {
		PrimeGenerator pg = new PrimeGenerator(numPrimes, bitlen);
		List<BigInteger> primes = pg.getPrimes();

		for (BigInteger b : primes) {
			n = n.multiply(b);
		}

		BigInteger m = BigInteger.ONE;
		for (BigInteger b : primes) {
			m = m.multiply(b.subtract(BigInteger.ONE));
		}

		e = new BigInteger("3");
		while (m.gcd(e).intValue() > 1) {
			e = e.add(new BigInteger("2"));
		}
		d = e.modInverse(m);
	}

	public synchronized String encrypt(String message) {
		return (new BigInteger(message.getBytes())).modPow(e, n).toString();
	}

	public synchronized String decrypt(String message) {
		return new String((new BigInteger(message)).modPow(d, n).toByteArray());
	}

	private synchronized BigInteger getN() {
		return n;
	}

	private synchronized BigInteger getE() {
		return e;
	}

	private synchronized BigInteger getD() {
		return d;
	}

	public void saveKeys(String name) {
		try {
			PrintWriter printWriter = new PrintWriter(name);
			printWriter.println("N: " + getN());
			printWriter.println("E: " + getE());
			printWriter.println("D: " + getD());
			printWriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		long st, dt, et;
		String plaintext1 = "Yellow and Black Border Collies";
		String keysPath = "keys.txt";
		RSA rsa;

		try {
			Scanner in = new Scanner(new FileReader(keysPath));
			BigInteger n = new BigInteger(in.nextLine().substring(3));
			BigInteger e = new BigInteger(in.nextLine().substring(3));
			BigInteger d = new BigInteger(in.nextLine().substring(3));
			in.close();

			rsa = new RSA(n, e, d);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}

//		rsa = new RSA(8, 1024);

		st = System.currentTimeMillis();
		String ciphertext = rsa.encrypt(plaintext1);
		dt = System.currentTimeMillis();
		String plaintext2 = rsa.decrypt(ciphertext);
		et = System.currentTimeMillis();

		System.out.println("enc time: " + (dt - st));
		System.out.println("dec time: " + (et - dt));
		System.out.println(plaintext2);

		// rsa.saveKeys(keysPath);
	}
}