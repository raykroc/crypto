#ifndef MUSICPLAYER_HPP_
#define MUSICPLAYER_HPP_

#include <openssl/aes.h>
#include <cstdio>
#include <iostream>
#include <string>
#include <sstream>

#include "Header.hpp"

using namespace std;

string getConf() {
	char conf[512];
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

	FILE *ifp = fopen("conf", "rb");
	encrypt(ckey, ivec, ifp, conf, AES_DECRYPT);
	fclose(ifp);
	return string(conf);
}

void playerMain(string source) {
	string keystore;
	string keyid;
	string pass;
	string pin;

	string s = getConf();
	istringstream iss(s);
	iss >> keystore;
	iss >> keyid;
	iss >> pass;
	iss >> pin;

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

	FILE *iefp = fopen(source.c_str(), "rb");
	char * temp = (char*) "xxx";
	FILE *dfp = fopen(temp, "wb");
	encryptFile(ckey, ivec, iefp, dfp, AES_DECRYPT);
	fclose(iefp);
	fclose(dfp);

	char cmdBuff[64];
	sprintf(cmdBuff, "Open %s Type MPEGVideo Alias theMP3", temp);
	sendCommand(cmdBuff);
	sprintf(cmdBuff, "Play theMP3 Wait");
	sendCommand(cmdBuff);

	remove(temp);
}

#endif
