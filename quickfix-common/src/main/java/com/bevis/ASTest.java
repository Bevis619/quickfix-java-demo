package com.bevis;

import com.bevis.utils.UserKeyAESUtil;

import java.util.UUID;

/**
 * The type As test.
 *
 * @author yanghuadong
 * @date 2019 -04-03
 */
public class ASTest {
    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        String accessKey = UUID.randomUUID().toString();
        String secretKey = UUID.randomUUID().toString();
        String encpAccessKey = UserKeyAESUtil.encrypt(accessKey);
        String encpSecretKey = UserKeyAESUtil.encrypt(secretKey);
        System.out.println("accessKey=" + accessKey);
        System.out.println("secretKey=" + secretKey);
        System.out.println("encpAccessKey=" + encpAccessKey);
        System.out.println("encpSecretKey=" + encpSecretKey);

        System.out.println("accessKey=" + UserKeyAESUtil.decrypt("uDqw9NgOluEx2zs1w0teyXQW9vnMVAkg3wBIQn75+fAoHUvtowpncvghsOdsOBj+"));
        System.out.println("secretKey=" + UserKeyAESUtil.decrypt("NofdyV9GIZQNQP1R42NPIvpxCIUUGaLRD0eXvmFzsxgaqZDSVLjXSbNRDl8zRxzk"));
    }
}
