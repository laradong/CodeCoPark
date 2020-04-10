package org.laradong.ccp;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class NetUtil {

    /**
     * 查找本机IP地址.
     */
    public static String getLocalIp() {
        String localIp;
        try {
            localIp = findLen().getHostAddress();
        } catch (Exception exception) {
            try {
                localIp = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e1) {
                localIp = "UNKNOWN";
            }
        }
        return localIp;
    }

    private static InetAddress findLen() throws Exception {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        if (interfaces == null) {
            throw new RuntimeException("no LEN address");
        }
        while (interfaces.hasMoreElements()) {
            NetworkInterface in = interfaces.nextElement();
            Enumeration<InetAddress> addresses = in.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress address = addresses.nextElement();
                byte[] addrBytes = address.getAddress();
                int first = addrBytes[0] & 0xFF;
                int second = addrBytes[1] & 0xFF;
                if (first == 10) {
                    return address;
                }
                if (first == 172 && second >= 16 && second <= 31) {
                    return address;
                }
                if (first == 192 && second == 168) {
                    return address;
                }
            }
        }
        throw new RuntimeException("LEN address not found");
    }
}
