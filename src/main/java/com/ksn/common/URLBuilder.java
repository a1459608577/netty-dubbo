package com.ksn.common;

import com.ksn.utils.ApplicationContextUtil;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

/**
 * @author ksn
 * @version 1.0
 * @date 2022/3/22 15:47
 * @description: 构建要注册的URL
 */
public class URLBuilder {

    public static Set<URL> build(CommonConfig config, Set<Class<?>> classSet, Boolean flag) {
        Set<URL> list = new HashSet<>();
        for (Class<?> clazz : classSet) {
            URL url = new URL();
            String name = flag ? clazz.getName() : clazz.getInterfaces()[0].getName();
            String type = flag ? GlobalConstants.CONSUMERS_CATEGORY : GlobalConstants.PROVIDERS_CATEGORY;
            // 构建URL
            url.setType(type);
            url.setPort(config.getPort());
            url.setApplicationName(config.getApplicationName());
            String registryAddress = config.getRegistryAddress();
            url.setRegisterAddr(subIpAndPort(registryAddress, true));
            String s = subIpAndPort(registryAddress, false);
            url.setRegisterPort(Integer.parseInt(s));
            url.setInterfaceName(name);
            url.setRegisterName(new StringBuilder().append(type).append(':').append(name).toString());
            url.setServiceAddr(ApplicationContextUtil.getLocalIp());
            url.setServicePort(ApplicationContextUtil.getPort());
            url.setTimestamp(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
            Map<String, String> hashMap = new HashMap<>();
            StringBuilder builder = new StringBuilder();
            Arrays.stream(clazz.getDeclaredMethods()).forEach(item -> {
                builder.append(',').append(item.getName());
            });
            builder.delete(0, 1);
            hashMap.put("methods", builder.toString());
            url.setParams(hashMap);
            list.add(url);
        }
        return list;
    }

    private static String subIpAndPort(String registryAddress, Boolean flag) {
        int i = registryAddress.indexOf(':');
        String registerPort = registryAddress.substring(i+1);
        String registerAddr = registryAddress.substring(0, i);
        return flag ? registerAddr : registerPort;
    }


}
