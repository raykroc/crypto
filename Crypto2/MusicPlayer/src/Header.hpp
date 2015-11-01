#ifndef HEADER_HPP_
#define HEADER_HPP_

#include <crtdefs.h>
#include <openssl/aes.h>
#include <openssl/sha.h>
#include <iostream>
#include <cstdio>
#include <cstdlib>
#include <windows.h>
#include <conio.h>
#include <fstream>

using namespace std;

void encrypt(unsigned char ckey[], unsigned char ivec[], FILE* ifp, char* outBuf, int enc) {
	int bytes_read;
	unsigned char indata[AES_BLOCK_SIZE];
	unsigned char oudata[AES_BLOCK_SIZE];

	AES_KEY key;
	AES_set_encrypt_key(ckey, 128, &key);
	AES_set_decrypt_key(ckey, 128, &key);

	int num = 0;
	char s[32];
	while (strlen(s) < AES_BLOCK_SIZE * 2) {
		bytes_read = fread(indata, 1, AES_BLOCK_SIZE, ifp);
		AES_cfb128_encrypt(indata, oudata, bytes_read, &key, ivec, &num, enc);
		strncat(s, (char*) oudata, 16);
		if (bytes_read < AES_BLOCK_SIZE)
			break;
	}
	strcpy(outBuf, s);
}

void encryptFile(unsigned char ckey[], unsigned char ivec[], FILE* ifp, FILE* efp, int enc) {
	int bytes_read;
	unsigned char indata[AES_BLOCK_SIZE];
	unsigned char oudata[AES_BLOCK_SIZE];

	AES_KEY key;
	AES_set_encrypt_key(ckey, 128, &key);
	AES_set_decrypt_key(ckey, 128, &key);

	int num = 0;
	while (1) {
		bytes_read = fread(indata, 1, AES_BLOCK_SIZE, ifp);
		AES_cfb128_encrypt(indata, oudata, bytes_read, &key, ivec, &num, enc);
		fwrite(oudata, 1, bytes_read, efp);
		if (bytes_read < AES_BLOCK_SIZE)
			break;
	}
}

void sendCommand(char *s) {
	int i = mciSendString(s, NULL, 0, 0);
	if (i) {
		fprintf(stderr, "Error %d when sending %s\n", i, s);
	}
}

void sha256(char * string, char outputBuffer[32]) {
	unsigned char hash[SHA256_DIGEST_LENGTH];
	SHA256_CTX sha256;
	SHA256_Init(&sha256);
	SHA256_Update(&sha256, string, strlen(string));
	SHA256_Final(hash, &sha256);
	sprintf(outputBuffer, "%s", hash);
}

void getKeyFromKeystore(char * keystore, char * keyid, char * pass, char * outputKey) {
	char buf[100];
	strcpy(buf, pass);
	strcat(buf, keyid);
	char sh[32];
	sha256(buf, sh);

	unsigned char ckey[17];
	unsigned char ivec[17];

	for (int i = 0; i < 16; i++) {
		ckey[i] = sh[i];
		ivec[i] = sh[i + 16];
	}
	ckey[16] = '\0';
	ivec[16] = '\0';

	FILE *ifp = fopen(keystore, "rb");
	encrypt(ckey, ivec, ifp, outputKey, AES_DECRYPT);
	fclose(ifp);
}

#endif
