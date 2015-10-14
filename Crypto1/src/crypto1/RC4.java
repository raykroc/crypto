package crypto1;

import java.io.File;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class RC4 {
	public RC4() throws Exception {
		//72200b70ab0c9c11
		//Ja wiem ,ze ludzie mysla, ze jestem krejzi dziewucha z psami. W Kalifornii psy to sa dzieci ludzi. Wszystkie celebrities maja psy.
		
		File file = new File("daneRC4.txt");
		Scanner scanner = new Scanner(file);
		String dane = scanner.useDelimiter("\\A").next();
		scanner.close();

		dane = dane.replace("\n", " ").replace("\r", "");
		int len = dane.split(" ").length;
		String[] daneTab = dane.split(" ");
		byte[] cipherText = new byte[len];
		for (int i=0; i < len; i++) {
			cipherText[i] = (byte)Integer.parseInt(daneTab[i], 2);
		}

		String Q = "";
		String W = "";
		String E = "";
		String R = "";
		String T = "";
		String Y = "";
		String U = "";
		String I = "";
		
		for (int q = 7; q < 16; q++) {
			for (int w = 2; w < 16; w++) {
				for (int e = 2; e < 16; e++) {
					for (int r = 0; r < 16; r++) {
						for (int t = 0; t < 16; t++) {
							for (int y = 0; y < 16; y++) {
								for (int u = 0; u < 16; u++) {
									for (int i = 0; i < 16; i++) {
										Q = Integer.toHexString(q);
										W = Integer.toHexString(w);
										E = Integer.toHexString(e);
										R = Integer.toHexString(r);
										T = Integer.toHexString(t);
										Y = Integer.toHexString(y);
										U = Integer.toHexString(u);
										I = Integer.toHexString(i);
										
										String key = Q+W+E+R+T+Y+U+I+"ab0c9c11";
										
										SecretKeySpec rc4Key = new SecretKeySpec(key.getBytes("ASCII"), "RC4");
										Cipher rc4Decrypt = Cipher.getInstance("RC4");
										rc4Decrypt.init(Cipher.DECRYPT_MODE, rc4Key);
										String result = new String(rc4Decrypt.update(cipherText), "ASCII");

										if (result.matches("[a-zA-Z0-9 .?,!()@\"'%-+]+")) {
											System.out.println(key);
											System.out.println(result);
											return;
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public static void main(String args[]) throws Exception {
		new RC4();
	}
}
