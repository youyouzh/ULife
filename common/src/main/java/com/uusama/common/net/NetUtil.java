package com.uusama.common.net;

import com.uusama.common.text.CharPool;
import com.uusama.common.util.StrUtil;

import java.net.IDN;
import java.net.InetAddress;

/**
 * 网络相关工具
 *
 * @author uusama
 */
public class NetUtil {

    public final static String LOCAL_IP = "127.0.0.1";

    /**
     * 默认最小端口，1024
     */
    public static final int PORT_RANGE_MIN = 1024;
    /**
     * 默认最大端口，65535
     */
    public static final int PORT_RANGE_MAX = 0xFFFF;

    /**
     * Unicode域名转puny code
     *
     * @param unicode Unicode域名
     * @return puny code
     * @since 4.1.22
     */
    public static String idnToASCII(String unicode) {
        return IDN.toASCII(unicode);
    }

    /**
     * 从多级反向代理中获得第一个非unknown IP地址
     *
     * @param ip 获得的IP地址
     * @return 第一个非unknown IP地址
     * @since 4.4.1
     */
    public static String getMultistageReverseProxyIp(String ip) {
        // 多级反向代理检测
        if (StrUtil.isNotBlank(ip) && ip.indexOf(ip, CharPool.COMMA) > 0) {
            final String[] ips = ip.split(ip, CharPool.COMMA);
            for (final String subIp : ips) {
                if (!isUnknown(subIp)) {
                    ip = subIp;
                    break;
                }
            }
        }
        return ip;
    }

    /**
     * 检测给定字符串是否为未知，多用于检测HTTP请求相关<br>
     *
     * @param checkString 被检测的字符串
     * @return 是否未知
     * @since 5.2.6
     */
    public static boolean isUnknown(String checkString) {
        return StrUtil.isBlank(checkString) || "unknown".equalsIgnoreCase(checkString);
    }

    /**
     * 检测IP地址是否能ping通
     *
     * @param ip IP地址
     * @return 返回是否ping通
     */
    public static boolean ping(String ip) {
        return ping(ip, 200);
    }

    /**
     * 检测IP地址是否能ping通
     *
     * @param ip      IP地址
     * @param timeout 检测超时（毫秒）
     * @return 是否ping通
     */
    public static boolean ping(String ip, int timeout) {
        try {
            // 当返回值是true时，说明host是可用的，false则不可。
            return InetAddress.getByName(ip).isReachable(timeout);
        } catch (Exception ex) {
            return false;
        }
    }
}
