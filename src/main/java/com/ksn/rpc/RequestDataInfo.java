package com.ksn.rpc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author ksn
 * @version 1.0
 * @date 2022/3/24 17:23
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestDataInfo implements Serializable {

    private String requestId;

    private String className;

    private String methodName;

    private Object[] params;

    private Class<?>[] types;

    private Object result;
}
