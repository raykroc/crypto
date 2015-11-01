#ifndef ENCRYPTMP3_HPP_
#define ENCRYPTMP3_HPP_

#include <openssl/aes.h>
#include <cstdio>
#include <iostream>
#include <string>

#include "Header.hpp"

using namespace std;

void encryptMp3(string source, string output) {
	string keystore = "keystore";
	string keyid = "mykey";
	string pass;

	cout << "Enter password for key \'" << keyid << "\':" << endl;
	cin >> pass;

	char key[32];
	getKeyFromKeystore((char*) keystore.c_str(), (char*) keyid.c_str(), (char*) pass.c_str(), key);

	unsigned char ckey[17];
	unsigned char ivec[17];

	for (int i = 0; i < 16; i++) {
		ckey[i] = key[i];
		ivec[i] = key[i + 16];
	}
	ckey[16] = '\0';
	ivec[16] = '\0';

	FILE *ifp = fopen(source.c_str(), "rb");
	FILE *efp = fopen(output.c_str(), "wb");
	encryptFile(ckey, ivec, ifp, efp, AES_ENCRYPT);
	fclose(ifp);
	fclose(efp);
}

#endif
