import CryptoJS from 'crypto-js';

const key = '0CoJUm6ywQ8W8Hwn';
const keyHex = CryptoJS.enc.Utf8.parse(key);

// DES加密
export const encryptDES = (message) => {
    if (message) {
        const encrypt = CryptoJS.DES.encrypt(message, keyHex, {
            mode: CryptoJS.mode.ECB,
            padding: CryptoJS.pad.ZeroPadding
        });
        return encrypt.toString();
    } else {
        return '';
    }
};

export const md5 = (message) => {
    if (message) {
        return CryptoJS.MD5(message).toString();
    } else {
        return '';
    }
}


