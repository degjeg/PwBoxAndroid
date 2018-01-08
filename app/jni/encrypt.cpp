//
// Created by danger on 16/9/19.
//
#include <string.h>
#include <stdio.h>
#include "math.h"
#include "includes/Log.h"

#include "encrypt.h"
#include <stdlib.h>

#ifdef __cplusplus
extern "C" {
#endif

static const int KEYS[] = {
        0xf9599b30, 0x3e2a3c9c, 0xcb8f18f9, 0x40b24daf, 0x2e01feb8, 0xf9e0b444, 0x323d7dd8,
        0xac5cdd26,
        0x9f6df2d4, 0x464684f7, 0xa4ba8e4a, 0x24480a6f, 0x4bc25d76, 0x6e3cb657, 0xb6b58ebb,
        0xa0ffdcb6,
        0x763c032c, 0x91597a0f, 0x98f22c2d, 0x48bae0a1, 0x78e46ca5, 0x81d27d06, 0x0ef1c75b,
        0x9f217ea4,
        0xa1712073, 0x961e97d0, 0x3843ce21, 0x8d03e293, 0xbc363511, 0xd51d9268, 0x9937c71a,
        0xfdce4878,
        0x47217f28, 0xf54bef1c, 0xfd31f38a, 0x3d34c2a6, 0x9a43e00d, 0xd573f0c8, 0xb58dab7e,
        0x458ccd0e,
        0x67c2788e, 0x6eac5046, 0x5906d832, 0x8fdc41d0, 0x9261c3ee, 0x87885524, 0x235630b2,
        0x470a8315,
        0x8c78df2d, 0x5e644597, 0xf1421cc3, 0xa919ee1b, 0xc93b808b, 0xfff461b0, 0x3d206202,
        0xe11ceb90,
        0x9c2d634f, 0x6eebddcd, 0x80e2ff4e, 0x4bfb3124, 0x867a2bc4, 0x34f49051, 0xaf8cda63,
        0x4e510859,
        0x04190784, 0x73874697, 0xa69123c7, 0x1bfbae9d, 0x8b538df8, 0x4fbff523, 0x426a94e8,
        0xdf832299,
        0x84bba51e, 0x4e48371a, 0x46e5ec86, 0xf1c2f2cb, 0x70146e8d, 0x4725d0f4, 0x4d6d4d4e,
        0x7e38e152,
        0x606e80b6, 0x5be22a6f, 0x105862c7, 0x4f797108, 0x26f7f06b, 0x585af3c6, 0x7c1f7071,
        0x2eb7afd5,
        0x5d04eea5, 0x1edb6a22, 0x1c8daf2b, 0x724ed242, 0x7addfe81, 0xe620ef51, 0x96f19cd5,
        0x0f11744c,
        0x062d078c, 0xcae4e8b5, 0xa1102235, 0xd80b937a, 0xe0b2c83f, 0x61b1197e, 0x2189b31e,
        0xfda19631,
        0xe2826ccc, 0xe062ec1b, 0x1faeccce, 0x50c7f447, 0x3a994e1b, 0x24155eed, 0xc0b37698,
        0x26b454d5,
        0xd9851d0f, 0x3f70883d, 0x8d07a8c2, 0xde223550, 0x3c2afe0c, 0x89d8e570, 0x8494bdb0,
        0x36526dda,
        0xdce658bd, 0xf8e7e978, 0x71fe5141, 0xb28e26d5, 0xf75e6012, 0x363a7e8e, 0x66073278,
        0xd06b13b9,
        0x03dd9688, 0xab396f1d, 0x23214b5e, 0x9c980727, 0xb9e6d2ac, 0x1e4aea01, 0x8039a327,
        0x3ed6343e,
        0xab7787e1, 0x681b95f0, 0xc73fde93, 0x0b34b12c, 0xcb1f5760, 0xdfa0e838, 0xd1fae297,
        0xf9e90f07,
        0xdb112d80, 0xf75db2aa, 0x0cb27d3c, 0xa1791adc, 0xcad26f37, 0xf18d1cd6, 0x9c5038c6,
        0xcd971c08,
        0x2070fbe0, 0xca677c78, 0x1b17bd17, 0x015323c6, 0x6971073d, 0x72f7c131, 0xc6396355,
        0xe2594209,
        0x53300fc0, 0x1b2f6779, 0x8587dfca, 0x0e1db38c, 0xf28a7505, 0xb25226d3, 0x07c4260c,
        0xde555d26,
        0x336572a3, 0x80b1cf43, 0xbf80eb5d, 0x692a2862, 0xaa978324, 0x104609fd, 0x00d16b54,
        0xa880154e,
        0xa3c56f51, 0xba32f15e, 0xdd0d52bc, 0x53991593, 0x516a8db2, 0x890cb41f, 0xca2df4bb,
        0x14b304c6,
        0x53a6f654, 0x91d5b7c7, 0x34e72e36, 0x721c51fc, 0x32d6aecc, 0x0397ee61, 0x01e09b74,
        0xe4f12ea3,
        0x09a1127c, 0x7e51536f, 0xd19a0401, 0x287c568f, 0x0f5ffb14, 0x80f6c41e, 0xcdd020d4,
        0x1953c553,
        0x63b26d5b, 0x1acfa6ba, 0x7ce520b3, 0x63faecd2, 0xcf06ce2f, 0x69a01563, 0xd2228ed6,
        0x7d594115,
        0xce24e3ca, 0xa33e8001, 0x96e07fca, 0xb4ed2d5d, 0x60802746, 0x818cf975, 0xa2f82898,
        0xbe90c67c,
        0xa69f2a62, 0x629da410, 0xd8a64426, 0x8926ce60, 0x5f6d1586, 0x7f4cf148, 0xcb63eadc,
        0x1ddbdcf0,
        0xfd168204, 0x6813a8a8, 0x9882c08a, 0x3210c21a, 0x1c6334a0, 0xdd7dea08, 0x1dc39c88,
        0xf9c0752c,
        0x28927c52, 0xe6d39efe, 0x23ed101e, 0x22852563, 0x69d83949, 0x41520f94, 0xa3d96f26,
        0x05853fc1,
        0x71f4d033, 0x5c1d8e3a, 0x49ca3ef0, 0x79ca7e23, 0x21428dbb, 0xbc0d6efe, 0x2f492f65,
        0xfe125392,
        0x6b373e53, 0xf5e3bdfa, 0x3dbd026e, 0x824849d8, 0xac00fe33, 0x40a5690c, 0x3d5b0596,
        0x12d02456,
};

static const int KEY_LEN = sizeof(KEYS) / sizeof(KEYS[0]);
static const int KEY_LEN2 = KEY_LEN * KEY_LEN;


void move(char *data, int len) {
    char tmp = data[len - 1];

    // 移低五位
    for (int i = len - 1; i > 0; i--) {
        data[i] &= 0xe0; // 清除低五位
        data[i] |= data[i - 1] & 0x1f;
    }

    data[0] &= 0xe0; // 清除低五位
    data[0] |= tmp & 0x1f;

    // 移高五位
    tmp = data[len - 1];
    for (int i = len - 1; i > 0; i--) {
        data[i] &= 0x07; // 清除高五位
        data[i] |= data[i - 1] & 0xf8;
    }
    data[0] &= 0x07; // 清除高五位
    data[0] |= tmp & 0xf8;
}

void moveBack(char *data, int len) {
    char tmp = data[0];

    // 移高五位
    for (int i = 0; i < len - 1; i++) {
        data[i] &= 0x7; //
        data[i] |= data[i + 1] & 0xf8;
    }

    data[len - 1] &= 0x7; //
    data[len - 1] |= tmp & 0xf8;

    // 移低五位
    tmp = data[0];
    for (int i = 0; i < len - 1; i++) {
        data[i] &= 0xe0; // 清除高五位
        data[i] |= data[i + 1] & 0x1f;
    }
    data[len - 1] &= 0xe0; // 清除高五位
    data[len - 1] |= tmp & 0x1f;
}

char *encryptWithKey(char *data, int len, int key) {
    key = (key > 0 ? key : -key) % KEY_LEN2;
    key = len * key % KEY_LEN2;

    int hKey = key / KEY_LEN;
    int lKey = key % KEY_LEN;

    if (hKey == lKey) {
        lKey = (hKey + (KEY_LEN / 4)) % KEY_LEN;
    }
    int step = (key % 3) + 1;

    for (int i = 0; i < len; i++) {
        char d = data[i];
        data[i] = (char) (data[i] ^ ((KEYS[hKey] >> 24) & 0xff));
        data[i] = (char) (data[i] ^ ((KEYS[hKey] >> 16) & 0xff));
        data[i] = (char) (data[i] ^ ((KEYS[hKey] >> 8) & 0xff));
        data[i] = (char) (data[i] ^ KEYS[hKey] & 0xff);

        data[i] = (char) (data[i] ^ ((KEYS[lKey] >> 24) & 0xff));
        data[i] = (char) (data[i] ^ ((KEYS[lKey] >> 16) & 0xff));
        data[i] = (char) (data[i] ^ ((KEYS[lKey] >> 8) & 0xff));
        data[i] = (char) (data[i] ^ KEYS[lKey] & 0xff);

        if (d == data[i]) {
            // System.out.println("sameCount:" + sameCount + "," + hKey + "," + lKey);
            // System.out.println("d:" + d + "," + data[i]);
            i--;
        }

        hKey = (hKey + step) % KEY_LEN;
        lKey = (lKey + step) % KEY_LEN;
    }
    return data;
}


char *decryptWithKey(char *data, int len, int key) {
    return encrypt(data, len, key);
}

char *encrypt(char *data, int len, int key) {
    // System.out.println("encrype ori:" + Arrays.toString(data));
    move(data, len);
    // System.out.println("encrype mov:" + Arrays.toString(data));

    encryptWithKey(data, len, key);
    // System.out.println( "encrype key:" + Arrays.toString(data));
    return data;
}

char *decrypt(char *data, int len, int key) {
    // System.out.println( "decrypt ori:" + Arrays.toString(data));
    // int len = strlen(data);

    encryptWithKey(data, len, key);
    // System.out.println( "decrypt key:" + Arrays.toString(data));

    moveBack(data, len);
    // System.out.println( "decrypt mov:" + Arrays.toString(data));
    return data;
}




void printArray(char *ori, char *d, int k) {

}


void testEncrypt() {

}


#ifdef __cplusplus
}
#endif