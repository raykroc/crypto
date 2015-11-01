#ifndef ENCRYPTFILES_HPP_
#define ENCRYPTFILES_HPP_

#include <openssl/aes.h>
#include <cstdio>
#include <iostream>
#include <string>

#include "Header.hpp"

using namespace std;

void encryptConf() {
	unsigned char ckey[17];
	unsigned char ivec[17];
	string skey = "configurationkey";
	string svec = "configurationvec";

	for (int i = 0; i < 16; i++) {
		ckey[i] = skey[i];
		ivec[i] = svec[i];
	}
	ckey[16] = '\0';
	ivec[16] = '\0';

	FILE *ifp = fopen("confPub", "rb");
	FILE *efp = fopen("conf", "wb");
	encryptFile(ckey, ivec, ifp, efp, AES_ENCRYPT);
	fclose(ifp);
	fclose(efp);
}

void encryptKeystore() {
	string keyid;
	string pass;
	cout << "Enter id for key" << endl;
	cin >> keyid;
	cout << "Enter password for key \'" << keyid << "\':" << endl;
	cin >> pass;

	char key[32];
	sha256((char*) keyid.append(pass).c_str(), (char*) key);

	unsigned char ckey[17];
	unsigned char ivec[17];

	for (int i = 0; i < 16; i++) {
		ckey[i] = key[i];
		ivec[i] = key[i + 16];
	}
	ckey[16] = '\0';
	ivec[16] = '\0';

	FILE *ifp = fopen("keystorePub", "rb");
	FILE *efp = fopen("keystore", "wb");
	encryptFile(ckey, ivec, ifp, efp, AES_ENCRYPT);
	fclose(ifp);
	fclose(efp);
}

#endif
