package crypto1;

import java.io.File;
import java.util.Scanner;

public class PrintDecrypted {
	public PrintDecrypted() throws Exception {
		File file = new File("dane.txt");
		Scanner scanner = new Scanner(file);
		String dane = scanner.useDelimiter("\\A").next();
		scanner.close();

		int lines = dane.split("\n").length;
		dane = dane.replace("\n", " ").replace("\r", "");

		File fileCodes = new File("kod.txt");
		Scanner scannerCodes = new Scanner(fileCodes);
		String daneKody = scannerCodes.useDelimiter("\\A").next();
		scannerCodes.close();
		daneKody = daneKody.replace("\n", " ").replace("\r", "");
		String[] kody = daneKody.split(" ");

		int ileznam = kody.length;

		String[][] tab = new String[ileznam][lines];
		String[] temp = dane.split(" ");

		for (int i = 0; i < lines; i++) {
			for (int j = 0; j < ileznam; j++) {
				tab[j][i] = temp[53 * i + j];
			}
		}

		String tmp = "";
		for (int i = 0; i < lines; i++) {
			for (int j = 0; j < ileznam; j++) {
				char m = (char) Integer.parseInt(XOR(tab[j][i], kody[j]), 2);
				tmp += m;
			}
			tmp += "\n";
		}
		System.out.println(tmp);

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
		new PrintDecrypted();
	}
}
