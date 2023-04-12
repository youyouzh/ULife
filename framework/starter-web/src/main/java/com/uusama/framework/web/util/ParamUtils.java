package com.uusama.framework.web.util;

import com.uusama.framework.api.constants.GlobalErrorCodeConstants;
import com.uusama.framework.api.pojo.ErrorCode;
import com.uusama.framework.web.exception.ServiceException;
import com.uusama.framework.web.exception.ServiceExceptionUtil;

/**
 * 参数检查相关工具
 * @author uusama
 */
public class ParamUtils {

    public static void match(boolean condition) {
        if (condition) {
            throw new ServiceException(GlobalErrorCodeConstants.PARAM_INVALID);
        }
    }

    public static void match(boolean condition, ErrorCode errorCode) {
        if (condition) {
            throw ServiceExceptionUtil.exception(errorCode);
        }
    }

    public static void notNull(Object object, String errorMessage) {
        match(object == null);
    }
}
