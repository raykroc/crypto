package crypto3;

import static org.junit.Assert.*;

import java.io.FileReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.junit.Before;
import org.junit.BeforeClass;

public class Test {
	RSA_CRT_THR rsa;
	List<BigInteger> list;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {}

	@Before
	public void setUp() throws Exception {
		list = new ArrayList<>();
		Scanner in = new Scanner(new FileReader("primes2048.txt"));
		while (in.hasNextLine()) {
			String line = in.nextLine();
			list.add(new BigInteger(line));
		}
		in.close();
		
		rsa = new RSA_CRT_THR(8, 2048, list);
	}

	@org.junit.Test
	public void test() {
		String s = "aaaabbbbccccddddeeeeffffgggg";
		System.out.println(Arrays.asList(s));
	}

}
