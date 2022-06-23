package com.xobyx.satfinder.toolnet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {
    public static String getFileMD5(String ppath) {
        StringBuilder v0 = new StringBuilder();
        try {
            MessageDigest v1 = MessageDigest.getInstance("MD5");
            byte[] v5_1 = MD5Utils.getFileBytes(ppath);
            if(v5_1 != null) {
                v1.update(v5_1);
                byte[] v5_2 = v1.digest();
                int v2;
                for(v2 = 0; true; ++v2) {
                    if(v2 >= v5_2.length) {
                        return v0.toString();
                    }

                    byte v3 = v5_2[v2];
                    if(v3 < 0) {
                        v3 &= 0xFF;
                    }

                    if(v3 < 16) {
                        v0.append("0");
                    }

                    v0.append(Integer.toHexString(v3));
                }
            }

            return v0.toString();
        }
        catch(NoSuchAlgorithmException v5) {
            v5.printStackTrace();
        }

        return v0.toString();
    }

    private static byte[] getFileBytes(String arg4) {
        try {
            FileInputStream v4_1 = new FileInputStream(new File(arg4));
            ByteArrayOutputStream v0 = new ByteArrayOutputStream(1000);
            byte[] v1 = new byte[1000];
            while(true) {
                int v2 = v4_1.read(v1);
                if(v2 == -1) {
                    break;
                }

                v0.write(v1, 0, v2);
            }

            v4_1.close();
            v0.close();
            return v0.toByteArray();
        }
        catch(IOException v4) {
            v4.printStackTrace();
            return null;
        }
    }
}

