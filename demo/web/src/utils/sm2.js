import {sm2} from 'sm-crypto';

export function rsaPublicData(data) {
  const publicKey =
    '0408e226cfdb9b6377516164e0228e8bce5c538d88d138b409a807090caf77b0159e5950df46e7df57fd09fe979cd5bc045a5e05ac3af7c157ebd9ee9693cad8d1';
  const cipherMode = 1;
  return sm2.doEncrypt(data, publicKey, cipherMode);
}
