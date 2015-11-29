package crypto3;

import java.lang.reflect.GenericArrayType;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PrimeGenerator2 {
	static long st;
	static int id = 0;

	SecureRandom rnd;
	int num;
	List<BigInteger> primes = new ArrayList<>();

	public PrimeGenerator2(int num, int bitLength) {
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
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
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
			BigInteger bi = BigInteger.probablePrime(bitLength, rnd);
			while (!isProbablePrime(bi, 40)) {
				bi = BigInteger.probablePrime(bitLength, rnd);
			}
			primes.add(bi);
			// primes.add(BigInteger.probablePrime(bitLength, rnd));
			// System.out.println(id++ + ": time: " +
			// (System.currentTimeMillis() - st) + ": " + bi);
		}
	}

	private static final BigInteger ZERO = BigInteger.ZERO;
	private static final BigInteger ONE = BigInteger.ONE;
	private static final BigInteger TWO = new BigInteger("2");
	private static final BigInteger THREE = new BigInteger("3");

	public static boolean isProbablePrime(BigInteger n, int k) {
		if (n.compareTo(THREE) < 0)
			return true;
		int s = 0;
		BigInteger d = n.subtract(ONE);
		while (d.mod(TWO).equals(ZERO)) {
			s++;
			d = d.divide(TWO);
		}
		for (int i = 0; i < k; i++) {
			BigInteger a = uniformRandom(TWO, n.subtract(ONE));
			BigInteger x = a.modPow(d, n);
			if (x.equals(ONE) || x.equals(n.subtract(ONE)))
				continue;
			int r = 1;
			for (; r < s; r++) {
				x = x.modPow(TWO, n);
				if (x.equals(ONE))
					return false;
				if (x.equals(n.subtract(ONE)))
					break;
			}
			if (r == s) // None of the steps made x equal n-1.
				return false;
		}
		return true;
	}

	private static BigInteger uniformRandom(BigInteger bottom, BigInteger top) {
		Random rnd = new Random();
		BigInteger res;
		do {
			res = new BigInteger(top.bitLength(), rnd);
		} while (res.compareTo(bottom) < 0 || res.compareTo(top) > 0);
		return res;
	}

	public static void main(String[] args) {
//		PrimeGenerator pg = new PrimeGenerator(8, 24);
//		System.out.println(pg.getPrimes());
	}
}
