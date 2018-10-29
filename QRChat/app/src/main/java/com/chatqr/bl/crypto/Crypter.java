package com.chatqr.bl.crypto;

import java.security.spec.KeySpec;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class Crypter {

    public enum DES_ALGORITM{
        DES,
        DESede
    }
    private KeySpec keySpec;
    private SecretKey  key;
    private IvParameterSpec iv;
    private String cipherAlgorithm   /*     = "DESede/CBC/PKCS5Padding"*/;
    private DES_ALGORITM keyFactoryAlgorithm;
    /**Нулевой вектор инициализации*/
    public static   byte[] ZERO_IV = {0,0,0,0,0,0,0,0};

    /**
     *
     * @param keyString 24 байта. если 16 байт, то возьмет первые 8 и добавит их в конец ключа.
     * @param ivString
     * @param keyFactoryAlgorithm
     * @param cipherAlgorithm
     * @throws Exception
     */
    public Crypter(byte[] keyString, byte[] ivString, DES_ALGORITM keyFactoryAlgorithm, String cipherAlgorithm) throws Exception{
        if (keyString.length == 16){
            //if ((keyFactoryAlgorithm==DES_ALGORITM.DESede)&&(keyString.length == 16)){
            byte[] keyVal24 = new byte[24];
            System.arraycopy(keyString, 0, keyVal24, 0, 16);
            System.arraycopy(keyString, 0, keyVal24, 16, 8);
            keyString = keyVal24;
        }
        this.keyFactoryAlgorithm = keyFactoryAlgorithm;
        switch(keyFactoryAlgorithm){
            case DESede:
                keySpec = new DESedeKeySpec(keyString);
                if (!cipherAlgorithm.equals("DESede/ECB/NoPadding")){
                    iv = new IvParameterSpec(ivString);
                }
                break;
            case DES:
                keySpec = new DESKeySpec(keyString);
                break;
        }

        key = SecretKeyFactory.getInstance(keyFactoryAlgorithm.toString()).generateSecret(keySpec);

        this.cipherAlgorithm = cipherAlgorithm;
    }

    public Crypter(byte[] keyString, String cipherAlgorithm) throws Exception{
        this(keyString, null, DES_ALGORITM.DES, cipherAlgorithm);
    }

    public byte[] encrypt(byte[] value) throws Exception {
        try {
            Cipher ecipher =Cipher.getInstance(cipherAlgorithm);

            switch(keyFactoryAlgorithm){
                case DESede:
                    if (cipherAlgorithm.equals("DESede/ECB/NoPadding")){
                        ecipher.init(Cipher.ENCRYPT_MODE, key);
                    }
                    else{
                        ecipher.init(Cipher.ENCRYPT_MODE, key, iv);
                    }
                    break;
                case DES:
                    ecipher.init(Cipher.ENCRYPT_MODE, key);
                    break;
            }

            if(value==null)
                return null;


            byte[] enc = ecipher.doFinal(value);

            return enc;

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public  byte[] decrypt(byte[] value) {
        try {
            Cipher dcipher = Cipher.getInstance(cipherAlgorithm);

            switch(keyFactoryAlgorithm){
                case DESede:
                    dcipher.init(Cipher.DECRYPT_MODE, key, iv);
                    break;
                case DES:
                    dcipher.init(Cipher.DECRYPT_MODE, key);
                    break;
            }

            if(value==null)
                return null;

            // Decrypt
            byte[] utf8 = dcipher.doFinal(value);

            // Decode using utf-8
            return utf8;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * шифрует data используя 3DES NoPadding
     * @param keyString ключ
     * @param ivString вектор инициализации
     * @param data данные для шифрования
     * @return данные зашифрованные алгоритмом 3DES
     */
    public static byte[] TripleDESEncodeNoPadding(byte[] keyString, byte[] ivString, byte[] data) throws Exception{
        //Crypter сrypter = new Crypter(keyString, ivString, "DESede","DESede/CBC/PKCS5Padding");
        Crypter сrypter = new Crypter(keyString, ivString, DES_ALGORITM.DESede,"DESede/CBC/NoPadding");
        return сrypter.encrypt(data);
    }
    public static byte[] TripleDESEncodeECBNoPadding(byte[] keyString, byte[] data) throws Exception{
        //Crypter сrypter = new Crypter(keyString, ivString, "DESede","DESede/CBC/PKCS5Padding");
        Crypter сrypter = new Crypter(keyString, null, DES_ALGORITM.DESede,"DESede/ECB/NoPadding");
        return сrypter.encrypt(data);
    }
    /**
     * дешифрует data используя 3DES NoPadding
     * @param keyString ключ
     * @param ivString вектор инициализации
     * @param data данные для шифрования
     * @return данные зашифрованные алгоритмом 3DES
     */
    public static byte[] TripleDESDecodeNoPadding(byte[] keyString, byte[] ivString, byte[] data) throws Exception{
        //Crypter сrypter = new Crypter(keyString, ivString, "DESede","DESede/CBC/PKCS5Padding");
        Crypter сrypter = new Crypter(keyString, ivString, DES_ALGORITM.DESede,"DESede/CBC/NoPadding");
        return сrypter.decrypt(data);
    }
    public static byte[] TripleDESDecodeNoPadding(byte[] keyString, byte[] data) throws Exception{
        //Crypter сrypter = new Crypter(keyString, ivString, "DESede","DESede/CBC/PKCS5Padding");
        Crypter сrypter = new Crypter(keyString, null, DES_ALGORITM.DES,"DESede");
        return сrypter.decrypt(data);
    }

    /**
     * шифрует data используя 3DES PKCS5Padding
     * @param keyString ключ
     * @param ivString вектор инициализации
     * @param data данные для шифрования
     * @return данные зашифрованные алгоритмом 3DES
     */
    public static byte[] TripleDESEncodePKCS5Padding(byte[] keyString, byte[] ivString, byte[] data) throws Exception{
        Crypter сrypter = new Crypter(keyString, ivString, DES_ALGORITM.DESede,"DESede/CBC/PKCS5Padding");
        return сrypter.encrypt(data);
    }
    /**
     * дешифрует data используя 3DES PKCS5Padding
     * @param keyString ключ
     * @param ivString вектор инициализации
     * @param data данные для шифрования
     * @return данные зашифрованные алгоритмом 3DES
     */
    public static byte[] TripleDESDecodePKCS5Padding(byte[] keyString, byte[] ivString, byte[] data) throws Exception{
        Crypter сrypter = new Crypter(keyString, ivString, DES_ALGORITM.DESede,"DESede/CBC/PKCS5Padding");

        return сrypter.decrypt(data);
    }

    /**
     * шифрует data используя DES/ECB/NoPadding
     * @param keyString ключ
     * @param data данные для шифрования
     * @return данные зашифрованные алгоритмом DES
     */
    public static byte[] DESEncodePKCS5Padding(byte[] keyString, byte[] data) throws Exception{
        Crypter сrypter = new Crypter(keyString, "DES/ECB/PKCS5Padding");
        //Crypter сrypter = new Crypter(keyString, "DES/ECB/NoPadding");
        return сrypter.encrypt(data);
    }

    /**
     * шифрует data используя DES/CBC/NoPadding
     * @param keyString ключ
     * @param data данные для шифрования
     * @return данные зашифрованные алгоритмом DES
     */
    public static byte[] DESEncodeCBCNoPadding(byte[] keyString, byte[] data) throws Exception{
        Crypter сrypter = new Crypter(keyString, "DES/CBC/NoPadding");
        return сrypter.encrypt(data);
    }

    /**
     * дешифрует data используя DES/CBC/NoPadding
     * @param keyString ключ
     * @param data данные для дешифрования
     * @return данные дешифрованные алгоритмом DES
     */
    public static byte[] DESDecoceCBCNoPadding(byte[] keyString, byte[] data) throws Exception{
        Crypter сrypter = new Crypter(keyString, "DES/CBC/NoPadding");
        return сrypter.decrypt(data);
    }

    /**
     * шифрует data используя DES/ECB/NoPadding
     * @param keyString ключ
     * @param data данные для шифрования
     * @return данные зашифрованные алгоритмом DES
     */
    public static byte[] DESEncodeECBNoPadding(byte[] keyString, byte[] data) throws Exception{
        Crypter сrypter = new Crypter(keyString, "DES/ECB/NoPadding");
        return сrypter.encrypt(data);
    }
    /**
     * дешифрует data используя DES/ECB/NoPadding
     * @param keyString ключ
     * @param data данные для шифрования
     * @return данные зашифрованные алгоритмом DES
     */
    public static byte[] DESDecodeECBNoPadding(byte[] keyString, byte[] data) throws Exception{
        Crypter сrypter = new Crypter(keyString, "DES/ECB/NoPadding");
        return сrypter.decrypt(data);
    }

    public static byte[] DESEncode(byte[] keyString, byte[] data) throws Exception{
        Crypter сrypter = new Crypter(keyString, "DES");
        return сrypter.encrypt(data);
    }
    /**
     * дешифрует data используя DES/ECB/NoPadding
     * @param keyString ключ
     * @param data данные для шифрования
     * @return данные зашифрованные алгоритмом DES
     */
    public static byte[] DESDecodePKCS5Padding(byte[] keyString, byte[] data) throws Exception{
        Crypter сrypter = new Crypter(keyString, "DES/ECB/PKCS5Padding");

        return сrypter.decrypt(data);
    }


}
