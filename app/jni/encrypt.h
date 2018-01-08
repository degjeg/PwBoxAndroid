//
// Created by danger on 16/9/19.
//

#ifndef PWBOX_ENCRYPT_H
#define PWBOX_ENCRYPT_H

#define GEN_DATA 1

#ifdef __cplusplus
extern "C" {
#endif

char *encrypt(char *data, int len, int key);

char *decrypt(char *data, int len, int key);

void testEncrypt();


#ifdef __cplusplus
}
#endif

#endif //PWBOX_ENCRYPT_H
