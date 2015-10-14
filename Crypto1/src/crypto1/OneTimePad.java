package crypto1;

import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class OneTimePad {

	public OneTimePad() throws Exception {
		//53 znaki
		File file = new File("dane.txt");
		Scanner scanner = new Scanner(file);
		String dane = scanner.useDelimiter("\\A").next();
		scanner.close();
		int mesLen = dane.split("\n")[0].split(" ").length;
		dane = dane.replace("\n", " ").replace("\r", "");

		// System.out.println(dane);

		File fileCodes = new File("kod.txt");
		Scanner scannerCodes = new Scanner(fileCodes);
		String daneKody = "";
		String[] knownCode = {};
		try {
			daneKody = scannerCodes.useDelimiter("\\A").next();
			daneKody = daneKody.replace("\n", " ").replace("\r", "");
			knownCode = daneKody.split(" ");
		} catch (NoSuchElementException e) {}
		scannerCodes.close();
		
		int mesCnt = 21;
		int offset = knownCode.length;
		
		String[][] tab = new String[mesLen][mesCnt];
		String[] temp = dane.split(" ");

		for (int i = 0; i < mesCnt; i++) {
			for (int j = 0; j < 8; j++) {
				tab[j][i] = temp[mesLen * i + j + offset];
			}
		}
		String[] knownTab = new String[mesCnt];
		for (int i=0; i < mesCnt; i++) {
			knownTab[i] = "";
		}
		
		for (int i = 0; i < mesCnt; i++) {
			for (int j = 0; j < offset; j++) {
				knownTab[i] += (char) Integer.parseInt(XOR(temp[mesLen * i + j], knownCode[j]), 2);
			}
		}

		String[] kody = new String[8];
		String regexp = "[a-zA-Z0-9 .?,!()@\"'%-+]+";
		
		PrintWriter printWriter = new PrintWriter("result.txt");
		
		for (int q = 0; q < 256; q++) {
			kody[0] = Integer.toBinaryString(q);
			while (kody[0].length() < 8) {
				kody[0] = "0" + kody[0];
			}
			String t0 = "";
			for (int i = 0; i < mesCnt; i++) {
				char m = (char) Integer.parseInt(XOR(tab[0][i], kody[0]), 2);
				t0 += m;
			}
			
			if (!t0.matches(regexp))//"[A-Z?]+"))
				continue;
			
			for (int w = 0; w < 256; w++) {
				kody[1] = Integer.toBinaryString(w);
				while (kody[1].length() < 8) {
					kody[1] = "0" + kody[1];
				}
				String t1 = "";
				for (int i = 0; i < mesCnt; i++) {
					char m = (char) Integer.parseInt(XOR(tab[1][i], kody[1]), 2);
					t1 += m;
				}
				
				if (!t1.matches(regexp))
					continue;
				
				for (int e = 0; e < 256; e++) {
					kody[2] = Integer.toBinaryString(e);
					while (kody[2].length() < 8) {
						kody[2] = "0" + kody[2];
					}
					String t2 = "";
					for (int i = 0; i < mesCnt; i++) {
						char m = (char) Integer.parseInt(XOR(tab[2][i], kody[2]), 2);
						t2 += m;
					}
					
					if (!t2.matches(regexp))
						continue;
					
					for (int r = 0; r < 256; r++) {
						kody[3] = Integer.toBinaryString(r);
						while (kody[3].length() < 8) {
							kody[3] = "0" + kody[3];
						}
						String t3 = "";
						for (int i = 0; i < mesCnt; i++) {
							char m = (char) Integer.parseInt(XOR(tab[3][i], kody[3]), 2);
							t3 += m;
						}
						
						if (!t3.matches(regexp))
							continue;
						
						for (int t = 0; t < 256; t++) {
							kody[4] = Integer.toBinaryString(t);
							while (kody[4].length() < 8) {
								kody[4] = "0" + kody[4];
							}
							String t4 = "";
							for (int i = 0; i < mesCnt; i++) {
								char m = (char) Integer.parseInt(XOR(tab[4][i], kody[4]), 2);
								t4 += m;
							}
							
							if (!t4.matches(regexp)) {
								continue;
							}
							
							for (int y = 0; y < 256; y++) {
								kody[5] = Integer.toBinaryString(y);
								while (kody[5].length() < 8) {
									kody[5] = "0" + kody[5];
								}
								String t5 = "";
								for (int i = 0; i < mesCnt; i++) {
									char m = (char) Integer.parseInt(XOR(tab[5][i], kody[5]), 2);
									t5 += m;
								}
								
								if (!t5.matches(regexp))
									continue;
								
								for (int u = 0; u < 256; u++) {
									kody[6] = Integer.toBinaryString(u);
									while (kody[6].length() < 8) {
										kody[6] = "0" + kody[6];
									}
									String t6 = "";
									for (int i = 0; i < mesCnt; i++) {
										char m = (char) Integer.parseInt(XOR(tab[6][i], kody[6]), 2);
										t6 += m;
									}
									
									if (!t6.matches(regexp))
										continue;
									
									for (int o = 0; o < 256; o++) {
										kody[7] = Integer.toBinaryString(o);
										while (kody[7].length() < 8) {
											kody[7] = "0" + kody[7];
										}
										String t7 = "";
										for (int i = 0; i < mesCnt; i++) {
											char m = (char) Integer.parseInt(XOR(tab[7][i], kody[7]), 2);
											t7 += m;
										}
										
										if (!t7.matches(regexp))
											continue;
										
										String tmp = "";
										for (int i = 0; i < mesCnt; i++) {
											tmp += knownTab[i] + " ";
											for (int j = 0; j < 8; j++) {
												char m = (char) Integer.parseInt(XOR(tab[j][i], kody[j]), 2);
												tmp += m + " ";
											}
											tmp += "\r\n";
										}

										printWriter.println(Arrays.asList(kody));
										printWriter.println(tmp);
//										System.out.println(Arrays.asList(kody));
//										System.out.println(tmp);
									}
								}
							}
						}
					}
				}
			}
		}
		printWriter.close();
	}

	String XOR(String mes, String code) {
		String res = "";
		for (int i = 0; i < mes.length(); i++) {
			if (mes.charAt(i) == code.charAt(i))
				res += "0";
			else
				res += "1";
		}
		return res;
	}

	public static void main(String args[]) throws Exception {
		new OneTimePad();
	}
}
